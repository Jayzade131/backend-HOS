package com.org.hosply360.controller.IPD;


import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.IPDDTO.IPDAdmissionDTO;
import com.org.hosply360.dto.IPDDTO.IPDAdmissionReqDTO;
import com.org.hosply360.dto.IPDDTO.IPDAdmissionStatusReqDTO;
import com.org.hosply360.dto.IPDDTO.IPDPatientListDTO;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.service.IPD.IPDAdmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping(EndpointConstants.IPD_API)
@RequiredArgsConstructor
public class IPDAdmissionController {

    private final IPDAdmissionService ipdAdmissionService;

    @PostMapping(EndpointConstants.IPD_ADMISSION)
    public ResponseEntity<AppResponseDTO> saveAdmission(@RequestBody IPDAdmissionReqDTO ipdAdmissionDTO) {
        return ResponseEntity.ok(AppResponseDTO.ok(ipdAdmissionService.createAdmission(ipdAdmissionDTO)));
    }

    @PutMapping(EndpointConstants.IPD_ADMISSION)
    public ResponseEntity<AppResponseDTO> updateAdmission(@RequestBody IPDAdmissionReqDTO ipdAdmissionDTO) {
        return ResponseEntity.ok(AppResponseDTO.ok(ipdAdmissionService.updateAdmission(ipdAdmissionDTO)));
    }

    @PutMapping(EndpointConstants.IPD_ADMISSION_CANCEL)
    public ResponseEntity<AppResponseDTO> cancelAdmission(@RequestBody IPDAdmissionStatusReqDTO requestDTO) {
        return ResponseEntity.ok(AppResponseDTO.ok(ipdAdmissionService.cancelAdmission(requestDTO)));
    }

    @GetMapping(EndpointConstants.IPD_BEDS_BY_WARD)
    public ResponseEntity<AppResponseDTO> getBedsByWard(
            @RequestParam String orgId,
            @RequestParam String wardId) {
        return ResponseEntity.ok(AppResponseDTO.ok(ipdAdmissionService.getBedsByWardId(orgId, wardId)));
    }

    @GetMapping(EndpointConstants.IPD_ADMISSION_FILTERS)
    public ResponseEntity<AppResponseDTO> getAdmissions(
            @RequestParam String orgId,
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String wardId,
            @RequestParam(required = false) String ipdStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = ApplicationConstant.DEFAULT_VALUE_PAGE) int page,
            @RequestParam(defaultValue = ApplicationConstant.DEFAULT_VALUE_SIZE) int size
    ) {
        int actualPage = (page > 0) ? page - 1 : 0;

        Page<IPDAdmissionDTO> admissions =
                ipdAdmissionService.getAdmissions(orgId, id, wardId, ipdStatus, fromDate, toDate, actualPage, size);

        return ResponseEntity.ok(
                AppResponseDTO.getOk(
                        admissions.getContent(),
                        admissions.getTotalElements(),
                        admissions.getTotalPages(),
                        page
                )
        );
    }

    @GetMapping(EndpointConstants.IPD_PATIENT_LIST)
    public ResponseEntity<AppResponseDTO> getPatientList(
            @RequestParam String orgId,
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String wardId,
            @RequestParam(required = false) String ipdStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = ApplicationConstant.DEFAULT_VALUE_PAGE) int page,
            @RequestParam(defaultValue = ApplicationConstant.DEFAULT_VALUE_SIZE) int size
    ) {
        int actualPage = (page > 0) ? page - 1 : 0;

        Page<IPDPatientListDTO> admissions =
                ipdAdmissionService.getPatientList(orgId, id, wardId, ipdStatus, fromDate, toDate, actualPage, size);

        return ResponseEntity.ok(
                AppResponseDTO.getOk(
                        admissions.getContent(),
                        admissions.getTotalElements(),
                        admissions.getTotalPages(),
                        page
                )
        );
    }

    @GetMapping(EndpointConstants.IPD_BARCODE)
    public ResponseEntity<AppResponseDTO> getBedsByWard(
            @PathVariable String ipdAdmissionId) {
        return ResponseEntity.ok(AppResponseDTO.ok(ipdAdmissionService.getIpdBarcode(ipdAdmissionId)));
    }

    @GetMapping(EndpointConstants.IPD_ADMISSION_RECORD)
    public ResponseEntity<AppResponseDTO> getAdmissionRecord(
            @PathVariable String ipdAdmissionId) {
        return ResponseEntity.ok(AppResponseDTO.ok(ipdAdmissionService.getAdmissionRecord(ipdAdmissionId)));
    }


}
