package com.org.hosply360.dto.IPDDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IPDBillingReqDTO {

    private String organizationId;
    private String admissionId;
    private LocalDate billingDate;
    private List<IPDBillingItemDTO> billingItems;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal netAmount;
    private BigDecimal balanceAmount;
    private BigDecimal refundAmount;
    private String remarks;
    private Boolean isSettled;
}
