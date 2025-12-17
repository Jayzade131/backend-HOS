package com.org.hosply360.dao.auth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "modules")
public class Modules {

    @Id
    private String id;

    @NotBlank(message = "Module name is required")
    @Indexed(unique = true)
    @Field("module_name")
    private String moduleName;

    @Field("defunct")
    private Boolean defunct = false;
}
