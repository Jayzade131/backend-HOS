package com.org.hosply360.dao.frontDeskDao;


import com.org.hosply360.util.encryptionUtil.EncryptedField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientEmergencyContact {

    @Indexed
    @Field("name")
    @EncryptedField
    private String name;

    @Field("relationship")
    @EncryptedField
    private String relationship;

    @Field("phone")
    @EncryptedField
    private String phone;

    @Field("is_primary")
    private boolean isPrimary;


}
