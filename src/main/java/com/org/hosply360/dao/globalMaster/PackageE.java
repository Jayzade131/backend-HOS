package com.org.hosply360.dao.globalMaster;

import com.org.hosply360.dao.other.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "package_master")
public class PackageE extends BaseModel {
    @Id
    private String id;
    @DBRef
    private Organization organization;
    @Field("package_name")
    private String packageName;
   @DBRef
    private BillingItemGroup billingItemGroup;
   @DBRef
    private List<Test> testName;
    @Field("total_amount")
    private Double totalAmount;
    @Field("defunct")
    private Boolean defunct;
}