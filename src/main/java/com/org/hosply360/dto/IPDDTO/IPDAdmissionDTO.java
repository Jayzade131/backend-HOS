package com.org.hosply360.dto.IPDDTO;

import com.org.hosply360.constant.Enums.IpdStatus;
import com.org.hosply360.constant.Enums.PatientCaseType;
import com.org.hosply360.constant.Enums.PatientType;
import com.org.hosply360.dto.frontDeskDTO.DoctorInfoDTO;
import com.org.hosply360.dto.frontDeskDTO.PatientInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IPDAdmissionDTO {

    private String id;
    private String orgId;
    private PatientInfoDTO patient;
    private String wardMaster;
    private String bedMaster;
    private String wardName;
    private String bedNo;
    private LocalDateTime admitDateTime;
    private DoctorInfoDTO primaryConsultant;
    private DoctorInfoDTO secondaryConsultant;
    private LocalDateTime dischargeDateTime;  // (Optional)
    private PatientType patientType;
    private String regular;
    private CorporateDetailsDTO corporateDetails;  // If Corporate
    private InsuranceDetailsDTO insuranceDetails;  // If Insurance
    private String diagnosis;
    private PatientCaseType isPatient;  // MLC, NON_MLC (Required)
    private String departmentId;
    private String  refBy;
    private String ipdNo;
    private String regMrdNo;
    private IpdStatus ipdStatus;
    private String remarks;
    private Boolean defunct = false;



}
