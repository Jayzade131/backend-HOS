package com.org.hosply360.controller.IPD;

import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.IPDDTO.IPDFinalBillDiscountUpdateDTO;
import com.org.hosply360.dto.IPDDTO.IPDFinalBillReqDTO;
import com.org.hosply360.dto.IPDDTO.IPDFinalBillingPaymentDTO;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.service.IPD.IPDFinalBillService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsible for handling IPD Final Billing operations.
 * <p>
 * This controller manages the complete lifecycle of an IPD Final Bill — including
 * creation, retrieval, payment, discount updates, refreshing, and PDF generation
 * for both full and summary views.
 * </p>
 *
 * <p><b>Module:</b> In-Patient Department (IPD)</p>
 * <p><b>Entity:</b> IPDFinalBill</p>
 *
 * <p>All responses are wrapped in a standardized {@link AppResponseDTO} object.</p>
 */
@RestController
@RequestMapping(EndpointConstants.IPD_API)
@RequiredArgsConstructor
public class IPDFinalBillController {

    private static final Logger logger = LoggerFactory.getLogger(IPDFinalBillController.class);

    private final IPDFinalBillService finalBillService;

    /**
     * Creates a new Final Bill for a given IPD Admission.
     * <p>
     * This endpoint consolidates all provisional charges, consultant fees,
     * procedure costs, and additional services into a final payable amount.
     * </p>
     *
     * @param requestDto DTO containing admission ID and billing data
     * @return Response indicating successful final bill creation
     */
    @PostMapping(EndpointConstants.CREATE_FINAL_BILL)
    public ResponseEntity<AppResponseDTO> createFinalBill(@RequestBody IPDFinalBillReqDTO requestDto) {
        logger.info("Received request to create final bill for admission ID: {}", requestDto.getAdmissionId());
        return ResponseEntity.ok(AppResponseDTO.ok(finalBillService.createFinalBill(requestDto)));
    }

    /**
     * Fetches an existing Final Bill based on the Admission ID.
     *
     * @param admissionId ID of the IPD admission
     * @return Response containing complete Final Bill details
     */
    @GetMapping(EndpointConstants.GET_FINAL_BILL)
    public ResponseEntity<AppResponseDTO> getFinalBill(@PathVariable String admissionId) {
        logger.info("Received request to fetch final bill for admission ID: {}", admissionId);
        return ResponseEntity.ok(AppResponseDTO.ok(finalBillService.getFinalBillByAdmissionId(admissionId)));
    }

    /**
     * Processes the payment for the Final Bill.
     * <p>
     * Records payment details, updates the paid/balance amounts, and optionally
     * logs payment history for audit purposes.
     * </p>
     *
     * @param paymentDto DTO containing admission ID, payment mode, and amount paid
     * @return Response indicating successful payment posting
     */
    @PostMapping(EndpointConstants.FINAL_BILL_PAYMENT)
    public ResponseEntity<AppResponseDTO> makeFinalBillPayment(@RequestBody IPDFinalBillingPaymentDTO paymentDto) {
        logger.info("Received final bill payment for Admission ID: {}, Amount: {}", paymentDto.getAdmissionId(), paymentDto.getAmountPaid());
        return ResponseEntity.ok(AppResponseDTO.ok(finalBillService.makeFinalBillPayment(paymentDto)));
    }

    /**
     * Retrieves a summarized view of the Final Bill.
     * <p>
     * Used mainly for overview screens and reports. Provides total charges,
     * discounts, paid amount, balance, and settlement status.
     * </p>
     *
     * @param admissionId ID of the IPD admission
     * @return Response containing summarized Final Bill data
     */
    @GetMapping(EndpointConstants.FINAL_BILL_SUMMARY)
    public ResponseEntity<AppResponseDTO> getFinalBillSummary(@PathVariable String admissionId) {
        logger.info("Received request for Final Bill Summary, Admission ID: {}", admissionId);
        return ResponseEntity.ok(AppResponseDTO.ok(finalBillService.getFinalBillSummary(admissionId)));
    }

    /**
     * Generates and downloads a Summary PDF for the Final Bill.
     * <p>
     * This PDF includes a compact version of the Final Bill suitable for printing
     * or sharing with patients.
     * </p>
     *
     * @param admissionId ID of the IPD admission
     * @param orgId       Organization ID (used for logo, header, etc.)
     * @return Response containing binary or encoded PDF data
     */
    @GetMapping(EndpointConstants.FINAL_BILL_SUMMARY_PDF)
    public ResponseEntity<AppResponseDTO> downloadFinalBillSummaryPdf(
            @PathVariable String admissionId,
            @PathVariable String orgId) {
        logger.info("Received request to download Final Bill Summary PDF for Admission ID: {}", admissionId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(AppResponseDTO.ok(finalBillService.generateFinalBillSummaryPdf(admissionId, orgId)));
    }

    /**
     * Generates and downloads a detailed Final Bill PDF.
     * <p>
     * This version includes all charge breakdowns, consultant fees,
     * investigations, consumables, and other billing heads.
     * </p>
     *
     * @param admissionId ID of the IPD admission
     * @param orgId       Organization ID (used for branding or hospital details)
     * @return Response containing the complete Final Bill PDF
     */
    @GetMapping(EndpointConstants.FINAL_BILL_PDF)
    public ResponseEntity<AppResponseDTO> downloadFinalBillPdf(
            @PathVariable String admissionId,
            @PathVariable String orgId) {
        logger.info("Received request to download Full Final Bill PDF for Admission ID: {}", admissionId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(AppResponseDTO.ok(finalBillService.generateFinalBillPdf(admissionId, orgId)));
    }

    /**
     * Updates the discount applied to a Final Bill.
     * <p>
     * Allows authorized users to modify additional discounts before settlement,
     * provided the bill is not locked or settled.
     * </p>
     *
     * @param requestDto DTO containing admission ID and discount update details
     * @return Response indicating success or validation failure
     */
    @PutMapping(EndpointConstants.UPDATE_FINAL_BILL_DISCOUNT)
    public ResponseEntity<AppResponseDTO> updateFinalBillDiscount(@RequestBody IPDFinalBillDiscountUpdateDTO requestDto) {
        logger.info("Received request to update final bill discount for Admission ID: {}", requestDto.getAdmissionId());
        return ResponseEntity.ok(AppResponseDTO.ok(finalBillService.updateFinalBillDiscount(requestDto)));
    }

    /**
     * Refreshes the Final Bill for a given Admission.
     * <p>
     * This action recalculates charges if any provisional entries, procedures,
     * or medicines were added after the initial bill generation. It maintains
     * consistency between provisional and final billing data.
     * </p>
     *
     * @param admissionId ID of the IPD admission
     * @return Response confirming successful bill refresh
     */
    @PutMapping(EndpointConstants.REFRESH_FINAL_BILL)
    public ResponseEntity<AppResponseDTO> refreshFinalBill(@PathVariable String admissionId) {
        logger.info("Received request to refresh final bill for admission ID: {}", admissionId);
        return ResponseEntity.ok(AppResponseDTO.ok(finalBillService.refreshFinalBill(admissionId)));
    }
}
