package com.org.hosply360.controller.IPD;

import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.IPDDTO.IPDDischargeFormReqDTO;
import com.org.hosply360.dto.OPDDTO.PdfResponseDTO;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.service.IPD.IPDDischargeFormService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<AppResponseDTO> downloadDischargeFormPDF(@RequestParam String dischargeFormId) {
        PdfResponseDTO response = ipdDischargeFormService.generateDischargeFormPdf(dischargeFormId);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(AppResponseDTO.ok(response));
    }
}
