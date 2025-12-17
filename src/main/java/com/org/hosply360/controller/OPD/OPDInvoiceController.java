package com.org.hosply360.controller.OPD;

import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.OPDDTO.InvoiceResponseDTO;
import com.org.hosply360.dto.OPDDTO.OPDInvoiceDTO;
import com.org.hosply360.dto.OPDDTO.OPDInvoicePaymentUpdateDTO;
import com.org.hosply360.dto.OPDDTO.OPDInvoiceReqDTO;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.service.OPD.OPDInvoiceService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(EndpointConstants.FRONTDESK_API)
@RequiredArgsConstructor
public class OPDInvoiceController {

    private static final Logger logger = LoggerFactory.getLogger(OPDInvoiceController.class);
    private final OPDInvoiceService opdInvoiceService;

    @PostMapping(EndpointConstants.CREATE_OPD_INVOICE_API)
    public ResponseEntity<AppResponseDTO> createInvoice(@RequestBody OPDInvoiceReqDTO dto) {
        logger.info("Creating OPD Invoice for patient ID: {}", dto.getPatientId());
        String created = opdInvoiceService.createInvoice(dto);
        return ResponseEntity.ok(AppResponseDTO.ok(created));
    }

    @PutMapping(EndpointConstants.UPDATE_OPD_INVOICE_API)
    public ResponseEntity<AppResponseDTO> updateInvoice(@RequestBody OPDInvoiceReqDTO dto) {
        logger.info("Updating OPD Invoice with ID: {}", dto.getId());
        OPDInvoiceDTO updated = opdInvoiceService.updateInvoice(dto);
        return ResponseEntity.ok(AppResponseDTO.ok(updated));
    }

    @GetMapping(EndpointConstants.GET_OPD_INVOICE_BY_ID_API)
    public ResponseEntity<AppResponseDTO> getInvoice(@PathVariable String id, @PathVariable String orgId) {
        logger.info("Fetching OPD Invoice with ID: {} for Org: {}", id, orgId);
        OPDInvoiceDTO invoice = opdInvoiceService.getInvoiceById(id, orgId);
        return ResponseEntity.ok(AppResponseDTO.ok(invoice));
    }

    @GetMapping(EndpointConstants.GET_OPD_INVOICES_BY_FILTERS)
    public ResponseEntity<AppResponseDTO> getFilteredInvoices(
            @RequestParam String orgId,
            @RequestParam(required = false) String patientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        logger.info("Fetching OPD Invoices | orgId: {}, patientId: {}, from: {}, to: {}", orgId, patientId, fromDate, toDate);
        List<OPDInvoiceDTO> invoiceList = opdInvoiceService.getInvoicesByFilters(orgId, patientId, fromDate, toDate);
        return ResponseEntity.ok(AppResponseDTO.ok(invoiceList));
    }

    @DeleteMapping(EndpointConstants.DELETE_OPD_INVOICE_API)
    public ResponseEntity<AppResponseDTO> deleteInvoice(@PathVariable("id") String id, @PathVariable("orgId") String orgId) {
        logger.info("Deleting OPD Invoice with ID: {}, OrgID: {}", id, orgId);
        opdInvoiceService.deleteInvoice(id, orgId);
        return ResponseEntity.ok(AppResponseDTO.customStatus(ApplicationConstant.DELETED_SUCCESSFULLY, HttpStatus.OK));
    }


    @PutMapping(EndpointConstants.INVOICE_PAYMENT)
    public ResponseEntity<AppResponseDTO> updatePaidAmount(@RequestBody OPDInvoicePaymentUpdateDTO dto) {
        logger.info("Updating payment and generating receipt for Invoice ID: {}", dto.getInvoiceId());
        return ResponseEntity.ok(AppResponseDTO.ok(opdInvoiceService.updatePaidAmount(dto)));
    }

    @GetMapping(EndpointConstants.GET_OPD_RECEIPT_API)
    public ResponseEntity<AppResponseDTO> getReciptData(@PathVariable String receiptId) {
        return ResponseEntity.ok(
                AppResponseDTO.ok(opdInvoiceService.getReceiptData(receiptId))
        );
    }

    @GetMapping("/with-appointments")
    public ResponseEntity<AppResponseDTO> getInvoicesWithAppointments(
            @RequestParam String orgId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        List<OPDInvoiceDTO> result = opdInvoiceService.getInvoicesWithAppointments(orgId, fromDate, toDate);
        return ResponseEntity.ok(AppResponseDTO.ok(result));
    }

    @GetMapping(EndpointConstants.GET_OPD_INVOICE_DETAILS_API)
    public ResponseEntity<AppResponseDTO> getInvoiceDetails(
            @PathVariable("identifier") String identifier) {

        logger.info("Fetching OPD Invoice details (JSON only) | Identifier: {}", identifier);

        InvoiceResponseDTO response = opdInvoiceService.generateInvoiceDetailsByIdentifier(identifier);

        return ResponseEntity.ok(AppResponseDTO.ok(response));
    }


}
