package com.org.hosply360.dao.globalMaster;

import com.org.hosply360.constant.Enums.CreditToEnum;
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
@Document(collection = "billing_item_master")
public class BillingItem extends BaseModel {
    @Id
    private String id;
    @DBRef
    private Organization organization;
    @Field("item_name")
    private String itemName;
    @DBRef
    private BillingItemGroup billingItemGroup;
    @Field("service_code")
    private String serviceCode;
    @Field("percent")
    private Double percentage;
    @DBRef
    private Speciality speciality;
    @Field("credit_to")
    private CreditToEnum creditTo;
    @Field("rate")
    private Double rate;
    @Field("defunct")
    private Boolean defunct = false;


}
