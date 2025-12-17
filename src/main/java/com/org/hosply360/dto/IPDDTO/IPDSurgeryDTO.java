package com.org.hosply360.dto.IPDDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IPDSurgeryDTO {

    private String id;
    private String orgId;
    private String ipdAdmissionId;

    private String consultantId;
    private String consultantName;
    private String secondConsultantId;
    private String secondConsultantName;

    private LocalDate date;
    private String time;
    private String startTime;
    private String endTime;
    private String typeOfSurgery;
    private BigDecimal totalSurgeryExpense;
    private BigDecimal surgeryCharge;


    private List<ParticipantChargeDTO> surgeons;
    private List<ParticipantChargeDTO> anaesthetists;
    private List<ParticipantChargeDTO> pediatrics;


    private BigDecimal surgeonsTotal;
    private BigDecimal surgeonsOtCharges;
    private BigDecimal surgeonsOtConsumableCharges;

    private BigDecimal anaesthetistsTotal;
    private BigDecimal anaesthetistsOtCharges;
    private BigDecimal anaesthetistsOtConsumableCharges;

    private BigDecimal pediatricsTotal;
    private BigDecimal pediatricsOtCharges;
    private BigDecimal pediatricsOtConsumableCharges;

    private BigDecimal otInstrumentationCharges;
    private String otInstrumentationRemark;

    private String assistantSurgeon;
    private String assistantStaff;
    private String anaesthesia;
    private String OT;
    private Boolean isEmergency;

    private String diagnosisDetail;
    private String procedureDetail;
    private String surgeonRemark;
    private String anaesthetistRemark;
    private String pediatricsRemark;
    private String physicianRemark;
    private String otherRemark;
    private Boolean hasCancelled;
    private String cancelledReason;
    private LocalDateTime cancelDateTime;

    private Boolean defunct;
}
