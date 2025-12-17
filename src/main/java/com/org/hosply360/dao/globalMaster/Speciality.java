package com.org.hosply360.dao.globalMaster;

import com.org.hosply360.dao.other.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "speciality_department_master")
public class Speciality extends BaseModel implements Serializable {

    @Id
    private String id;

    @DBRef
    private Organization organization;

    @Field("description")
    @NotNull(message = "description must not be null")
    private String description;

    @Field("department")
    @NotNull(message = "department must not be null")
    private String department;

    @Field("type")
    @NotNull(message = "type must not be null")
    private String type;

    @Field("defunct")
    private boolean defunct = false;

    @Field("master_type")
    @NotNull(message = "type must not be null")
    private String masterType;

}
