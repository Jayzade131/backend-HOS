package com.org.hosply360.dto.frontDeskDTO;

import com.org.hosply360.constant.Enums.Status;
import com.org.hosply360.dto.globalMasterDTO.OrganizationDTO;
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
public class PatientDTO {

    private String id;

    private String pId;

    private OrganizationDTO organization;

    private PatientPersonalInformationDTO personalInformation;

    private PatientContactInformationDTO contactInformation;

    private List<PatientIdentificationDTO> identification;

    private PatientMedicalInformationDTO medicalInformation;

    private List<PatientEmergencyContactDTO> emergencyContacts;

    private List<PatientInsuranceDetailsDTO> insuranceDetails;

    private PatientDemographicInformationDTO demographicInformation;

    private PatientConsentsDTO consents;

    private PatientMiscellaneousDTO miscellaneous;

    private byte[] image;

    private Status status;
}
