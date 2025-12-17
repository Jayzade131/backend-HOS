package com.org.hosply360.controller.globalMaster;

import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.globalMasterDTO.OccupationDTO;
import com.org.hosply360.dto.globalMasterDTO.OccupationReqDTO;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.dto.globalMasterDTO.IdentificationDocumentDTO;
import com.org.hosply360.dto.globalMasterDTO.IdentificationDocumentReqDTO;
import com.org.hosply360.service.globalMaster.IdentificationDocumentService;
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
public class IdentificationDocumentController {

    private final IdentificationDocumentService identificationDocumentService;
    private static final Logger logger = LoggerFactory.getLogger(IdentificationDocumentController.class);

    @PostMapping(EndpointConstants.ID_DOCUMENT)
    public ResponseEntity<AppResponseDTO> createIdentificationDocument(@RequestBody IdentificationDocumentReqDTO identificationDocumentDto) {
        logger.info("Creating patientIdentification document with code: {}", identificationDocumentDto.getCode());
        IdentificationDocumentDTO created = identificationDocumentService.createIdentificationDocument(identificationDocumentDto);
        return ResponseEntity.ok(AppResponseDTO.ok(created));
    }

    @PutMapping(EndpointConstants.ID_DOCUMENT)
    public ResponseEntity<AppResponseDTO> updateIdentificationDocument(@RequestBody IdentificationDocumentReqDTO identificationDocumentDto) {
        IdentificationDocumentDTO updated = identificationDocumentService.updateIdentificationDocument(identificationDocumentDto.getId(),identificationDocumentDto);
        return ResponseEntity.ok(AppResponseDTO.ok(updated));
    }

    @GetMapping(EndpointConstants.DOCUMENT_BY_ID)
    public ResponseEntity<AppResponseDTO> getIdentificationDocumentById(@PathVariable String id) {
        logger.info("Fetching document with ID: {}", id);
        return ResponseEntity.ok(AppResponseDTO.ok(identificationDocumentService.getIdentificationDocumentById(id)));
    }

    @GetMapping(EndpointConstants.GET_ALL_ID_DOCUMENTS)
    public ResponseEntity<AppResponseDTO> getAllDocuments(@PathVariable String organizationId) {
        logger.info("Fetching all billing item groups for organization ID: {}", organizationId);
        return ResponseEntity.ok(AppResponseDTO.ok(identificationDocumentService.getAllIdentificationDocuments(organizationId)));

    }

    @DeleteMapping(EndpointConstants.DOCUMENT_BY_ID)
    public ResponseEntity<AppResponseDTO> deleteDocumentById(@PathVariable String id) {
        identificationDocumentService.deleteIdentificationDocumentById(id);
        return ResponseEntity.ok(AppResponseDTO.ok(ApplicationConstant.DELETED_SUCCESSFULLY));
    }
}