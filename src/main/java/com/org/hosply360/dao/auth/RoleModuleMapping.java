package com.org.hosply360.dao.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document("role_module_mapping")
public class RoleModuleMapping {

    @Id
    private String id;

    @DBRef
    private Roles roles;

    @DBRef(lazy = false)
    private Modules modules;
}
