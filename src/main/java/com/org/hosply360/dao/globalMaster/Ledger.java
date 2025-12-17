package com.org.hosply360.dao.globalMaster;

import com.org.hosply360.constant.Enums.Group;
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
@Document(collection = "ledger")
public class Ledger {

    @Id
    private String id;

    @Field("ledger_name")
    private String LedgerName;

    @DBRef
    @Field("address")
    private Address address;

    @Field("group")
    private Group group;

    @Field("mobile")
    private String mobile;

    @Field("email")
    private String email;

    @Field("aadhaar")
    private String aadhaar;

    @Field("pan")
    private String pan;

    @Field("contract_person")
    private String contractPerson;

    @Field("bank_account_name")
    private String bankAccountName;

    @Field("branch")
    private String branch;

    @Field("bank_name")
    private String bankName;

    @Field("bank_account_number")
    private String bankAccountNumber;

    @Field("ifsc_code")
    private String ifscCode;

    @Field("registered_with_gst")
    private Boolean registeredWithGst;

    @Field("gst_number")
    private String gstNumber;

    @DBRef
    private Organization organization;

    @Field("defunct")
    private boolean defunct;
}
