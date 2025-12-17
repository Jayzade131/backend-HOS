package com.org.hosply360.controller.globalMaster;

import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.dto.globalMasterDTO.ConfigurationReqDTO;
import com.org.hosply360.service.globalMaster.ConfigurationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(EndpointConstants.GLOBAL_MASTER_API)
@RequiredArgsConstructor
public class ConfigurationController {

    private final ConfigurationService configurationService;
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationController.class);

    @PostMapping(EndpointConstants.CONFIGURATION)
    public ResponseEntity<AppResponseDTO> createConfiguration(@RequestBody ConfigurationReqDTO configurationDTO) {
        logger.info("Creating configuration with key: {}", configurationDTO);
        return ResponseEntity.ok(AppResponseDTO.ok(configurationService.createConfiguration(configurationDTO)));
    }

    @PutMapping(EndpointConstants.CONFIGURATION)
    public ResponseEntity<AppResponseDTO> updateConfiguration(@RequestBody ConfigurationReqDTO configurationDTO) {
        logger.info("Updating configuration with ID: {}", configurationDTO.getId());
        return ResponseEntity.ok(AppResponseDTO.ok(configurationService.updateConfiguration(configurationDTO.getId(), configurationDTO)));
    }

    @GetMapping(EndpointConstants.CONFIGURATION_BY_ID)
    public ResponseEntity<AppResponseDTO> getConfigurationById(@PathVariable String id) {
        return ResponseEntity.ok(AppResponseDTO.ok(configurationService.getConfiguration(id)));
    }

    @GetMapping(EndpointConstants.GET_ALL_CONFIGURATION)
    public ResponseEntity<AppResponseDTO> getAllConfigurations(@PathVariable String organizationId) {
        return ResponseEntity.ok(AppResponseDTO.ok(configurationService.getAllConfiguration(organizationId)));
    }


    @DeleteMapping(EndpointConstants.CONFIGURATION_BY_ID)
    public ResponseEntity<AppResponseDTO> deleteConfiguration(@PathVariable String id) {
        logger.info("Deleting configuration with ID: {}", id);
        configurationService.deleteConfiguration(id);
        return ResponseEntity.ok(AppResponseDTO.customStatus(ApplicationConstant.DELETED_SUCCESSFULLY, HttpStatus.OK));
    }
}
