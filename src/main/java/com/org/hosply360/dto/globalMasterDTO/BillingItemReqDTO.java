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
public class BillingItemReqDTO {
    private String id;
    private String organization;
    private String itemName;
    private String itemGroupId;
    private String serviceCode;
    private Double percentage;
    private String departmentId;
    private Double rate;
    private CreditToEnum creditTo;
    private Boolean defunct = false;

}
