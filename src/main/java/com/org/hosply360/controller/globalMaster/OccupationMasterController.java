package com.org.hosply360.controller.globalMaster;

import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.dto.globalMasterDTO.OccupationDTO;
import com.org.hosply360.dto.globalMasterDTO.OccupationReqDTO;
import com.org.hosply360.service.globalMaster.OccupationMasterService;
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

public  class OccupationMasterController {


    private static final Logger logger = LoggerFactory.getLogger(OccupationMasterController.class);

    private final OccupationMasterService occupationMasterService;

    @PostMapping(EndpointConstants.OCCUPATION_API)
    public ResponseEntity<AppResponseDTO> createOccupation(@RequestBody OccupationReqDTO occupationDTO) {
        logger.info("Creating occupation with code: {}", occupationDTO.getOccupationCode());
        OccupationDTO createdOccupationDTO = occupationMasterService.createOccupation(occupationDTO);
        return ResponseEntity.ok(AppResponseDTO.ok(createdOccupationDTO));
    }

    @GetMapping(EndpointConstants.GET_OCCUPATIONS_API)
    public ResponseEntity<AppResponseDTO> getAllOccupations(@PathVariable String organizationId) {
        logger.info("Fetching all billing item groups for organization ID: {}", organizationId);
        return ResponseEntity.ok(AppResponseDTO.ok(occupationMasterService.getAllOccupations(organizationId)));
    }

    @GetMapping(EndpointConstants.OCCUPATION_API_BY_ID)
    public ResponseEntity<AppResponseDTO> getOccupationById(@PathVariable String id) {
        logger.info("Fetching occupation with ID: {}", id);
        return ResponseEntity.ok(AppResponseDTO.ok(occupationMasterService.getOccupationById(id)));
    }

    @PutMapping(EndpointConstants.OCCUPATION_API)
    public ResponseEntity<AppResponseDTO> updateOccupation(@RequestBody OccupationReqDTO occupationDTO) {
        OccupationDTO updated = occupationMasterService.updateOccupation(occupationDTO.getId(),occupationDTO);
        return ResponseEntity.ok(AppResponseDTO.ok(updated));
    }

    @DeleteMapping(EndpointConstants.OCCUPATION_API_BY_ID)
    public ResponseEntity<AppResponseDTO> deleteOccupationById(@PathVariable String id) {
        occupationMasterService.deleteOccupation(id);
        return ResponseEntity.ok(AppResponseDTO.customStatus(ApplicationConstant.DELETED_SUCCESSFULLY, HttpStatus.OK));
    }


}