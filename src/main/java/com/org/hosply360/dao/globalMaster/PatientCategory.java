package com.org.hosply360.dao.globalMaster;

import com.org.hosply360.constant.Enums.PatientCategoryStatus;
import com.org.hosply360.dao.other.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "patient_category_master")
public class PatientCategory extends BaseModel {
    @Id
    private String id;
    @DBRef
    private Organization organization;
    @Field("category_name")
    private String categoryName;
    @DBRef
    private Tariff tariff;
    @Field("patient_category_status")
    private PatientCategoryStatus patientCategoryStatus;
    @Field("defunct")
    private Boolean defunct;
}
