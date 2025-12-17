package com.org.hosply360.dto.IPDDTO;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IPDFinalBillSummaryDTO {

    private String finalBillId;
    private String finalBillNo;
    private LocalDate finalBillDate;

    // Patient Info
    private String patientName;
    private String patientId;
    private String gender;
    private String mobileNumber;
    private String ipdNo;

    // Bill Summary
    private BigDecimal totalBillAmount;
    private BigDecimal totalSurgeryCharges;
    private BigDecimal totalAdvanceAmount;
    private BigDecimal totalPaidAmount;
    private BigDecimal totalDiscountAmount;
    private BigDecimal totalRefundAmount;
    private BigDecimal netPayableAmount;
    private BigDecimal balanceAmount;
    private Boolean hasSettled;
}
