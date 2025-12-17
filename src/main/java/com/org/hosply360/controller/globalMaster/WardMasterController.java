package com.org.hosply360.controller.globalMaster;

import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.dto.globalMasterDTO.WardMasterDto;
import com.org.hosply360.service.globalMaster.WardMasterService;
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
public class WardMasterController {

    private static final Logger logger = LoggerFactory.getLogger(WardMasterController.class);

    private final WardMasterService wardMasterService;

    @PostMapping(EndpointConstants.CREATE_WARD_API)
    public ResponseEntity<AppResponseDTO> createWard(@RequestBody WardMasterDto wardDto) {
        logger.info("Creating ward with name: {} for Org: {}", wardDto.getWardName(), wardDto.getOrgId());
        WardMasterDto created = wardMasterService.createWard(wardDto);
        return ResponseEntity.ok(AppResponseDTO.ok(created));
    }

    @GetMapping(EndpointConstants.GET_WARDS_API)
    public ResponseEntity<AppResponseDTO> getAllWards(@RequestParam String orgId) {
        logger.info("Fetching all wards for Org ID: {}", orgId);
        List<WardMasterDto> wards = wardMasterService.getAllWards(orgId);
        return ResponseEntity.ok(AppResponseDTO.ok(wards));
    }

    @GetMapping(EndpointConstants.GET_WARD_API)
    public ResponseEntity<AppResponseDTO> getWardById(@PathVariable String orgId, @PathVariable String id) {
        logger.info("Fetching ward with ID: {} for Org: {}", id, orgId);
        WardMasterDto ward = wardMasterService.getWardById(orgId, id);
        return ResponseEntity.ok(AppResponseDTO.ok(ward));
    }

    @PutMapping(EndpointConstants.UPDATE_WARD_API)
    public ResponseEntity<AppResponseDTO> updateWard(@RequestBody WardMasterDto wardDto) {
        logger.info("Updating ward with ID: {}", wardDto.getId());
        WardMasterDto updated = wardMasterService.updateWard(wardDto);
        return ResponseEntity.ok(AppResponseDTO.ok(updated));
    }

    @DeleteMapping(EndpointConstants.DELETE_WARD_API)
    public ResponseEntity<AppResponseDTO> deleteWardById(@PathVariable String id) {
        logger.info("Deleting ward with ID: {}", id);
        wardMasterService.deleteWardById(id);
        return ResponseEntity.ok(AppResponseDTO.customStatus(ApplicationConstant.DELETED_SUCCESSFULLY, HttpStatus.OK));
    }
}
