package com.org.hosply360.dao.frontDeskDao;

import com.org.hosply360.util.encryptionUtil.EncryptedField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientMedicalInformation {

    @EncryptedField
    @Field("primary_care_physician")
    private String primaryCarePhysician;


    @EncryptedField
    @Field("known_allergies")
    private List<PatientAllergy> knownAllergies;


    @EncryptedField
    @Field("current_medications")
    private List<PatientMedication> currentPatientMedications;

    @EncryptedField
    @Field("chronic_conditions")
    private List<String> chronicConditions;

    @EncryptedField
    @Field("disabilities")
    private List<String> disabilities;

    @EncryptedField
    @Field("smoking_status")
    private String smokingStatus;

    @EncryptedField
    @Field("alcohol_consumption")
    private String alcoholConsumption;

}
