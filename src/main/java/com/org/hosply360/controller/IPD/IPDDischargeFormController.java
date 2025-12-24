package com.org.hosply360.controller.IPD;

import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.IPDDTO.DischargeFormPdfResponseDTO;
import com.org.hosply360.dto.IPDDTO.IPDDischargeFormReqDTO;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.service.IPD.IPDDischargeFormService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(EndpointConstants.IPD_API)
@RequiredArgsConstructor
public class IPDDischargeFormController {
    private final IPDDischargeFormService ipdDischargeFormService;

    @PostMapping(EndpointConstants.IPD_DISCHARGE_FORM)
    public ResponseEntity<AppResponseDTO> createDischargeForm(@RequestBody IPDDischargeFormReqDTO requestDTO) {
        return ResponseEntity.ok(AppResponseDTO.ok(ipdDischargeFormService.createDischargeFrom(requestDTO)));
    }

    @GetMapping(EndpointConstants.IPD_DISCHARGE_RECEIPT)
    public ResponseEntity<DischargeFormPdfResponseDTO> downloadDischargeFormPDF(@PathVariable String dischargeFormId) {
        return ResponseEntity.ok(ipdDischargeFormService.getDischargeFormPdf(dischargeFormId));
    }
}
