package com.org.hosply360.dao.frontDeskDao;

import com.org.hosply360.constant.Enums.Status;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dao.other.BaseModel;
import com.org.hosply360.util.encryptionUtil.EncryptedField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "patients")
public class Patient extends BaseModel {

    @Id
    private String id;


    @Field("pId")
    @Indexed(unique = true)
    private String pId;

    @Field("organization")
    @DBRef
    private Organization organization;

    @Field("personal_info")
    private PatientPersonalInformation patientPersonalInformation;

    @Field("contact_info")
    private PatientContactInformation patientContactInformation;

    @Field("patientIdentification")
    private List<PatientIdentification> patientIdentification;

    @Field("medical_info")
    private PatientMedicalInformation patientMedicalInformation;


    @Field("emergency_contacts")
    @EncryptedField
    private List<PatientEmergencyContact> patientEmergencyContacts;

    @Field("insurance_info")
    private List<PatientInsuranceDetails> patientInsuranceDetails;

    @Field("demographic_info")
    private PatientDemographicInformation patientDemographicInformation;


    @Field("patientConsents")
    private PatientConsents patientConsents;

    @Field("patientMiscellaneous")
    private PatientMiscellaneous patientMiscellaneous;

    @Field("defunct")
    private boolean defunct;

    @Field("image")
    private byte[] image;

    private Status status;


}