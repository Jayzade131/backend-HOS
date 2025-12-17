package com.org.hosply360.dao.globalMaster;

import com.org.hosply360.constant.Enums.TemplateStatus;
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

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "template")
public class Template  extends BaseModel {
    @Id
    private String id;
    @DBRef
    private Organization organization;
    @Field("template_name")
    private String templateName;
    @Field("design")
    private String design;
    @Field("template_status")
    private TemplateStatus templateStatus;
    @Field("defunct")
    private boolean defunct;
}
