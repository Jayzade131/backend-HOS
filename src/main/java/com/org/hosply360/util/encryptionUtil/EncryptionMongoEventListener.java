

package com.org.hosply360.util.encryptionUtil;

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class EncryptionMongoEventListener extends AbstractMongoEventListener<Object> {

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Object> event) {
        Object source = event.getSource();
        processFields(source, true, new IdentityHashMap<>());
    }

    @Override
    public void onAfterConvert(org.springframework.data.mongodb.core.mapping.event.AfterConvertEvent<Object> event) {
        Object entity = event.getSource();
        if (entity != null) {
            processFields(entity, false, new IdentityHashMap<>());
        }
    }

    private void processFields(Object object, boolean encrypt, Map<Object, Boolean> visited) {
        if (object == null || visited.containsKey(object)) return;

        Class<?> clazz = object.getClass();

        if (clazz.getPackageName().startsWith("java.")) return;

        visited.put(object, true);

        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) continue;

            field.setAccessible(true);

            try {
                Object value = field.get(object);
                if (value == null) continue;

                boolean isEncrypted = field.isAnnotationPresent(EncryptedField.class);

                if (isEncrypted) {
                    if (value instanceof String strVal) {
                        if (strVal.isEmpty()) continue;

                        String processed = encrypt
                                ? EncryptionUtil.encrypt(strVal)
                                : EncryptionUtil.decrypt(strVal);
                        field.set(object, processed);

                    } else if (value instanceof Collection<?> collection) {
                        boolean modified = false;

                        int index = 0;

                        // If it's a List, you can modify items by index; else skip (for sets, etc.)
                        if (collection instanceof List<?> list) {
                            for (Object item : list) {
                                if (item instanceof String str) {
                                    if (!str.isEmpty()) {
                                        String processed = encrypt
                                                ? EncryptionUtil.encrypt(str)
                                                : EncryptionUtil.decrypt(str);
                                        if (!processed.equals(str)) {
                                            // Replace the String in the list
                                            ((List<Object>) list).set(index, processed);
                                            modified = true;
                                        }
                                    }
                                } else {
                                    processFields(item, encrypt, visited);
                                }
                                index++;
                            }
                        } else {
                            // For non-list collections (e.g., Set), just recurse but do not replace items
                            for (Object item : collection) {
                                if (!(item instanceof String)) {
                                    processFields(item, encrypt, visited);
                                }
                            }
                        }

                    } else {
                        processFields(value, encrypt, visited);
                    }

                } else if (!isJavaPrimitiveOrWrapper(field.getType())) {
                    processFields(value, encrypt, visited);
                }

            } catch (Exception e) {
                throw new RuntimeException("Failed to process field: " + field.getName(), e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Collection<Object> createNewCollectionInstance(Collection<?> original) throws Exception {
        if (original instanceof List<?>) return new ArrayList<>();
        if (original instanceof Set<?>) return new HashSet<>();
        return original.getClass().getDeclaredConstructor().newInstance();
    }

    private boolean isJavaPrimitiveOrWrapper(Class<?> type) {
        return type.isPrimitive()
                || type.isEnum()
                || type.getPackageName().startsWith("java.")
                || type.getName().startsWith("jakarta.")
                || type.getName().startsWith("org.bson.");
    }
}