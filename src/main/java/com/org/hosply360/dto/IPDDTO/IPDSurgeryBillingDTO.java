package com.org.hosply360.dto.IPDDTO;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IPDSurgeryBillingDTO {
    private String id;
    private String organizationId;
    private String ipdAdmissionId;
    private String surgeryId;
    private String ipdSurgeryBillNo;
    private LocalDateTime billingDateTime;
    private List<ParticipantChargeDTO> surgeonDetails;
    private List<ParticipantChargeDTO> anaesthetistDetails;
    private List<ParticipantChargeDTO> pediatricsDetails;
    private BigDecimal totalSurgeryAmount;
    private BigDecimal paidAmount;
    private BigDecimal balanceAmount;
    private Boolean hasCancelled;
    private Boolean hasSettled;
}
