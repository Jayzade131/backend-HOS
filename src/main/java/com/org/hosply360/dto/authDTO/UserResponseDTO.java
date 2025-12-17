package com.org.hosply360.dto.authDTO;

import com.org.hosply360.dto.globalMasterDTO.OrganizationDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {

    private String id;

    private String username;

    private String name;

    private String mobileNo;

    private String email;

    private boolean defunct;

    private boolean isDefaultPassword;

    private List<OrganizationDTO> organizations;

    private List<RoleModuleAccessDto> roleModuleAccessDtos;

}
