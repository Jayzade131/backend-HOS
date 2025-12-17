package com.org.hosply360.dao.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document("module_access_mapping")
public class ModuleAccessMapping {

    @Id
    private String id;

    @DBRef
    private Modules modules;

    @DBRef(lazy = false)
    private Access access;
}
