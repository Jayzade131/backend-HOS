package com.org.hosply360.dto.IPDDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Builder
public class IPDSurgeryBillCancelDTO {
    private String organizationId;
    private String ipdAdmissionId;
    private String surgeryId;
    private String cancelReason;
}
