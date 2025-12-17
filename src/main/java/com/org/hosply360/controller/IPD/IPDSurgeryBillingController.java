package com.org.hosply360.controller.IPD;

import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.IPDDTO.IPDBillingPaymentDTO;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.service.IPD.IPDSurgeryBillingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class IPDSurgeryBillingController {
    private static final Logger logger = LoggerFactory.getLogger(IPDSurgeryBillingController.class);
    private final IPDSurgeryBillingService ipdSurgeryBillingService;

    @GetMapping(EndpointConstants.GET_IPD_SURGERY_BILLINGS)
    public ResponseEntity<AppResponseDTO> getAllIPDSurgeryBillings(
            @RequestParam String organizationId,
            @RequestParam(required = false) String admissionId,
            @RequestParam(required = false) String id
    ) {
        return ResponseEntity.ok(AppResponseDTO.ok(ipdSurgeryBillingService.getAllIPDSurgeryBillings(organizationId, admissionId, id)));
    }
    @PostMapping(EndpointConstants.IPD_SURGERY_BILL)
    public ResponseEntity<AppResponseDTO> surgeryBillPayment( @RequestBody IPDBillingPaymentDTO paymentDTO) {
        logger.info("Processing the payment for the  bill {} ",paymentDTO.getBillingId());
        return ResponseEntity.ok(AppResponseDTO.ok(ipdSurgeryBillingService.surgeryBillPayment(paymentDTO)));
    }
}
