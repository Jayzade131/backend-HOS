package com.org.hosply360.dto.globalMasterDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpecialityReqDTO {
    private String id;
    private String organization;

    @NotNull(message = "Description must not be blank")
    private String description;

    @NotNull(message = "Department must not be blank")
    private String department;

    private String type;

    private boolean defunct;

    private String masterType;

}
