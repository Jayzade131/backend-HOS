package com.org.hosply360.dto.OPDDTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceItemsReqDto {

    private String billingItemGroupId;
    private String billingItemId;
    private Long quantity;
    private Double rate;
    private Double discountPercent;
    private Double discountAmount;
    private Double amount;
}