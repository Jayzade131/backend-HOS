package com.org.hosply360.controller.globalMaster;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingItemInfoDTO {
    private String id;
    private String itemName;
    private Double rate ;
}
