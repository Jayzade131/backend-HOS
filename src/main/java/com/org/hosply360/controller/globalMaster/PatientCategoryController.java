package com.org.hosply360.controller.globalMaster;

import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.dto.globalMasterDTO.PatientCategoryDTO;
import com.org.hosply360.dto.globalMasterDTO.PatientCategoryReqDTO;
import com.org.hosply360.service.globalMaster.PatientCategoryService;
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
public class PatientCategoryController {
    private static final Logger logger = LoggerFactory.getLogger(PatientCategoryController.class);

    private final PatientCategoryService patientCategoryService;

    @PostMapping(EndpointConstants.PATIENT_CATEGORY)
    public ResponseEntity<AppResponseDTO> create(@RequestBody PatientCategoryReqDTO requestDTO) {
        logger.info("Creating Patient Category");
        PatientCategoryDTO dto = patientCategoryService.createPatientCategory(requestDTO);
        return ResponseEntity.ok(AppResponseDTO.ok(dto));
    }

    @PutMapping(EndpointConstants.PATIENT_CATEGORY)
    public ResponseEntity<AppResponseDTO> update(@RequestBody PatientCategoryReqDTO requestDTO) {
        logger.info("Updating Patient Category with ID: {}", requestDTO.getId());
        PatientCategoryDTO dto = patientCategoryService.updatePatientCategory(requestDTO.getId(), requestDTO);
        return ResponseEntity.ok(AppResponseDTO.ok(dto));
    }

    @GetMapping(EndpointConstants.PATIENT_CATEGORY_BY_ID)
    public ResponseEntity<AppResponseDTO> getById(@PathVariable String id) {
        logger.info("Fetching Patient Category by ID: {}", id);
        PatientCategoryDTO dto = patientCategoryService.getPatientCategoryById(id);
        return ResponseEntity.ok(AppResponseDTO.ok(dto));
    }

    @GetMapping(EndpointConstants.GET_ALL_PATIENT_CATEGORIES)
    public ResponseEntity<AppResponseDTO> getAll(@PathVariable String organizationId) {
        logger.info("Fetching all Patient Categories for Organization ID: {}", organizationId);
        return ResponseEntity.ok(AppResponseDTO.ok(patientCategoryService.getAllPatientCategories(organizationId)));
    }

    @DeleteMapping(EndpointConstants.PATIENT_CATEGORY_BY_ID)
    public ResponseEntity<AppResponseDTO> delete(@PathVariable String id) {
        logger.info("Deleting Patient Category with ID: {}", id);
        patientCategoryService.deletePatientCategory(id);
        return ResponseEntity.ok(AppResponseDTO.customStatus(ApplicationConstant.DELETED_SUCCESSFULLY, HttpStatus.OK));
    }


}
