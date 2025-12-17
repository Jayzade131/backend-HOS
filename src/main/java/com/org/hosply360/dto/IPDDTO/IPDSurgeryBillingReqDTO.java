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
public class IPDSurgeryBillingReqDTO {
    private String organizationId;
    private String ipdAdmissionId;
    private String surgeryId;

    private LocalDateTime billingDateTime;

    private List<ParticipantChargeDTO> surgeonDetails;
    private List<ParticipantChargeDTO> anaesthetistDetails;
    private List<ParticipantChargeDTO> pediatricsDetails;
    private BigDecimal surgeryCharges;
    private BigDecimal totalSurgeryExpenses;
    private BigDecimal otInstrumentationCharges;
    private BigDecimal surgeonsOtCharges;
    private BigDecimal surgeonsOtConsumableCharges;
    private BigDecimal anaesthetistsOtCharges;
    private BigDecimal anaesthetistsOtConsumableCharges;
    private BigDecimal pediatricsOtCharges;
    private BigDecimal pediatricsOtConsumableCharges;

    private BigDecimal discountAmount;
}
