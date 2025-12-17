package com.org.hosply360.service.auth;

import com.org.hosply360.dto.authDTO.RolesDTO;
import com.org.hosply360.dto.authDTO.RolesResDto;

import java.util.List;

public interface RolesService {
    RolesResDto createRole(RolesDTO rolesDto);

    RolesDTO updateRole(RolesDTO dto);

    RolesDTO getRole(String id);

    List<RolesDTO> getAllRoles(int page, int size);

    void deleteRole(String id);
}
