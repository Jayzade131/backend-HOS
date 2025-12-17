package com.org.hosply360.controller.globalMaster;

import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.dto.globalMasterDTO.ReligionDTO;
import com.org.hosply360.dto.globalMasterDTO.ReligionReqDTO;
import com.org.hosply360.service.globalMaster.ReligionService;
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
public class ReligionController
{
    private final ReligionService religionService;
    private static final Logger logger = LoggerFactory.getLogger(ReligionController.class);

    @PostMapping(EndpointConstants.RELIGION)
    public ResponseEntity<AppResponseDTO> createReligion(@RequestBody ReligionReqDTO religionDto)
    {
        logger.info("Creating religion with code: {}", religionDto.getCode());
        ReligionDTO created = religionService.createReligion(religionDto);
        return ResponseEntity.ok(AppResponseDTO.ok(created));
    }

    @PutMapping(EndpointConstants.RELIGION)
    public ResponseEntity<AppResponseDTO> updateReligion(@RequestBody ReligionReqDTO religionDto) {
        ReligionDTO updated = religionService.updateReligion(religionDto.getId(),religionDto);
        return ResponseEntity.ok(AppResponseDTO.ok(updated));
    }

    @GetMapping(EndpointConstants.RELIGION_BY_ID)
    public ResponseEntity<AppResponseDTO>  getReligionById(@PathVariable String id) {
        logger.info("Fetching religion with ID: {}", id);
        return ResponseEntity.ok(AppResponseDTO.ok(religionService.getReligionById(id)));
    }

    @GetMapping(EndpointConstants.GET_ALL_RELIGION)
    public ResponseEntity<AppResponseDTO> getAllReligion(@PathVariable String organizationId) {
        logger.info("Fetching all billing item groups for organization ID: {}", organizationId);
        return ResponseEntity.ok(AppResponseDTO.ok(religionService.getAllReligions(organizationId)));

    }

    @DeleteMapping(EndpointConstants.RELIGION_BY_ID)
    public ResponseEntity<AppResponseDTO> deleteReligionById(@PathVariable String id) {
        religionService.deleteReligionById(id);
        return ResponseEntity.ok(AppResponseDTO.customStatus(ApplicationConstant.DELETED_SUCCESSFULLY, HttpStatus.OK));
    }


}
