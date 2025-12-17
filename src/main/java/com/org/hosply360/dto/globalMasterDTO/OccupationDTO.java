package com.org.hosply360.dto.globalMasterDTO;

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
public class OccupationDTO {

    private String id;
    private OrganizationDTO organizationDTO;
    private String occupationCode;
    private String description;
    private boolean defunct;

}
