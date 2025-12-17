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
public class PatientAllergy {


    @EncryptedField
    @Field("allergen")
    private String allergen;

    @EncryptedField
    @Field("reaction")
    private String reaction;


    @Field("severity")
    private String severity;

}
