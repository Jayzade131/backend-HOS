package com.org.hosply360.audit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    private String entityName;
    private String entityId;
    private String operation;
    private Instant timestamp;
    private String performedBy;
    private String performedById;
    private Map<String, ChangedValue> changedFields;
    private String databaseName;
    private String collectionName;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChangedValue {
        private Object oldValue;
        private Object newValue;
    }
}
