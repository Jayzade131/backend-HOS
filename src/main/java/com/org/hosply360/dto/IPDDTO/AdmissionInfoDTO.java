package com.org.hosply360.dto.IPDDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdmissionInfoDTO {

    private String ipdId;
    private String ipdNo;
    private String regMrdNo;
    private String admitDateTime;
    private String dischargeDateTime;
    private String department;
    private String patientType;
    private String referredBy;
    private String ipdStatus;
}
