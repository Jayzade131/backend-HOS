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
public class PatientCategoryReqDTO {
    private String id;
    private String organizationId;
    private String categoryName;
    private String tariffId;
    private PatientCategoryStatus patientCategoryStatus;
    private Boolean defunct;
}
