package com.org.hosply360.dao.frontDeskDao;

import com.org.hosply360.util.encryptionUtil.EncryptedField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientMedication {


    @EncryptedField
    @Field("name")
    private String name;

    @EncryptedField
    @Field("dosage")
    private String dosage;

    @Field("frequency")
    private String frequency;

}
