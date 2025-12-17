package com.org.hosply360.controller.IPD;

import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.OPDDTO.PdfResponseDTO;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.service.IPD.IPDReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping(EndpointConstants.IPD_API)
@RequiredArgsConstructor
public class IPDReceiptController {
    private final IPDReceiptService receiptService;

    @GetMapping(EndpointConstants.IPD_RECEIPT)
    public ResponseEntity<AppResponseDTO> getReceipts(
            @RequestParam String orgId,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(required = false) String ipdAdmissionId,
            @RequestParam(required = false) String ipdReceiptId,
            @RequestParam(required = false) String receiptType) {
        return ResponseEntity.ok(AppResponseDTO.ok(receiptService.getReceipts(orgId, fromDate, toDate, ipdAdmissionId, ipdReceiptId,receiptType)));
    }

    @GetMapping(EndpointConstants.IPD_RECEIPT_DOWNLOAD)
    public ResponseEntity<AppResponseDTO> downloadReceipt(@RequestParam String receiptId) {
        PdfResponseDTO pdfResponseDTO = receiptService.generateReceiptPdf(receiptId);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(AppResponseDTO.ok(pdfResponseDTO));
    }
}
