package com.org.hosply360.dto.globalMasterDTO;

import com.org.hosply360.constant.Enums.PatientCategoryStatus;
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
public class PatientCategoryDTO {
    private String id;
    private OrganizationDTO organizationDTO;
    private String categoryName;
    private TariffDTO tariffDTO;
    private PatientCategoryStatus patientCategoryStatus;
    private Boolean defunct;
}
