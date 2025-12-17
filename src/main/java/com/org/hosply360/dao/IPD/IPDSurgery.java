package com.org.hosply360.dao.IPD;


import com.org.hosply360.dao.frontDeskDao.Doctor;
import com.org.hosply360.dao.other.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ipd_surgery")
public class IPDSurgery extends BaseModel {

    @Id
    private String id;
    private String orgId;
    private String ipdAdmissionId;
    @DBRef
    private Doctor consultant;
    @DBRef
    private Doctor secondConsultant;
    private LocalDate date;
    private String time;
    private String startTime;
    private String endTime;
    private String typeOfSurgery;
    private BigDecimal totalSurgeryExpense;
    private BigDecimal surgeryCharge;


    private List<ParticipantCharge> surgeons;
    private List<ParticipantCharge> anaesthetists;
    private List<ParticipantCharge> pediatrics;

    private BigDecimal surgeonsTotal;
    private BigDecimal surgeonsOtCharges;
    private BigDecimal surgeonsOtConsumableCharges;

    private BigDecimal anaesthetistsTotal;
    private BigDecimal anaesthetistsOtCharges;
    private BigDecimal anaesthetistsOtConsumableCharges;

    private BigDecimal pediatricsTotal;
    private BigDecimal pediatricsOtCharges;
    private BigDecimal pediatricsOtConsumableCharges;



    private BigDecimal otConsumableCharges;


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
    private Boolean defunct = false;
    private Boolean hasCancelled;
    private String cancelledReason;
    private LocalDateTime cancelDateTime;
}
