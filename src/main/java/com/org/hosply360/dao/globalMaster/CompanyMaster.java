package com.org.hosply360.dao.globalMaster;

import com.org.hosply360.constant.Enums.CompanyMasterStatus;
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
@Document(collection = "company_master")
public class CompanyMaster extends BaseModel {
    @Id
    private String id;
    @DBRef
    private Organization organization;
    @Field("company_name")
    private String companyName;
    @DBRef
    private Tariff tariff;
    @Field("company_master_status")
    private CompanyMasterStatus companyMasterStatus;
    @Field("defunct")
    private Boolean defunct;
}
