package com.org.hosply360.controller.frontDesk;

import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.dto.frontDeskDTO.PatientReqDTO;
import com.org.hosply360.dto.frontDeskDTO.PatientResponseDTO;
import com.org.hosply360.service.frontdesk.PatientMasterService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(EndpointConstants.FRONTDESK_API)
@RequiredArgsConstructor
public class PatientController {
    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    private final PatientMasterService patientMasterService;


    @PostMapping(EndpointConstants.CREATE_PATIENT_API)
    public ResponseEntity<AppResponseDTO> createPatient(@RequestBody PatientReqDTO patientDTO) {
        logger.info("Creating or updating patient: {}", patientDTO);

        return ResponseEntity.ok(AppResponseDTO.ok(patientMasterService.createOrUpdatePatient(patientDTO)));
    }


    @GetMapping(EndpointConstants.GET_PATIENTS_API)
    public ResponseEntity<AppResponseDTO> getAllPatients(@RequestParam String organizationId) {

        List<PatientResponseDTO> patientList = patientMasterService.getAllPatients(organizationId);

        return ResponseEntity.ok(
                AppResponseDTO.getOk2(patientList)
        );
    }


    @GetMapping(EndpointConstants.GET_PATIENT_API)
    public ResponseEntity<AppResponseDTO> getPatientByID(@PathVariable String id) {
        logger.info("Fetching patient with ID: {}", id);
        return ResponseEntity.ok(AppResponseDTO.ok(patientMasterService.getPatient(id)));
    }

    @DeleteMapping(EndpointConstants.DELETE_PATIENT_API)
    public ResponseEntity<AppResponseDTO> deletePatient(@PathVariable String id) {
        logger.info("Deleting patient with ID: {}", id);
        patientMasterService.deletePatient(id);
        return ResponseEntity.ok(AppResponseDTO.customStatus(ApplicationConstant.DELETED_SUCCESSFULLY, HttpStatus.OK));
    }

    @GetMapping(EndpointConstants.FETCH_ALL_PATIENT)
    public ResponseEntity<AppResponseDTO> fetchAllPatient(@PathVariable String organizationId) {

        logger.info("Fetching all patients");
        return ResponseEntity.ok(AppResponseDTO.ok(patientMasterService.fetchAllPatient(organizationId)));
    }
}
