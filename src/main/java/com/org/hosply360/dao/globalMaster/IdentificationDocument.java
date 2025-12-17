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

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "identification_documents_master")
public class IdentificationDocument extends BaseModel
{
    @Id
    private String id;

    @DBRef
    private Organization organization;

    @NotBlank(message = "Document code is required")
    @Indexed(unique = true)
    @Field("document_code")
    private String code;

    @NotBlank(message = "Description is required")
    @Field("document_description")
    private String description;

    @Field("document_limit")
    private Long limit;

    @Field("defunct")
    private Boolean defunct = false;
}
