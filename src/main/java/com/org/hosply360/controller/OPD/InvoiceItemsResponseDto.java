package com.org.hosply360.controller.OPD;

import com.org.hosply360.controller.globalMaster.BillingItemInfoDTO;
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
public class InvoiceItemsResponseDto {

        private String billingItemGroupName;
        private BillingItemInfoDTO billingItem;
        private Long quantity;
        private Double rate;
        private Double amount;


}
