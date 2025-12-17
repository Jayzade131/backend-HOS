package com.org.hosply360.dto.globalMasterDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageEDTO {
    private String id;
    private OrganizationDTO organizationDTO;
    private String packageName;
    private BillingItemGroupDTO billingItemGroupDTO;
    private List<TestDTO> testName;
    private Double totalAmount;
    private Boolean defunct;



}
