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
public class CompanyMasterReqDTO {
    private String id;
    private String organization;
    private String companyName;
    private String tariff;
    private CompanyMasterStatus companyMasterStatus;
    private Boolean defunct;
}
