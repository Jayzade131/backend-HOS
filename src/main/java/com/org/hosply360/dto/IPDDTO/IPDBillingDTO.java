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
public class IPDBillingDTO {
    private String id;
    private String organizationId;
    private String admissionId;
    private String billingNo;
    private LocalDate billingDate;
    private List<IPDBillingItemDTO> billingItems;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal netAmount;
    private BigDecimal balanceAmount;
    private BigDecimal refundAmount;
    private String remarks;
    private Boolean canceled;
    private String cancelReason;
    private LocalDateTime cancelDate;
    private String canceledBy;
    private Boolean isSettled;
    private Boolean isAdvanceAdjusted;


}
