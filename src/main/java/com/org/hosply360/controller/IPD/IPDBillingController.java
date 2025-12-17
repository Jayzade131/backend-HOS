package com.org.hosply360.controller.IPD;

import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.IPDDTO.CancelBillingItemsRequestDTO;
import com.org.hosply360.dto.IPDDTO.IPDBillingDTO;
import com.org.hosply360.dto.IPDDTO.IPDBillingPaymentDTO;
import com.org.hosply360.dto.IPDDTO.IPDBillingReqDTO;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.service.IPD.IPDBillingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(EndpointConstants.IPD_API)
@RequiredArgsConstructor
public class IPDBillingController {


    private final IPDBillingService ipdBillingService;
    private static final Logger logger = LoggerFactory.getLogger(IPDBillingController.class);

    @PostMapping(EndpointConstants.IPD_BILLING_CREATE)
    public ResponseEntity<AppResponseDTO> createBilling(@RequestBody IPDBillingReqDTO billingDTO) {
        logger.info("Received request to create IPD Billing");
        return ResponseEntity.ok(AppResponseDTO.ok(ipdBillingService.createIPDBilling(billingDTO)));
    }

    @GetMapping(EndpointConstants.GET_IPD_BILLINGS)
    public ResponseEntity<AppResponseDTO> getAllIPDBillings(
         @RequestParam String organizationId,
         @RequestParam(required = false) String admissionId,
         @RequestParam(required = false) String id
    ) {
        List<IPDBillingDTO> list = ipdBillingService.getAllIPDBillings(organizationId, admissionId, id);
        return ResponseEntity.ok(AppResponseDTO.ok(list));
    }


    @PutMapping(EndpointConstants.IPD_BILLING_UPDATE)
    public ResponseEntity<AppResponseDTO> updateBilling(@RequestBody IPDBillingDTO dto) {
        return ResponseEntity.ok(AppResponseDTO.ok(ipdBillingService.updateIPDBilling(dto.getId(), dto)));
    }

    // 🔹 Cancel entire billing
    @PutMapping(EndpointConstants.IPD_BILLING_CANCEL)
    public ResponseEntity<AppResponseDTO> cancelBilling(@PathVariable String billingId, @RequestParam(required = false) String reason) {
        logger.info("Received request to cancel billing ID: {}", billingId);
        return ResponseEntity.ok(AppResponseDTO.ok(ipdBillingService.cancelIPDBilling(billingId, reason)));
    }

    // 🔹 Cancel multiple billing items in a billing
    @PutMapping(EndpointConstants.IPD_BILLING_ITEM_CANCEL)
    public ResponseEntity<AppResponseDTO> cancelMultipleBillingItems(
            @RequestBody CancelBillingItemsRequestDTO request) {

        logger.info("Received request to cancel billing items {} in billing ID: {}", request.getBillingItemIds(), request.getBillingId());
        return ResponseEntity.ok(
                AppResponseDTO.ok(ipdBillingService.cancelMultipleIPDBillingItems(request.getBillingId(), request))
        );
    }

    @PostMapping(EndpointConstants.BILLING_PAYMENT)
    public ResponseEntity<AppResponseDTO> billPayment( @RequestBody IPDBillingPaymentDTO paymentDTO) {
        logger.info("Processing the payment for the  bill {} ",paymentDTO.getBillingId());
        return ResponseEntity.ok(AppResponseDTO.ok(ipdBillingService.billPayment(paymentDTO.getBillingId(), paymentDTO)));

    }

    @GetMapping(EndpointConstants.DOWNLOAD_IPD_BILL_API)
    public ResponseEntity<AppResponseDTO> downloadIPDBill(
            @PathVariable("id") String id,
            @PathVariable("orgId") String orgId) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(AppResponseDTO.ok(ipdBillingService.generateIPDBillingPdf(id, orgId)));
    }



}
