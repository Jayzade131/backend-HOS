package com.org.hosply360.dto.IPDDTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Setter
@Getter
public class IPDFinancialSummaryDTO {
  private String id;
  private String ipdAdmission;
  private String organizationId;
  private LocalDateTime lastUpdated;
  private BigDecimal totalDepositAmount;
  private BigDecimal totalPaidAmount;
  private BigDecimal totalBilledAmount;
  private BigDecimal totalNetAmount;
  private BigDecimal totalRefundedAmount;
  private BigDecimal pendingAmount;
  private BigDecimal totalDiscountAmount;
  private BigDecimal totalSurchargeAmount;
  private BigDecimal netSurchargeAmount;
  private String remarks;
}
