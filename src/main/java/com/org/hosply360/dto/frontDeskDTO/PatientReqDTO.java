package com.org.hosply360.dto.frontDeskDTO;

import com.org.hosply360.constant.Enums.Status;
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
public class PatientReqDTO {

        private String id;

        private String pId;

        private String organizationId;

        private PatientPersonalInformationReqDTO personalInformation;

        private PatientContactInformationReqDTO contactInformation;

        private List<PatientIdentificationReqDTO> identification;

        private PatientMedicalInformationReqDTO medicalInformation;

        private List<PatientEmergencyContactReqDTO> emergencyContacts;

        private List<PatientInsuranceDetailsReqDTO> insuranceDetails;

        private PatientDemographicInformationReqDTO demographicInformation;

        private PatientConsentsReqDTO consents;

        private PatientMiscellaneousReqDTO miscellaneous;

        private String image;

        private Status status;

}
