package com.org.hosply360.dto.frontDeskDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PatientMedicalInformationReqDTO {
    private String primaryCarePhysician;

    private List<PatientAllergyDTO> knownAllergies;

    private List<PatientMedicationDTO> currentPatientMedications;

    private List<String> chronicConditions;

    private List<String> disabilities;

    private String smokingStatus;

    private String alcoholConsumption;
}
