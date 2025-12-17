package com.org.hosply360.controller.IPD;

import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.IPDDTO.IpdTransferDTO;
import com.org.hosply360.dto.OPDDTO.PdfResponseDTO;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.service.IPD.IPDTransferService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(EndpointConstants.IPD_API)
@RequiredArgsConstructor
public class IPDTransferController {
    private final IPDTransferService ipdTransferService;
    private static final Logger logger = LoggerFactory.getLogger(IPDTransferController.class);

    @PostMapping(EndpointConstants.IPD_TRANSFER)
    public ResponseEntity<AppResponseDTO> createIpdTransfer(@RequestBody IpdTransferDTO requestDTO) {
        logger.info("Received request to create IPD transfer for Admission ID: {}", requestDTO.getIpdAdmissionId());
        return ResponseEntity.ok(AppResponseDTO.ok(ipdTransferService.createIpdTransfer(requestDTO)));
    }

    @GetMapping(EndpointConstants.IPD_TRANSFER_BY_ADMISSION)
    public ResponseEntity<AppResponseDTO> getTransfersByIpdAdmission(@PathVariable String ipdAdmissionId) {
        logger.info("Received request to get transfers for IPD Admission ID: {}", ipdAdmissionId);
        return ResponseEntity.ok(AppResponseDTO.ok(ipdTransferService.getTransfersByIpdAdmission(ipdAdmissionId)));
    }

    @GetMapping(EndpointConstants.IPD_TRANSFER_RECEIPT)
    public ResponseEntity<AppResponseDTO> downloadTransferReceiptPDF(@RequestParam String transferId) {
        PdfResponseDTO pdfResponseDTO = ipdTransferService.generateTransferReceiptPdf(transferId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(AppResponseDTO.ok(pdfResponseDTO));
    }


}
