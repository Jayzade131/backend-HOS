package com.org.hosply360.repository.auditRepo;


import com.org.hosply360.audit.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
    // add query helpers if needed
}
