package com.org.hosply360.dto.globalMasterDTO;

import com.org.hosply360.constant.Enums.CreditToEnum;
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
public class BillingItemDTO {
    private String id;
    private OrganizationDTO organizationDTO;
    private String itemName;
    private BillingItemGroupDTO billingItemGroupDTO;
    private String serviceCode;
    private Double percentage;
    private SpecialityDTO specialityDTO;
    private CreditToEnum creditTo;
    private Double rate ;
    private Boolean defunct = false;

}
