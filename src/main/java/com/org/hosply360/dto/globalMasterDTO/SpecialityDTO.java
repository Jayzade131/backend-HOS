package com.org.hosply360.dto.globalMasterDTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpecialityDTO {

    private String id;
    private OrganizationDTO organizationDTO;

    @NotNull(message = "Description must not be blank")
    private String description;

    @NotNull(message = "Department must not be blank")
    private String department;

    private String type;

    private boolean defunct;

    private String masterType;


}
