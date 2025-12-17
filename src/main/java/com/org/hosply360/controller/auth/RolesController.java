package com.org.hosply360.controller.auth;

import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.dto.authDTO.RolesDTO;
import com.org.hosply360.service.auth.RolesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequestMapping(EndpointConstants.GLOBAL_MASTER_API)
@RequiredArgsConstructor
public class RolesController {

    private final RolesService rolesService;
    private static final Logger logger = LoggerFactory.getLogger(RolesController.class);

    @PostMapping(EndpointConstants.ROLE)
    public ResponseEntity<AppResponseDTO> createRole(@RequestBody RolesDTO dto) {
        logger.info("Creating role: {}", dto.getName());
        return ResponseEntity.ok(AppResponseDTO.ok(rolesService.createRole(dto)));
    }

    @PutMapping(EndpointConstants.ROLE)
    public ResponseEntity<AppResponseDTO> updateRole(@RequestBody RolesDTO dto) {
        return ResponseEntity.ok(AppResponseDTO.ok(rolesService.updateRole(dto)));
    }

    @GetMapping(EndpointConstants.ROLE_BY_ID)
    public ResponseEntity<AppResponseDTO> getRole(@PathVariable String id) {
        logger.info("Fetching role with ID: {}", id);
        return ResponseEntity.ok(AppResponseDTO.ok(rolesService.getRole(id)));
    }

    @GetMapping(EndpointConstants.GET_ALL_ROLES)
    public ResponseEntity<AppResponseDTO> getAllRoles(
            @RequestParam(defaultValue = ApplicationConstant.DEFAULT_VALUE_PAGE) int pageNumber,
            @RequestParam(defaultValue = ApplicationConstant.DEFAULT_VALUE_SIZE) int pageSize) {
        logger.info("Fetching all roles");
        return ResponseEntity.ok(AppResponseDTO.ok(rolesService.getAllRoles(pageNumber, pageSize)));
    }

    @DeleteMapping(EndpointConstants.ROLE_BY_ID)
    public ResponseEntity<AppResponseDTO> deleteRole(@PathVariable String id) {
        rolesService.deleteRole(id);
        return ResponseEntity.ok(AppResponseDTO.ok(ApplicationConstant.DELETED_SUCCESSFULLY));
    }
}
