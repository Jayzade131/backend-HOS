package com.org.hosply360.controller.globalMaster;

import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.dto.globalMasterDTO.InsuranceProviderDTO;
import com.org.hosply360.dto.globalMasterDTO.InsuranceProviderReqDTO;
import com.org.hosply360.service.globalMaster.InsuranceProviderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(EndpointConstants.GLOBAL_MASTER_API)
public class InsuranceProviderController {
    private final InsuranceProviderService insuranceProviderService;
    private static final Logger logger = LoggerFactory.getLogger(InsuranceProviderController.class);

    @PostMapping(EndpointConstants.INSURANCE_PROVIDER)
    public ResponseEntity<AppResponseDTO> createInsuranceProvider(@RequestBody InsuranceProviderReqDTO dto) {
        logger.info("Creating insurance provider with code: {}", dto.getCode());
        InsuranceProviderDTO created = insuranceProviderService.createInsuranceProvider(dto);
        return ResponseEntity.ok(AppResponseDTO.ok(created));
    }

    @PutMapping(EndpointConstants.INSURANCE_PROVIDER)
    public ResponseEntity<AppResponseDTO> updateInsuranceProvider(@RequestBody InsuranceProviderReqDTO dto) {
        InsuranceProviderDTO updated = insuranceProviderService.updateInsuranceProvider(dto.getId(),dto);
        return ResponseEntity.ok(AppResponseDTO.ok(updated));
    }

    @GetMapping(EndpointConstants.INSURANCE_PROVIDER_BY_ID)
    public ResponseEntity<AppResponseDTO> getInsuranceProviderById(@PathVariable String id) {
        logger.info("Fetching insurance provider with ID: {}", id);
        return ResponseEntity.ok(AppResponseDTO.ok(insuranceProviderService.getInsuranceProviderById(id)));
    }

    @GetMapping(EndpointConstants.GET_ALL_INSURANCE_PROVIDERS)
    public ResponseEntity<AppResponseDTO> getAllProvider(@PathVariable String organizationId) {
        logger.info("Fetching all billing item groups for organization ID: {}", organizationId);
        return ResponseEntity.ok(AppResponseDTO.ok(insuranceProviderService.getAllInsuranceProviders(organizationId)));

    }

    @DeleteMapping(EndpointConstants.INSURANCE_PROVIDER_BY_ID)
    public ResponseEntity<AppResponseDTO> deleteInsuranceProviderById(@PathVariable String id) {
        insuranceProviderService.deleteInsuranceProviderById(id);
        return ResponseEntity.ok(AppResponseDTO.customStatus(ApplicationConstant.DELETED_SUCCESSFULLY, HttpStatus.OK));
    }


}

