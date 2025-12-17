package com.org.hosply360.dao.globalMaster;

import com.org.hosply360.constant.Enums.IPDBill;
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
@Document(collection = "billing_item_group_master")
public class BillingItemGroup extends BaseModel {

    @Id
    private String id;
    @DBRef
    private Organization organization;
    @Field("item_group_name")
    private String itemGroupName;
    @Field("include_in_ipd_bill")
    private IPDBill includeInIPDBill;
    @Field("defunct")
    private Boolean defunct;



}
