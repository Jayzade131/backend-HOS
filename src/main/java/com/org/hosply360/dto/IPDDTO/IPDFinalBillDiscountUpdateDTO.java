package com.org.hosply360.dto.IPDDTO;


import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class IPDFinalBillDiscountUpdateDTO {

    private String admissionId;
    private BigDecimal additionalDiscountAmount;
    private String remarks;
}
