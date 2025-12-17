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
public class IPDFinalBillDTO {

    private String id;
    private String admissionId;
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
    private LocalDateTime settledDate;
    private String remarks;
    private List<String> allBillsId;
    private List<String> surgeriesId;
    private Boolean isSettled = false;


}
