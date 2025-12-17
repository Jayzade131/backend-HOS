package com.org.hosply360.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.hosply360.helper.CustomUserDetails;
import com.org.hosply360.repository.auditRepo.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

@Component
@RequiredArgsConstructor
public class AuditMongoEventListener extends AbstractMongoEventListener<Object> {

    private final AuditLogRepository auditLogRepository;
    private final MongoOperations mongoOps;
    private final ObjectMapper objectMapper;

    // Thread-local to hold temporary audit data between before and after save
    private final ThreadLocal<AuditLog> auditThreadLocal = new ThreadLocal<>();

    /**
     * Skip auditing AuditLog itself to prevent recursion
     */
    private boolean isAuditEntity(Object source) {
        return source instanceof AuditLog || (source != null && source.getClass().equals(AuditLog.class));
    }

    /**
     * Get the currently logged-in user safely
     */
//    private String currentUser() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth == null) return "anonymous";
//        return auth.getName() == null ? "anonymous" : auth.getName();
//    }


    private Map<String, String> currentUserInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("id", "anonymous");
        info.put("name", "anonymous");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            return info;
        }

        Object principal = auth.getPrincipal();

        // If you have a custom user details class, e.g. CustomUserDetails
        if (principal instanceof CustomUserDetails customUser) {
            info.put("id", String.valueOf(customUser.getUserId()));
            info.put("name", customUser.getUsername());
        }
        // Spring Security default case
        else if (principal instanceof org.springframework.security.core.userdetails.User user) {
            info.put("name", user.getUsername());
        }
        // Fallback if it's a raw string
        else if (principal instanceof String str) {
            info.put("name", str);
        }

        return info;
    }




    /**
     * Get the actual collection name where the operation is performed
     */
    private String getSourceCollectionName(Class<?> entityClass) {
        return mongoOps.getCollectionName(entityClass);
    }

    /**
     * Decide which audit collection to use based on the actual Mongo collection name
     */
    private String resolveAuditCollectionName(Class<?> entityClass) {
        String collectionName = getSourceCollectionName(entityClass).toLowerCase();

        if (collectionName.contains("appointment")) {
            return "appointment_audits";
        } else if (collectionName.equals("doctor_master") || collectionName.equals("doc_appointment_timetable")) {
            return "doctor_audits";

        } else if (collectionName.equals("patients")) {
            return "patient_audits";
        } else if (collectionName.contains("master") || collectionName.contains("validation")) {
            return "global_master_audits";
        } else if (collectionName.equals("opd_invoice")) {
            return "opd_invoice_audits";
        } else if (collectionName.equals("opd_receipts")) {
            return "opd_receipts_audits";
        } else if (collectionName.equals("prescription")) {
            return "prescription_audits";
        } else if (collectionName.equals("users") || collectionName.equals("roles") || collectionName.equals("permissions")) {
            return "user_management_audits";

        } else if (collectionName.equals("lab_tests") || collectionName.equals("lab_results")) {
            return "lab_audits";

        }else {

            return collectionName + "_audits";
        }
    }

    /**
     * Called before saving (CREATE or UPDATE)
     */
    @Override
    public void onBeforeSave(BeforeSaveEvent<Object> event) {
        Object source = event.getSource();
        if (source == null || isAuditEntity(source)) return;

        Document document = event.getDocument();
        if (document == null) return;

        Class<?> entityClass = source.getClass();
        String entityName = entityClass.getName();
        String sourceCollectionName = getSourceCollectionName(entityClass);

        // Try to extract the _id (could be null before insert)
        Object idValue = document.get("_id");
        if (idValue == null) idValue = document.get("id");
        String idStr = idValue == null ? null : idValue.toString();

        // Check if the document already exists
        boolean exists = false;
        if (idValue != null) {
            exists = mongoOps.getCollection(sourceCollectionName)
                    .find(new Document("_id", idValue)).first() != null;
        }

        String operation = exists ? "UPDATE" : "CREATE";
        Map<String, AuditLog.ChangedValue> changed = null;

        // Detect changed fields for updates
        if ("UPDATE".equals(operation)) {
            Object existing = mongoOps.findById(idValue, entityClass);
            if (existing != null) {
                Map<String, Object> existingMap = objectMapper.convertValue(existing, Map.class);
                Map<String, Object> newMap = objectMapper.convertValue(source, Map.class);

                changed = new LinkedHashMap<>();
                for (String key : newMap.keySet()) {
                    Object oldVal = existingMap.get(key);
                    Object newVal = newMap.get(key);
                    if (!Objects.equals(oldVal, newVal)) {
                        changed.put(key, new AuditLog.ChangedValue(oldVal, newVal));
                    }
                }

                // Include removed fields
                for (String key : existingMap.keySet()) {
                    if (!newMap.containsKey(key)) {
                        Object oldVal = existingMap.get(key);
                        changed.putIfAbsent(key, new AuditLog.ChangedValue(oldVal, null));
                    }
                }
            }
        }

        Map<String, String> userInfo = currentUserInfo();

        AuditLog audit = AuditLog.builder()
                .entityName(entityName)
                .entityId(idStr)
                .operation(operation)
                .timestamp(Instant.now())
                .performedBy(userInfo.get("name"))       // ✅ username
                .performedById(userInfo.get("id"))
                .changedFields(changed)
                .databaseName(((org.springframework.data.mongodb.core.MongoTemplate) mongoOps).getDb().getName())
                .collectionName(sourceCollectionName) // Store the source collection name here
                .build();

        auditThreadLocal.set(audit);
    }

    /**
     * Called after saving (CREATE/UPDATE complete)
     */
    @Override
    public void onAfterSave(AfterSaveEvent<Object> event) {
        Object source = event.getSource();
        if (source == null || isAuditEntity(source)) return;

        AuditLog pending = auditThreadLocal.get();
        if (pending != null) {
            try {
                // Extract generated _id after save
                Object idValue = null;
                if (event.getDocument() != null) {
                    idValue = event.getDocument().get("_id");
                } else {
                    Object mongoObj = mongoOps.getConverter().convertToMongoType(source);
                    if (mongoObj instanceof Document doc) {
                        idValue = doc.get("_id");
                    }
                }

                if (idValue instanceof ObjectId objectId) {
                    pending.setEntityId(objectId.toHexString());
                } else if (idValue != null) {
                    pending.setEntityId(idValue.toString());
                }

                // Update collection name in case it wasn't set properly in beforeSave
                String sourceCollectionName = getSourceCollectionName(source.getClass());
                pending.setCollectionName(sourceCollectionName);

                // Resolve audit collection based on real Mongo collection name
                String auditCollection = resolveAuditCollectionName(source.getClass());

                mongoOps.save(pending, auditCollection);

            } catch (Exception e) {
                System.err.println("Audit logging failed (after save): " + e.getMessage());
            } finally {
                auditThreadLocal.remove();
            }
        }
    }

    /**
     * Called before deletion
     */
    @Override
    public void onBeforeDelete(BeforeDeleteEvent<Object> event) {
        Document source = event.getSource();
        if (source == null) return;

        Object idValue = source.get("_id");
        String idStr = null;
        if (idValue instanceof ObjectId objectId) {
            idStr = objectId.toHexString();
        } else if (idValue != null) {
            idStr = idValue.toString();
        }

        Class<?> domainType = event.getType();
        if (domainType == null || domainType.equals(AuditLog.class)) return;

        String entityName = domainType.getName();
        String sourceCollectionName = getSourceCollectionName(domainType);
        String auditCollection = resolveAuditCollectionName(domainType);


        Map<String, String> userInfo = currentUserInfo();
        AuditLog audit = AuditLog.builder()
                .entityName(entityName)
                .entityId(idStr)
                .operation("DELETE")
                .timestamp(Instant.now())
                .performedBy(userInfo.get("name"))
                .performedById(userInfo.get("id"))
                .databaseName(((org.springframework.data.mongodb.core.MongoTemplate) mongoOps).getDb().getName())
                .collectionName(sourceCollectionName) // Store the source collection name
                .build();

        try {
            mongoOps.save(audit, auditCollection);
        } catch (Exception e) {
            System.err.println("Audit logging failed for delete: " + e.getMessage());
        }
    }
}