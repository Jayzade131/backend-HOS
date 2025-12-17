package com.org.hosply360.dao.frontDeskDao;

import com.org.hosply360.dao.globalMaster.Address;
import com.org.hosply360.util.encryptionUtil.EncryptedField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientContactInformation {

    @EncryptedField
    @Indexed
    @Field("primary_phone")
    private String primaryPhone;

    @EncryptedField
    @Field("secondary_phone")
    private String secondaryPhone;

    @EncryptedField
    @Indexed
    @Field("email")
    private String email;

    @EncryptedField
    @Field("address")
    @DBRef
    private Address address;

    @Field("preferred_contact_method")
    private String preferredContactMethod;


}
