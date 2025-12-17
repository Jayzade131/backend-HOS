package com.org.hosply360.dto.globalMasterDTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TariffDTO {


    private String id;

    private OrganizationDTO organizationDTO;

    private String name;

    private boolean defunct;


}
