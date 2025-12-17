package com.org.hosply360.service.auth.impl;

import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.auth.Access;
import com.org.hosply360.dao.auth.ModuleAccessMapping;
import com.org.hosply360.dao.auth.Modules;
import com.org.hosply360.dao.auth.RoleModuleMapping;
import com.org.hosply360.dao.auth.Roles;
import com.org.hosply360.dto.authDTO.ModuleAccessMappingDTO;
import com.org.hosply360.dto.authDTO.RoleModuleMappingDTO;
import com.org.hosply360.exception.GlobalMasterException;
import com.org.hosply360.repository.authRepo.AccessRepository;
import com.org.hosply360.repository.authRepo.ModuleAccessRepository;
import com.org.hosply360.repository.authRepo.ModuleRepository;
import com.org.hosply360.repository.authRepo.RoleModuleRepository;
import com.org.hosply360.repository.authRepo.RolesRepository;
import com.org.hosply360.service.auth.RoleModuleAccessMapService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleModuleAccessMapServiceImpl implements RoleModuleAccessMapService {

    private final RolesRepository rolesRepository;

    private final ModuleRepository moduleRepository;

    private final AccessRepository accessRepository;

    private final RoleModuleRepository roleModuleRepository;

    private final ModuleAccessRepository moduleAccessRepository;

    private static final Logger logger = LoggerFactory.getLogger(RoleModuleAccessMapServiceImpl.class);


    public static RoleModuleMappingDTO getRoleModuleMappingDTO(RoleModuleMapping roleModuleMapping) {
        RoleModuleMappingDTO dto = new RoleModuleMappingDTO();
        dto.setId(roleModuleMapping.getId());
        dto.setRoleId(roleModuleMapping.getRoles().getId());
        dto.setModuleId(roleModuleMapping.getModules().getId());
        return dto;
    }

    public static ModuleAccessMappingDTO getModuleAccessMappingDTO(ModuleAccessMapping moduleAccessMapping) {
        ModuleAccessMappingDTO dto = new ModuleAccessMappingDTO();
        dto.setId(moduleAccessMapping.getId());
        dto.setModuleId(moduleAccessMapping.getModules().getId());
        dto.setAccessId(moduleAccessMapping.getAccess().getId());
        return dto;
    }


    @Override
    public RoleModuleMappingDTO createRoleModuleMapping(RoleModuleMappingDTO dto) {
        if (dto == null) {
            throw new GlobalMasterException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST);
        }

        Roles role = rolesRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ROLE_NOT_FOUND, HttpStatus.NOT_FOUND));

        Modules module = moduleRepository.findById(dto.getModuleId())
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.MODULE_NOT_FOUND, HttpStatus.NOT_FOUND));

        RoleModuleMapping entity = new RoleModuleMapping();
        entity.setRoles(role);
        entity.setModules(module);

        RoleModuleMapping saved = roleModuleRepository.save(entity);
        return getRoleModuleMappingDTO(saved);
    }


    @Override
    @Transactional
    public RoleModuleMappingDTO updateRoleModuleMapping(RoleModuleMappingDTO dto) {
        if (dto == null || dto.getId() == null) {
            throw new GlobalMasterException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST);
        }

        RoleModuleMapping existing = roleModuleRepository.findById(dto.getId())
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ROLE_MODULE_MAPPING_NOT_FOUND, HttpStatus.NOT_FOUND));


        Roles role = rolesRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ROLE_NOT_FOUND, HttpStatus.NOT_FOUND));

        Modules module = moduleRepository.findById(dto.getModuleId())
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.MODULE_NOT_FOUND, HttpStatus.NOT_FOUND));


        existing.setRoles(role);
        existing.setModules(module);

        RoleModuleMapping updated = roleModuleRepository.save(existing);
        logger.info("Role Module Mapping updated successfully");

        return getRoleModuleMappingDTO(updated);
    }


    @Override
    @Transactional
    public List<RoleModuleMappingDTO> getAllRoleModuleMapping(int page, int size) {
        logger.info("Fetching all role-module mappings");

        Pageable pageable = PageRequest.of(page, size);
        Page<RoleModuleMapping> pageResult = roleModuleRepository.findAll(pageable);

        return pageResult.stream()
                .map(RoleModuleAccessMapServiceImpl::getRoleModuleMappingDTO)
                .toList();
    }


    @Override
    public void deleteRoleModuleMapping(String id) {
        if (id == null || id.isEmpty()) {
            throw new GlobalMasterException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST);
        }
        RoleModuleMapping roleModuleMapping = roleModuleRepository.findById(id)
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ROLE_MODULE_MAPPING_NOT_FOUND, HttpStatus.NOT_FOUND));
        roleModuleRepository.delete(roleModuleMapping);
        logger.info("Role Module Mapping deleted successfully with ID: {}", id);

    }

    @Override
    public ModuleAccessMappingDTO createModuleAccessMapping(ModuleAccessMappingDTO dto) {
        if (dto == null) {
            throw new GlobalMasterException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST);
        }
        Modules module = moduleRepository.findById(dto.getModuleId())
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.MODULE_NOT_FOUND, HttpStatus.NOT_FOUND));

        Access access = accessRepository.findById(dto.getAccessId())
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ACCESS_NOT_FOUND, HttpStatus.NOT_FOUND));

        ModuleAccessMapping entity = new ModuleAccessMapping();
        entity.setModules(module);
        entity.setAccess(access);

        ModuleAccessMapping saved = moduleAccessRepository.save(entity);
        logger.info("Module Access Mapping created successfully");

        return getModuleAccessMappingDTO(saved);
    }


    @Override
    public ModuleAccessMappingDTO updateModuleAccessMapping(ModuleAccessMappingDTO moduleAccessMappingDTO) {
        if (moduleAccessMappingDTO == null || moduleAccessMappingDTO.getId() == null) {
            throw new GlobalMasterException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST);
        }
        ModuleAccessMapping existingMapping = moduleAccessRepository.findById(moduleAccessMappingDTO.getId())
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.MODULE_ACCESS_MAPPING_NOT_FOUND, HttpStatus.NOT_FOUND));

        Modules module = moduleRepository.findById(moduleAccessMappingDTO.getModuleId())
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.MODULE_NOT_FOUND, HttpStatus.NOT_FOUND));

        Access access = accessRepository.findById(moduleAccessMappingDTO.getAccessId())
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ACCESS_NOT_FOUND, HttpStatus.NOT_FOUND));

        existingMapping.setModules(module);
        existingMapping.setAccess(access);
        ModuleAccessMapping updated = moduleAccessRepository.save(existingMapping);
        logger.info(" Module Access Mapping updated successfully");
        return getModuleAccessMappingDTO(updated);
    }

    @Override
    public List<ModuleAccessMappingDTO> getAllModuleAccessMapping(int page, int size) {
        logger.info("Fetching all module-access mappings");

        Pageable pageable = PageRequest.of(page, size);
        Page<ModuleAccessMapping> pageResult = moduleAccessRepository.findAll(pageable);

        return pageResult.stream()
                .map(RoleModuleAccessMapServiceImpl::getModuleAccessMappingDTO)
                .toList();
    }

    @Override
    public void deleteModuleAccessMapping(String id) {

        if (id == null || id.isEmpty()) {
            throw new GlobalMasterException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST);
        }
        ModuleAccessMapping moduleAccessMapping = moduleAccessRepository.findById(id)
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.MODULE_ACCESS_MAPPING_NOT_FOUND, HttpStatus.NOT_FOUND));

        moduleAccessRepository.delete(moduleAccessMapping);
        logger.info("Module Access Mapping deleted successfully with ID: {}", id);

    }
}
