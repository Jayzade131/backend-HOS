package com.org.hosply360.controller.OPD;

import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.OPDDTO.PrescriptionDTO;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.service.OPD.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(EndpointConstants.FRONTDESK_API)
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping(EndpointConstants.PRESCRIPTION_API)
    public ResponseEntity<AppResponseDTO> createUpdatePrescription(@RequestBody PrescriptionDTO prescriptionDTO) {
        return ResponseEntity.ok(AppResponseDTO.ok(prescriptionService.createUpdatePrescription(prescriptionDTO)));
    }

    @GetMapping(EndpointConstants.PRESCRIPTION_HISTORY_BY_PATIENT)
    public ResponseEntity<AppResponseDTO> getPrescriptionForPatient(@RequestParam String patientId, @RequestParam String orgId, @RequestParam(defaultValue = ApplicationConstant.DEFAULT_VALUE_SIZE) int size) {
        return ResponseEntity.ok(AppResponseDTO.ok(prescriptionService.getPrescriptionByPatientId(patientId, orgId, size)));
    }

    @GetMapping(EndpointConstants.DOWNLOAD_PRESCRIPTION)
    public ResponseEntity<AppResponseDTO> downloadPrescription(
            @RequestParam String prescriptionId,
            @RequestParam String organizationId) {
        return ResponseEntity.ok(AppResponseDTO.ok(prescriptionService.generatePrescriptionPdf(prescriptionId, organizationId)));


    }


    @GetMapping(EndpointConstants.UPCOMING_PATIENT_BY_VISIT_DATE)
    public ResponseEntity<AppResponseDTO> getPatientsByNextVisit(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) String doctorId) {

        return ResponseEntity.ok(AppResponseDTO.ok(prescriptionService.getPatientsByNextVisitRange(fromDate, toDate, doctorId)));
    }


}
