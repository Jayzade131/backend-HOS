package com.org.hosply360.dao.globalMaster;


import com.org.hosply360.dao.other.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "occupation_master")
public class Occupation extends BaseModel {

    @Id
    private String id;

    @DBRef
    private Organization organization;

    @Field("occupation_code")
    @NotNull(message = "Occupation code must not be null")
    @Indexed(unique = true)
    private String occupationCode;

    @Field("description")
    @NotNull(message = "Description code must not be null")
    private String description;

    @Field("defunct")
    private boolean defunct = false;

}

