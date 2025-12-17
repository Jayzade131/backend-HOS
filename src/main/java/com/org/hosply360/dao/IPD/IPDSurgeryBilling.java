package com.org.hosply360.dao.IPD;

import com.org.hosply360.dao.other.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "ipd_surgery_billing")
public class IPDSurgeryBilling extends BaseModel {

    @Id
    private String id;
    private String ipdAdmissionId;
    private String organizationId;
    private String surgeryId;
    private LocalDateTime billingDateTime;
    private String ipdSurgeryBillNo;

    private List<ParticipantCharge> surgeonDetails;
    private List<ParticipantCharge> anaesthetistDetails;
    private List<ParticipantCharge> pediatricsDetails;
    private BigDecimal surgeryCharges;

    private BigDecimal surgeonsTotal;
    private BigDecimal surgeonsOtCharges;
    private BigDecimal surgeonsOtConsumableCharges;

    private BigDecimal anaesthetistTotal;
    private BigDecimal anaesthetistOtCharges;
    private BigDecimal anaesthetistOtConsumableCharges;

    private BigDecimal pediatricsTotal;
    private BigDecimal pediatricsOtCharges;
    private BigDecimal pediatricsOtConsumableCharges;

    private BigDecimal otInstrumentationCharges;

    private String cancelReason;
    private LocalDateTime cancelDateTime;

    private BigDecimal totalSurgeryAmount;
    private BigDecimal paidAmount;
    private BigDecimal balanceAmount;
    private BigDecimal refundAmount;
    private Boolean hasCancelled;
    private Boolean hasSettled;

}
