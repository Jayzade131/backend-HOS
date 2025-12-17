package com.org.hosply360.controller.globalMaster;

import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.dto.globalMasterDTO.SpecialityReqDTO;
import com.org.hosply360.service.globalMaster.SpecialityMasterService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@RequestMapping(EndpointConstants.GLOBAL_MASTER_API)
@RequiredArgsConstructor
@Validated
public class SpecialityMasterController {


    private static final Logger logger = LoggerFactory.getLogger(SpecialityMasterController.class);

    private final SpecialityMasterService specialityMasterService;

    @PostMapping(EndpointConstants.SPECIALITY_API)
    public ResponseEntity<AppResponseDTO> createSpeciality(@Valid @RequestBody SpecialityReqDTO specialityDto) {
        logger.info("Creating speciality with description: {}", specialityDto.getDescription());
        return ResponseEntity.ok(AppResponseDTO.ok(specialityMasterService.createSpeciality(specialityDto)));
    }

    @GetMapping(EndpointConstants.GET_SPECIALITIES_API)
    public ResponseEntity<AppResponseDTO> getAllSpeciality(@PathVariable String organizationId,
                                                           @PathVariable String masterType) {
        logger.info("Fetch all speciality by Id: {} {}", organizationId, masterType);
        return ResponseEntity.ok(AppResponseDTO.ok(specialityMasterService.getAllSpeciality(organizationId, masterType)));
    }

    @GetMapping(EndpointConstants.SPECIALITY_API_BY_ID)
    public ResponseEntity<AppResponseDTO> getSpecialityById(@PathVariable String id) {
        logger.info("Fetching speciality with ID: {}", id);
        return ResponseEntity.ok(AppResponseDTO.ok(specialityMasterService.getSpecialityById(id)));
    }

    @PutMapping(EndpointConstants.SPECIALITY_API)
    public ResponseEntity<AppResponseDTO> updateSpeciality(@Valid @RequestBody SpecialityReqDTO specialityDto) {
        logger.info("Updating speciality with ID: {}", specialityDto.getId());
        return ResponseEntity.ok(AppResponseDTO.ok(specialityMasterService.updateSpeciality(specialityDto.getId(),specialityDto)));
    }

    @DeleteMapping(EndpointConstants.SPECIALITY_API_BY_ID)
    public ResponseEntity<AppResponseDTO> deleteSpecialityById(@PathVariable String id) {
        specialityMasterService.deleteSpeciality(id);
        return ResponseEntity.ok(AppResponseDTO.customStatus(ApplicationConstant.DELETED_SUCCESSFULLY, HttpStatus.OK));
    }


}
