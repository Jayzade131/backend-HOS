package com.org.hosply360.controller.auth;

import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.authDTO.AccessDTO;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.service.auth.AccessService;
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
public class AccessController {

    private static final Logger logger = LoggerFactory.getLogger(AccessController.class);
    private final AccessService accessService;

    @PostMapping(EndpointConstants.ACCESS)
    private ResponseEntity<AppResponseDTO> createAccess(@RequestBody AccessDTO accessDTO) {
        logger.info("Creating access for user: {}", accessDTO.getAccessName());
        AccessDTO createdAccess = accessService.createAccess(accessDTO);
        return ResponseEntity.ok(AppResponseDTO.ok(createdAccess));
    }

    @PutMapping(EndpointConstants.ACCESS)
    private ResponseEntity<AppResponseDTO> updateAccess(@RequestBody AccessDTO accessDTO) {
        logger.info("Updating access for user: {}", accessDTO.getAccessName());
        AccessDTO updatedAccess = accessService.updateAccess(accessDTO);
        return ResponseEntity.ok(AppResponseDTO.ok(updatedAccess));
    }

    @GetMapping(EndpointConstants.ACCESS_BY_ID)
    private ResponseEntity<AppResponseDTO> getAccess(@RequestBody String id) {
        logger.info("Fetching access with ID: {}", id);
        AccessDTO access = accessService.getAccess(id);
        return ResponseEntity.ok(AppResponseDTO.ok(access));
    }

    @GetMapping(EndpointConstants.GET_ALL_ACCESS)
    public ResponseEntity<AppResponseDTO> getAllAccess(
            @RequestParam(defaultValue = ApplicationConstant.DEFAULT_VALUE_PAGE) int pageNumber,
            @RequestParam(defaultValue = ApplicationConstant.DEFAULT_VALUE_SIZE) int pageSize) {
        logger.info("Fetching all access");
        List<AccessDTO> accessList = accessService.getAllAccess(pageNumber, pageSize);
        return ResponseEntity.ok(AppResponseDTO.ok(accessList));
    }

    @DeleteMapping(EndpointConstants.ACCESS_BY_ID)
    public ResponseEntity<AppResponseDTO> deleteAccess(@PathVariable String id) {
        logger.info("Deleting access with ID: {}", id);
        accessService.deleteAccess(id);
        return ResponseEntity.ok(AppResponseDTO.ok(ApplicationConstant.DELETED_SUCCESSFULLY));
    }


}
