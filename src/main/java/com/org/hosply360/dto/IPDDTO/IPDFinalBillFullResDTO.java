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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IPDFinalBillFullResDTO {
/*on the provisional bill we will be having the discou t fields also and as we create the createe final bill it will aslo pass the discout int eh createt api and the bill will be created of the finaldiscout addon the the like that */

    // ------------------- Bill Type -------------------
    private String billType;

    // ------------------- Patient Details -------------------
    private String pId;
    private String patientName;
    private String gender;
    private String age;
    private String mobileNumber;
    private String ipdNo;

    // ------------------- Admission Details -------------------
    private String admissionId;
    private String consultantName;
    private String secondConsultantName;
    private LocalDate admissionDate;
    private String wardName;
    private String bedName;

    // ------------------- Final Bill Details -------------------
    private String finalBillId;
    private String finalBillNo;
    private LocalDate finalBillDate;
    private BigDecimal totalBillAmount;
    private BigDecimal totalSurgeryCharges;
    private BigDecimal totalAdvanceAmount;
    private BigDecimal totalPaidAmount;
    private BigDecimal totalDiscountAmount;
    private BigDecimal totalRefundAmount;
    private BigDecimal netPayableAmount;
    private BigDecimal balanceAmount;
    private BigDecimal refundBalance;
    private Boolean hasSettled;
    private String remarks;
    private LocalDateTime settledDate;

    // ------------------- Expense Summary -------------------
    private List<IPDBillingDTO> billingSummary;
    private List<IPDSurgeryBillingDTO> surgeries;
}
