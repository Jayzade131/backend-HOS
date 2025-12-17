package com.org.hosply360.dto.OPDDTO;

import com.org.hosply360.dto.globalMasterDTO.BillingItemDTO;
import com.org.hosply360.dto.globalMasterDTO.BillingItemGroupDTO;
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
public class InvoiceItemsDto {
    private BillingItemGroupDTO billingItemGroup;
    private BillingItemDTO billingItem;
    private Long quantity;
    private Double rate;
    private Double discountPercent;
    private Double discountAmount;
    private Double amount;

}