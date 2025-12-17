package com.org.hosply360.dto.IPDDTO;

import com.org.hosply360.constant.Enums.IpdStatus;
import com.org.hosply360.constant.Enums.PatientCaseType;
import com.org.hosply360.constant.Enums.PatientType;
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
public class IPDAdmissionReqDTO {

    private String id;

    private String orgId;

    private String patientId;

    private String wardMasterId;

    private String bedMasterId;

    private LocalDateTime admitDateTime;

    private LocalDateTime dischargeDateTime;

    private String primaryConsultantId;

    private String secondaryConsultantId;


    private PatientType patientType;

    private String patientCategoryId;

    private CorporateDetailsReqDTO corporateDetails;

    private InsuranceDetailsReqDTO insuranceDetails;

    private String diagnosis;

    private PatientCaseType isPatient;

    private String department;

    private String refBy;

    private String regMrdNo;

    private IpdStatus ipdStatus;

    private String remarks;

    private Boolean defunct = false;


}
