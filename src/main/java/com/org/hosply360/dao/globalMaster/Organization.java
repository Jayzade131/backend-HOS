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

import java.io.Serializable;

@Document(collection = "organization_master")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Organization extends BaseModel implements Serializable {

    @Id
    private String id;

    @DBRef
    private Organization parentOrganization;

    @Field("organization_code")
    private String organizationCode;

    @Field("organization_name")
    private String organizationName;

    @Field("organization_quote")
    private String organizationQuote;

    @Field("organization_desc")
    private String organizationDesc;

    @DBRef
    @Field("address")
    private Address address;

    @Field("email")
    private String email;

    @Field("phone_number")
    private String phoneNumber;

    @Field("website")
    private String website;

    @Field("registration_number")
    private String registrationNumber;

    @Field("org_logo")
    private DocumentInfo orgLogo;

    @Field("defunct")
    private boolean defunct;

    @Field("gst_no")
    private String gstNo;

    @Field("pan_no")
    private String panNo;

    @Field("tan_no")
    private String tanNo;

}
