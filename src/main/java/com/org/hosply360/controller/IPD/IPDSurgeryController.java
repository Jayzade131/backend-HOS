package com.org.hosply360.controller.IPD;

import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.IPDDTO.IPDSurgeryDTO;
import com.org.hosply360.dto.IPDDTO.IPDSurgeryReqDTO;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.service.IPD.IPDSurgeryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@RestController
@RequestMapping(EndpointConstants.IPD_API)
@RequiredArgsConstructor
public class IPDSurgeryController {

    private final IPDSurgeryService ipdSurgeryService;
    private static final Logger logger = LoggerFactory.getLogger(IPDSurgeryController.class);


    @PostMapping(EndpointConstants.IPD_SURGERY_FORM)
    public ResponseEntity<AppResponseDTO> createSurgeryForm(@RequestBody IPDSurgeryReqDTO requestDTO) {
        logger.info("Received request to create IPD Surgery Form for Admission ID: {}", requestDTO.getIpdAdmissionId());
        return ResponseEntity.ok(AppResponseDTO.ok(ipdSurgeryService.createIPDSurgeryForm(requestDTO)));
    }

    @GetMapping(EndpointConstants.IPD_SURGERY_FORM_BY_ID)
    public ResponseEntity<AppResponseDTO> getSurgeryFormById(@PathVariable String id) {
        logger.info("Received request to fetch IPD Surgery Form with ID: {}", id);
        return ResponseEntity.ok(AppResponseDTO.ok(ipdSurgeryService.getSurgeryFormById(id)));
    }

    @GetMapping(EndpointConstants.IPD_SURGERIES)
    public ResponseEntity<AppResponseDTO> getSurgeryForms(
            @RequestParam String orgId,
            @RequestParam(required = false) String ipdAdmissionId) {
        logger.info("Received request to fetch IPD Surgery Forms for Admission ID: {} or Organization :{} ", ipdAdmissionId, orgId);
        return ResponseEntity.ok(AppResponseDTO.ok(ipdSurgeryService.getSurgeryFormsByIpdAdmission(orgId, ipdAdmissionId)));
    }

    @PutMapping(EndpointConstants.IPD_SURGERY_FORM_BY_ID)
    public ResponseEntity<AppResponseDTO> updateSurgeryForm(
            @RequestBody IPDSurgeryDTO requestDTO) {
        logger.info("Received request to update IPD Surgery Form with ID: {}", requestDTO.getId());
        return ResponseEntity.ok(AppResponseDTO.ok(ipdSurgeryService.updateSurgeryForm(requestDTO)));
    }

    @DeleteMapping(EndpointConstants.IPD_SURGERY_FORM_BY_ID)
    public ResponseEntity<AppResponseDTO> deleteSurgeryForm(@PathVariable String id) {
        logger.info("Received request to delete IPD Surgery Form with ID: {}", id);
        String deletedId = ipdSurgeryService.deleteSurgeryForm(id);
        return ResponseEntity.ok(AppResponseDTO.ok(deletedId));
    }

    @PostMapping(EndpointConstants.IPD_SURGERY_CANCEL)
    public ResponseEntity<AppResponseDTO> cancelSurgery(@PathVariable String surgeryId, @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(AppResponseDTO.ok(ipdSurgeryService.cancelIPDSurgery(surgeryId, reason)));
    }
}
