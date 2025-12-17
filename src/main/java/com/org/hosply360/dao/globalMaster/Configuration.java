package com.org.hosply360.dao.globalMaster;

import com.org.hosply360.constant.Enums.OPD_Vital;
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
@Document(collection = "configuration_master")
public class Configuration extends BaseModel {
    @Id
    private String id;
    @DBRef
    private Organization organization;
    @Field("opd_vital")
    private OPD_Vital opdVital;
    @DBRef
    private BillingItem billingItem;
    @Field("ipd_admission_print_format")
    private String ipdAdmissionPrintFormat;
    @Field("ipd_no_format")
    private String ipdNoFormat;
    @Field("medicine_master")
    private String medicineMaster;
    @Field("surgeon")
    private Double surgeon;
    @Field("anasthesist")
    private Double anasthesist;
    @Field("ot_charges")
    private Double otCharges;
    @Field("ot_consumable")
    private Double otConsumable;
    @Field("defunct")
    private Boolean defunct;

}
