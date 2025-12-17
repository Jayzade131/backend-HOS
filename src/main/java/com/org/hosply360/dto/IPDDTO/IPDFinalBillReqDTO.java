package com.org.hosply360.dto.IPDDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IPDFinalBillReqDTO {
    private String organizationId;
    private String admissionId;
    private BigDecimal additionalDiscountAmount;
    private String remarks;

}
