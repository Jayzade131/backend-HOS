package com.org.hosply360.dto.globalMasterDTO;

import com.org.hosply360.constant.Enums.CompanyMasterStatus;
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
public class CompanyMasterDTO {
    private String id;
    private OrganizationDTO organizationDTO;
    private String companyName;
    private TariffDTO tariffDTO;
    private CompanyMasterStatus companyMasterStatus;
    private Boolean defunct;
}
