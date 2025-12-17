package com.org.hosply360.dao.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Document(collection = "roles")
public class Roles {

    @Id
    private String id;

    @NotBlank(message = "Role name is required")
    @Field("name")
    private String name;

    @Field("description")
    private String description;

    @Field("defunct")
    private boolean defunct = false;
}
