package com.org.hosply360.controller.globalMaster;

import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.dto.globalMasterDTO.OrganizationReqDTO;
import com.org.hosply360.service.globalMaster.OrganizationMasterService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(EndpointConstants.GLOBAL_MASTER_API)
@RequiredArgsConstructor
public class OrganizationMasterController {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationMasterController.class);

    private final OrganizationMasterService organizationService;


    @PostMapping(EndpointConstants.ORGANIZATION_API)
    public ResponseEntity<AppResponseDTO> createOrganization(@RequestBody OrganizationReqDTO organizationDTO) {
        logger.info("Creating organization with org no: {}", organizationDTO.getId());
        return ResponseEntity.ok(AppResponseDTO.ok(organizationService.saveOrUpdateOrganization(organizationDTO)));
    }


    @GetMapping(EndpointConstants.ORGANIZATION_API_BY_ID)
    public ResponseEntity<AppResponseDTO> getOrganization(@PathVariable String id) {
        logger.info("Fetching organization with ID: {}", id);
        return ResponseEntity.ok(AppResponseDTO.ok(organizationService.getOrganization(id)));
    }

    @GetMapping(EndpointConstants.FETCH_ALL_ORGANIZATION)
    public ResponseEntity<AppResponseDTO> fetchAllOrganization(){
        logger.info("Fetching all organizations");
        return ResponseEntity.ok(AppResponseDTO.ok(organizationService.fetchAllOrganization()));
    }


}
