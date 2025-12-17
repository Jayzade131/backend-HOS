package com.org.hosply360.controller.OPD;

import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.dto.OPDDTO.OPDPaymentHistoryDTO;
import com.org.hosply360.dto.OPDDTO.OPDPaymentHistoryReqDTO;
import com.org.hosply360.service.OPD.OPDPaymentHistoryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(EndpointConstants.FRONTDESK_API)
@RequiredArgsConstructor
public class OPDPaymentHistoryController {

    private static final Logger logger = LoggerFactory.getLogger(OPDPaymentHistoryController.class);

    private final OPDPaymentHistoryService paymentHistoryService;

    @PostMapping
    public ResponseEntity<AppResponseDTO> save(@RequestBody OPDPaymentHistoryReqDTO dto) {
        logger.info("Saving OPD Payment History for Invoice ID: {}", dto.getInvoiceId());
        OPDPaymentHistoryDTO saved = paymentHistoryService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(AppResponseDTO.ok(saved));
    }

    @GetMapping(EndpointConstants.GET_OPD_PAYMENT_HISTORY_API)
    public ResponseEntity<AppResponseDTO> getByInvoiceId(@PathVariable String invoiceId) {
        logger.info("Fetching OPD Payment History for Invoice ID: {}", invoiceId);
        List<OPDPaymentHistoryDTO> list = paymentHistoryService.getByInvoiceId(invoiceId);
        return ResponseEntity.ok(AppResponseDTO.ok(list));
    }
}
