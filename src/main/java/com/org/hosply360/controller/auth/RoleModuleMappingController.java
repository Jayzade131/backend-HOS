package com.org.hosply360.controller.auth;

import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.dto.authDTO.ModuleAccessMappingDTO;
import com.org.hosply360.dto.authDTO.RoleModuleMappingDTO;
import com.org.hosply360.service.auth.RoleModuleAccessMapService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping(EndpointConstants.GLOBAL_MASTER_API)
@RequiredArgsConstructor

public class RoleModuleMappingController {
    private static final Logger logger = LoggerFactory.getLogger(RoleModuleMappingController.class);
    private final RoleModuleAccessMapService roleModuleMappingService;


    @PostMapping(EndpointConstants.ROLE_MODULE_MAPPING)
    public ResponseEntity<AppResponseDTO> createRoleModuleMapping(@RequestBody RoleModuleMappingDTO roleModuleMappingDTO) {
        logger.info("Creating role module mapping for  ID: {}", roleModuleMappingDTO.getId());
        RoleModuleMappingDTO created  = roleModuleMappingService.createRoleModuleMapping(roleModuleMappingDTO);
        return ResponseEntity.ok(AppResponseDTO.ok(created));
    }

    @GetMapping(EndpointConstants.GET_ROLE_MODULE_MAPPING)
    public ResponseEntity<AppResponseDTO> getAllAccess(
            @RequestParam(defaultValue = ApplicationConstant.DEFAULT_VALUE_PAGE) int pageNumber,
            @RequestParam(defaultValue = ApplicationConstant.DEFAULT_VALUE_SIZE) int pageSize) {
        logger.info("Fetching all role module mappings");
        List<RoleModuleMappingDTO> roleModuleMappingList = roleModuleMappingService.getAllRoleModuleMapping(pageNumber, pageSize);
        return ResponseEntity.ok(AppResponseDTO.ok(roleModuleMappingList));
    }


    @PutMapping(EndpointConstants.ROLE_MODULE_MAPPING)
    public ResponseEntity<AppResponseDTO> updateRoleModuleMapping(@RequestBody RoleModuleMappingDTO roleModuleMappingDTO) {
        logger.info("Updating role module mapping for  ID: {}", roleModuleMappingDTO.getId());
        RoleModuleMappingDTO updated = roleModuleMappingService.updateRoleModuleMapping(roleModuleMappingDTO);
        return ResponseEntity.ok(AppResponseDTO.ok(updated));
    }

    @DeleteMapping(EndpointConstants.ROLE_MODULE_MAPPING_BY_ID)
    public ResponseEntity<AppResponseDTO> deleteRoleModuleMapping(@PathVariable String id) {
        logger.info("Deleting role module mapping with ID: {}", id);
        roleModuleMappingService.deleteRoleModuleMapping(id);
        return ResponseEntity.ok(AppResponseDTO.ok("Role module mapping deleted successfully"));
    }

    @PostMapping(EndpointConstants.MODULE_ACCESS_MAPPING)
    public ResponseEntity<AppResponseDTO> createModuleAccessMapping(@RequestBody ModuleAccessMappingDTO moduleAccessMappingDTO) {
        logger.info("Creating module access mapping for  ID: {}", moduleAccessMappingDTO.getId());
        ModuleAccessMappingDTO created = roleModuleMappingService.createModuleAccessMapping(moduleAccessMappingDTO);
        return ResponseEntity.ok(AppResponseDTO.ok(created));
    }

    @PutMapping(EndpointConstants.MODULE_ACCESS_MAPPING)
    public ResponseEntity<AppResponseDTO> updateModuleAccessMapping(@RequestBody ModuleAccessMappingDTO moduleAccessMappingDTO) {
        logger.info("Updating module access mapping for  ID: {}", moduleAccessMappingDTO.getId());
        ModuleAccessMappingDTO updated = roleModuleMappingService.updateModuleAccessMapping(moduleAccessMappingDTO);
        return ResponseEntity.ok(AppResponseDTO.ok(updated));
    }
    @GetMapping(EndpointConstants.GET_MODULE_ACCESS_MAPPING)
    public ResponseEntity<AppResponseDTO> getAllModuleAccessMapping(
            @RequestParam(defaultValue = ApplicationConstant.DEFAULT_VALUE_PAGE) int pageNumber,
            @RequestParam(defaultValue = ApplicationConstant.DEFAULT_VALUE_SIZE) int pageSize) {
        logger.info("Fetching all module access mappings");
        List<ModuleAccessMappingDTO> moduleAccessMappingList = roleModuleMappingService.getAllModuleAccessMapping(pageNumber, pageSize);
        return ResponseEntity.ok(AppResponseDTO.ok(moduleAccessMappingList));
    }
    @DeleteMapping(EndpointConstants.MODULE_ACCESS_MAPPING_BY_ID)
    public ResponseEntity<AppResponseDTO> deleteModuleAccessMapping(@PathVariable String id) {
        logger.info("Deleting module access mapping with ID: {}", id);
        roleModuleMappingService.deleteModuleAccessMapping(id);
        return ResponseEntity.ok(AppResponseDTO.ok("Module access mapping deleted successfully"));
    }






}
