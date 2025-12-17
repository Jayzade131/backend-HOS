package com.org.hosply360.service.auth;

import com.org.hosply360.dto.authDTO.ModuleAccessMappingDTO;
import com.org.hosply360.dto.authDTO.RoleModuleMappingDTO;

import java.util.List;

public interface RoleModuleAccessMapService {

    RoleModuleMappingDTO createRoleModuleMapping(RoleModuleMappingDTO roleModuleMappingDTO);

    RoleModuleMappingDTO updateRoleModuleMapping(RoleModuleMappingDTO roleModuleMappingDTO);

    List<RoleModuleMappingDTO> getAllRoleModuleMapping(int page, int size);

    void deleteRoleModuleMapping(String id);


    ModuleAccessMappingDTO createModuleAccessMapping(ModuleAccessMappingDTO moduleAccessMappingDTO);

    ModuleAccessMappingDTO updateModuleAccessMapping(ModuleAccessMappingDTO moduleAccessMappingDTO);

    List<ModuleAccessMappingDTO> getAllModuleAccessMapping(int page, int size);

    void deleteModuleAccessMapping(String id);
}
