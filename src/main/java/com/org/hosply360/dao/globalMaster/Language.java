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
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "language_master")
public class Language extends BaseModel implements Serializable {

    @Id
    private String id;

    @DBRef
    private Organization organization;

    @NotNull(message = "field code must not be null")
    @Indexed(unique = true)
    @Field("code")
    private String code;

    @Field("description")
    @NotNull(message = "Description code must not be null")
    private String description; // Full name of the language

    @Field("defunct")
    private Boolean defunct = false;

}
