package com.org.hosply360.dto.IPDDTO;

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
public class IPDBillingItemDTO {
    private String billingItemGroupId;
    private String billingItemGroupName;
    private String billingItemId;
    private String billingItemName;

    private Long quantity;
    private Double rate;
    private Double discountPercent;
    private Double discountAmount;
    private Double amount;
    private Boolean canceled;
    private String cancelReason;
}
