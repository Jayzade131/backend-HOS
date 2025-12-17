package com.org.hosply360.controller.globalMaster;


import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.dto.globalMasterDTO.WardBedMasterDto;
import com.org.hosply360.service.globalMaster.WardBedMasterService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(EndpointConstants.GLOBAL_MASTER_API)
@RequiredArgsConstructor
public class WardBedMasterController {

    private static final Logger logger = LoggerFactory.getLogger(WardBedMasterController.class);

    private final WardBedMasterService wardBedMasterService;

    @PostMapping(EndpointConstants.CREATE_WARD_BED_API)
    public ResponseEntity<AppResponseDTO> createWardBed(@RequestBody WardBedMasterDto bedDto) {
        logger.info("Creating bed '{}' for Ward: {} and Org: {}", bedDto.getBedNo(), bedDto.getWardId(), bedDto.getOrgId());
        WardBedMasterDto created = wardBedMasterService.create(bedDto);
        return ResponseEntity.ok(AppResponseDTO.ok(created));
    }

    @PutMapping(EndpointConstants.UPDATE_WARD_BED_API)
    public ResponseEntity<AppResponseDTO> updateWardBed(@RequestBody WardBedMasterDto bedDto) {
        logger.info("Updating bed with ID: {}", bedDto.getId());
        WardBedMasterDto updated = wardBedMasterService.update(bedDto);
        return ResponseEntity.ok(AppResponseDTO.ok(updated));
    }

    @GetMapping(EndpointConstants.GET_WARD_BEDS_API)
    public ResponseEntity<AppResponseDTO> getAllWardBeds(@RequestParam String orgId, @RequestParam String wardId) {
        logger.info("Fetching all beds for Ward ID: {} in Org ID: {}", wardId, orgId);
        List<WardBedMasterDto> beds = wardBedMasterService.getAllByWard(orgId, wardId);
        return ResponseEntity.ok(AppResponseDTO.ok(beds));
    }

    @GetMapping(EndpointConstants.GET_WARD_BED_API)
    public ResponseEntity<AppResponseDTO> getWardBedById(@PathVariable String id) {
        logger.info("Fetching bed with ID: {}", id);
        WardBedMasterDto bed = wardBedMasterService.getById(id);
        return ResponseEntity.ok(AppResponseDTO.ok(bed));
    }

    @DeleteMapping(EndpointConstants.DELETE_WARD_BED_API)
    public ResponseEntity<AppResponseDTO> deleteWardBedById(@PathVariable String id) {
        logger.info("Deleting (soft) bed with ID: {}", id);
        wardBedMasterService.delete(id);
        return ResponseEntity.ok(AppResponseDTO.customStatus(ApplicationConstant.DELETED_SUCCESSFULLY, HttpStatus.OK));
    }

    @GetMapping(EndpointConstants.GET_ALL_AVAILABLE_BEDS_API)
    public ResponseEntity<AppResponseDTO> getAllAvailableBedsByWard(@RequestParam String orgId, @RequestParam String wardId) {
        logger.info("Fetching all available beds for Ward ID: {} in Org ID: {}", wardId, orgId);
        return ResponseEntity.ok(AppResponseDTO.ok(wardBedMasterService.getAllAvilableBedsByWard(orgId, wardId)));
    }
}
