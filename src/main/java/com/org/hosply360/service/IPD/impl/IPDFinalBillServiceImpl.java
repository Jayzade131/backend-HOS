package com.org.hosply360.service.IPD.impl;

import com.org.hosply360.constant.Enums.PaymentMode;
import com.org.hosply360.constant.Enums.ReceiptType;
import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.IPD.IPDAdmission;
import com.org.hosply360.dao.IPD.IPDBilling;
import com.org.hosply360.dao.IPD.IPDFinalBill;
import com.org.hosply360.dao.IPD.IPDFinancialSummary;
import com.org.hosply360.dao.IPD.IPDSurgeryBilling;
import com.org.hosply360.dto.IPDDTO.IPDBillingDTO;
import com.org.hosply360.dto.IPDDTO.IPDBillingItemDTO;
import com.org.hosply360.dto.IPDDTO.IPDFinalBillDTO;
import com.org.hosply360.dto.IPDDTO.IPDFinalBillDiscountUpdateDTO;
import com.org.hosply360.dto.IPDDTO.IPDFinalBillFullResDTO;
import com.org.hosply360.dto.IPDDTO.IPDFinalBillReqDTO;
import com.org.hosply360.dto.IPDDTO.IPDFinalBillSummaryDTO;
import com.org.hosply360.dto.IPDDTO.IPDFinalBillingPaymentDTO;
import com.org.hosply360.dto.IPDDTO.IPDReceiptReqDTO;
import com.org.hosply360.dto.IPDDTO.IPDSurgeryBillingDTO;
import com.org.hosply360.dto.IPDDTO.ParticipantChargeDTO;
import com.org.hosply360.dto.OPDDTO.PdfResponseDTO;
import com.org.hosply360.exception.IPDException;
import com.org.hosply360.repository.IPD.IPDAdmissionRepository;
import com.org.hosply360.repository.IPD.IPDBillingRepository;
import com.org.hosply360.repository.IPD.IPDFinalBillRepository;
import com.org.hosply360.repository.IPD.IPDFinancialSummaryRepository;
import com.org.hosply360.repository.IPD.IPDSurgeryBillingRepository;
import com.org.hosply360.service.IPD.IPDFinalBillService;
import com.org.hosply360.service.IPD.IPDFinancialSummaryService;
import com.org.hosply360.service.IPD.IPDReceiptService;
import com.org.hosply360.util.Others.EntityFetcherUtil;
import com.org.hosply360.util.PDFGenUtil.IPD.IPDFinalBillPdfGenerator;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.org.hosply360.util.Others.AgeUtil.getAge;

@Service
@RequiredArgsConstructor
public class IPDFinalBillServiceImpl implements IPDFinalBillService {

    private static final Logger logger = LoggerFactory.getLogger(IPDFinalBillServiceImpl.class);

    private final IPDFinalBillRepository finalBillRepository;
    private final IPDFinancialSummaryRepository financialSummaryRepository;
    private final IPDFinancialSummaryService financialSummaryService;
    private final IPDAdmissionRepository admissionRepository;
    private final IPDBillingRepository billingRepository;
    private final IPDReceiptService ipdReceiptService;
    private final IPDSurgeryBillingRepository surgeryBillingRepository;
    private final EntityFetcherUtil entityFetcherUtil;
    private final IPDFinalBillPdfGenerator finalBillPdfGenerator;

    // -------------------------------
    //CREATE FINAL BILL
    // have to see the proper handeling of the additional discount like if the item is changed or added or anything and the bil in finalized so is it recalculating all the thngs properly
    // -------------------------------
    @Override
    @Transactional
    public String createFinalBill(IPDFinalBillReqDTO requestDto) {
        logger.info("Creating Final Bill for Admission ID: {}", requestDto.getAdmissionId());

        // Validate Admission
        IPDAdmission admission = entityFetcherUtil.getIPDAdmissionOrThrow(requestDto.getAdmissionId());

        if (finalBillRepository.existsByAdmission_Id(admission.getId())) {
            throw new IPDException(ErrorConstant.DUPLICATE_ENTRY, HttpStatus.CONFLICT);
        }

        // Update Summary (Apply Additional Discount & Recalculate Totals)
        financialSummaryService.updateSummaryAfterFinalBill(requestDto);

        // Build final bill (new mode)
        IPDFinalBill newBill = buildOrUpdateFinalBill(admission, null, requestDto, false);
        IPDFinalBill saved = finalBillRepository.save(newBill);
        logger.info("Final Bill created successfully for Admission ID: {} | Final Bill ID: {}", admission.getId(), saved.getId());
        return saved.getId();
    }

    @Override
    @Transactional
    public IPDFinalBillDTO refreshFinalBill(String admissionId) {
        logger.info("Refreshing Final Bill for Admission ID: {}", admissionId);

        IPDAdmission admission = entityFetcherUtil.getIPDAdmissionOrThrow(admissionId);

        IPDFinalBill existing = finalBillRepository.findByAdmission_Id(admissionId)
                .orElseThrow(() -> new IPDException(ErrorConstant.BILL_NOT_FOUND, HttpStatus.NOT_FOUND));

        // Rebuild updated bill using shared builder
        IPDFinalBill updated = buildOrUpdateFinalBill(admission, existing, null, true);
        updated.setId(existing.getId()); // preserve the existing record ID

        finalBillRepository.save(updated);
        logger.info("Final Bill refreshed successfully for Admission ID: {}", admissionId);

        return ObjectMapperUtil.copyObject(updated, IPDFinalBillDTO.class);
    }

    /**
     * Core reusable method for creating or refreshing Final Bill.
     *
     * @param admission    - linked admission
     * @param existingBill - current bill if refresh mode
     * @param requestDto   - request payload if creation mode
     * @param isRefresh    - flag indicating refresh vs create
     */
    private IPDFinalBill buildOrUpdateFinalBill(
            IPDAdmission admission,
            IPDFinalBill existingBill,
            IPDFinalBillReqDTO requestDto,
            boolean isRefresh) {

        IPDFinancialSummary summary = financialSummaryRepository.findByIpdAdmissionId(admission.getId())
                .orElseThrow(() -> new IPDException(ErrorConstant.ITEM_NOT_FOUND, HttpStatus.NOT_FOUND));

        List<IPDBilling> activeBillings = billingRepository.findByAdmissionIdAndCanceledStatus(admission.getId(), false);
        List<IPDSurgeryBilling> activeSurgeries = surgeryBillingRepository.findByAdmissionIdAndCanceledStatus(admission.getId(), false);

        BigDecimal pendingAmount = nullSafe(summary.getPendingAmount());
        BigDecimal refundBalance = calculateRefundBalance(summary);

        String remarksMessage;
        if (isRefresh) {
            remarksMessage = "Final bill refreshed successfully";
        } else if (requestDto.getRemarks() != null) {
            remarksMessage = requestDto.getRemarks();
        } else {
            remarksMessage = "Final bill generated successfully";
        }

        return IPDFinalBill.builder()
                .organizationId(isRefresh
                        ? existingBill.getOrganizationId()
                        : requestDto.getOrganizationId())
                .admission(admission)
                .finalBillNo(isRefresh
                        ? existingBill.getFinalBillNo()
                        : generateFinalBillNo(requestDto.getOrganizationId()))
                .finalBillDate(isRefresh
                        ? existingBill.getFinalBillDate()
                        : LocalDate.now())

                .totalBillAmount(nullSafe(summary.getTotalBilledAmount()))
                .totalSurgeryCharges(nullSafe(summary.getTotalSurgeryAmount()))
                .totalAdvanceAmount(nullSafe(summary.getTotalDepositAmount()))
                .totalPaidAmount(nullSafe(summary.getTotalPaidAmount()))
                .totalDiscountAmount(nullSafe(summary.getTotalDiscountAmount()))

                .additionalDiscountAmount(isRefresh
                        ? nullSafe(existingBill.getAdditionalDiscountAmount())
                        : nullSafe(requestDto.getAdditionalDiscountAmount()))

                .totalRefundAmount(nullSafe(summary.getTotalRefundedAmount()))
                .netPayableAmount(nullSafe(summary.getTotalNetAmount()))
                .balanceAmount(pendingAmount)
                .refundBalance(refundBalance)
                .hasSettled(pendingAmount.compareTo(BigDecimal.ZERO) == 0)
                .settledDate(LocalDateTime.now())
                .expenseSummary(activeBillings)
                .surgeries(activeSurgeries)
                .remarks(remarksMessage)
                .build();
    }


    @Override
    @Transactional
    public String updateFinalBillDiscount(IPDFinalBillDiscountUpdateDTO requestDto) {
        if (requestDto == null || requestDto.getAdmissionId() == null || requestDto.getAdmissionId().isBlank()) {
            throw new IPDException("Admission ID is required", HttpStatus.BAD_REQUEST);
        }

        logger.info("Updating additional discount for Admission ID: {}", requestDto.getAdmissionId());

        // Validate Admission
        IPDAdmission admission = admissionRepository.findById(requestDto.getAdmissionId())
                .orElseThrow(() -> new IPDException(ErrorConstant.ITEM_NOT_FOUND, HttpStatus.NOT_FOUND));

        // Fetch Final Bill
        IPDFinalBill finalBill = finalBillRepository.findByAdmission_Id(admission.getId())
                .orElseThrow(() -> new IPDException("Final Bill not found for given Admission ID", HttpStatus.NOT_FOUND));

        if (Boolean.TRUE.equals(finalBill.getHasSettled())) {
            logger.warn("Attempt to modify a settled Final Bill for Admission ID: {}", admission.getId());
            throw new IPDException("Cannot modify a settled Final Bill", HttpStatus.CONFLICT);
        }

        // Fetch Financial Summary
        IPDFinancialSummary summary = financialSummaryRepository.findByIpdAdmissionId(admission.getId())
                .orElseThrow(() -> new IPDException(ErrorConstant.ITEM_NOT_FOUND, HttpStatus.NOT_FOUND));

        // --- Step 1: Remove old discount effect from Financial Summary ---
        BigDecimal previousDiscount = nullSafe(finalBill.getAdditionalDiscountAmount());
        if (previousDiscount.compareTo(BigDecimal.ZERO) > 0) {
            summary.setTotalDiscountAmount(nullSafe(summary.getTotalDiscountAmount()).subtract(previousDiscount));
            summary.setTotalNetAmount(nullSafe(summary.getTotalNetAmount()).add(previousDiscount));
        }

        // --- Step 2: Apply new discount ---
        BigDecimal newDiscount = nullSafe(requestDto.getAdditionalDiscountAmount());
        if (newDiscount.compareTo(BigDecimal.ZERO) > 0) {
            summary.setTotalDiscountAmount(nullSafe(summary.getTotalDiscountAmount()).add(newDiscount));
            summary.setTotalNetAmount(nullSafe(summary.getTotalNetAmount()).subtract(newDiscount));
        }

        // --- Step 3: Update pending and derived amounts ---
        BigDecimal pendingAmount = nullSafe(summary.getTotalNetAmount())
                .subtract(nullSafe(summary.getTotalPaidAmount()))
                .max(BigDecimal.ZERO);

        summary.setPendingAmount(pendingAmount);
        summary.setLastUpdated(LocalDateTime.now());

        // Save summary
        financialSummaryRepository.save(summary);
        logger.info("Financial Summary recalculated with new discount for Admission ID: {}", admission.getId());

        // --- Step 4: Update Final Bill ---
        finalBill.setAdditionalDiscountAmount(newDiscount);
        finalBill.setTotalDiscountAmount(nullSafe(summary.getTotalDiscountAmount()));
        finalBill.setNetPayableAmount(nullSafe(summary.getTotalNetAmount()));
        finalBill.setBalanceAmount(pendingAmount);
        finalBill.setRemarks(requestDto.getRemarks() != null ? requestDto.getRemarks() : "Discount updated");
        finalBill.setUpdatedDate(LocalDateTime.now());

        finalBillRepository.save(finalBill);
        logger.info("Final Bill updated with new discount for Admission ID: {}", admission.getId());

        return "Final Bill discount and financial summary updated successfully.";
    }


    @Override
    public IPDFinalBillFullResDTO getFinalBillByAdmissionId(String admissionId) {
        logger.info("Fetching Final or Provisional Bill for Admission ID: {}", admissionId);

        // 1️ Validate admission
        IPDAdmission admission = admissionRepository.findById(admissionId)
                .orElseThrow(() -> new IPDException(ErrorConstant.ADMISSION_NOT_FOUND, HttpStatus.NOT_FOUND));

        // 2️ Try to fetch final bill (optional)
        Optional<IPDFinalBill> finalBillOpt = finalBillRepository.findByAdmission_Id(admissionId);

        // 3️ Fetch financial summary (may exist even before final bill)
        IPDFinancialSummary summary = financialSummaryRepository.findByIpdAdmissionId(admissionId).orElse(null);

        // 4️ Fetch all billing and surgery records
        List<IPDBilling> billings = billingRepository.findByAdmissionIdAndCanceledStatus(admissionId, false);
        List<IPDSurgeryBilling> surgeryBillings = surgeryBillingRepository.findByAdmissionIdAndCanceledStatus(admissionId, false);

        // 5️ Map billing details manually
        List<IPDBillingDTO> billingDTOList = billings.stream().map(bill -> {
            IPDBillingDTO dto = ObjectMapperUtil.copyObject(bill, IPDBillingDTO.class);
            dto.setAdmissionId(bill.getAdmissionId().getId());
            dto.setBillingDate(bill.getBillingDateTime().toLocalDate());

            if (bill.getBillingItems() != null) {
                dto.setBillingItems(bill.getBillingItems().stream().map(item -> IPDBillingItemDTO.builder()
                        .billingItemGroupId(item.getBillingItemGroup() != null ? item.getBillingItemGroup().getId() : null)
                        .billingItemGroupName(item.getBillingItemGroup() != null ? item.getBillingItemGroup().getItemGroupName() : null)
                        .billingItemId(item.getBillingItem() != null ? item.getBillingItem().getId() : null)
                        .billingItemName(item.getBillingItem() != null ? item.getBillingItem().getItemName() : null)
                        .quantity(item.getQuantity())
                        .rate(item.getRate())
                        .discountPercent(item.getDiscountPercent())
                        .discountAmount(item.getDiscountAmount())
                        .amount(item.getAmount())
                        .canceled(item.getItemCanceled())
                        .cancelReason(item.getCancelReason())
                        .build()).toList());
            }
            return dto;
        }).toList();

        // 6️ Copy surgery data
        List<IPDSurgeryBillingDTO> surgeryDTOList = surgeryBillings.stream()
                .map(this::mapToSurgeryBillingDTO)
                .toList();


        // 7️ Get final bill if exists
        IPDFinalBill finalBill = finalBillOpt.orElse(null);

        // 8️ Calculate refund amount using helper
        BigDecimal totalNetAmount = summary != null ? summary.getTotalNetAmount() : BigDecimal.ZERO;
        BigDecimal totalDeposit = summary != null ? summary.getTotalDepositAmount() : BigDecimal.ZERO;
        BigDecimal refundAmount = calculateRefundAmount(totalNetAmount, totalDeposit);

        // 9️ Build unified response
        IPDFinalBillFullResDTO responseDto = IPDFinalBillFullResDTO.builder()
                .billType(finalBill != null ? "FINAL" : "PROVISIONAL")

                // Patient Info
                .pId(admission.getPatient().getPId())
                .patientName(String.format("%s %s",
                        admission.getPatient().getPatientPersonalInformation().getFirstName(),
                        admission.getPatient().getPatientPersonalInformation().getLastName()))
                .gender(admission.getPatient().getPatientPersonalInformation().getGender())
                .age(getAge(admission.getPatient().getPatientPersonalInformation().getDateOfBirth()))
                .mobileNumber(admission.getPatient().getPatientContactInformation().getPrimaryPhone())
                .ipdNo(admission.getIpdNo())

                // Admission Info
                .admissionId(admission.getId())
                .consultantName(admission.getPrimaryConsultant() != null
                        ? admission.getPrimaryConsultant().getFirstName()
                        : null)
                .secondConsultantName(admission.getSecondaryConsultant() != null
                        ? admission.getSecondaryConsultant().getFirstName()
                        : null)
                .admissionDate(admission.getAdmitDateTime() != null
                        ? admission.getAdmitDateTime().toLocalDate()
                        : null)
                .wardName(admission.getWardMaster() != null ? admission.getWardMaster().getWardName() : null)
                .bedName(admission.getBedMaster() != null ? admission.getBedMaster().getBedNo() : null)

                // Bill Details (fallback to summary for provisional)
                .finalBillId(finalBill != null ? finalBill.getId() : null)
                .finalBillNo(finalBill != null ? finalBill.getFinalBillNo() : null)
                .finalBillDate(finalBill != null ? finalBill.getFinalBillDate() : LocalDate.now())
                .remarks(finalBill != null ? finalBill.getRemarks() : "Provisional Bill (Not yet finalized)")
                .hasSettled(finalBill != null && Boolean.TRUE.equals(finalBill.getHasSettled()))
                .settledDate(finalBill != null ? finalBill.getSettledDate() : null)
                .totalSurgeryCharges(summary != null ? summary.getTotalSurgeryAmount() : BigDecimal.ZERO)
                .totalBillAmount(summary != null ? summary.getTotalBilledAmount() : BigDecimal.ZERO)
                .totalAdvanceAmount(totalDeposit)
                .totalPaidAmount(summary != null ? summary.getTotalPaidAmount() : BigDecimal.ZERO)
                .totalDiscountAmount(summary != null ? summary.getTotalDiscountAmount() : BigDecimal.ZERO)
                .totalRefundAmount(refundAmount)
                .netPayableAmount(totalNetAmount)
                .balanceAmount(totalNetAmount.subtract(totalDeposit).max(BigDecimal.ZERO))
                .refundBalance(refundAmount)

                // Details
                .billingSummary(billingDTOList)
                .surgeries(surgeryDTOList)
                .build();

        logger.info("Returning {} Bill for Admission ID: {} | Refund: {}",
                finalBill != null ? "Final" : "Provisional", admissionId, refundAmount);

        return responseDto;
    }

    /**
     * Maps IPDSurgeryBilling entity to IPDSurgeryBillingDTO with nested participant details.
     */
    private IPDSurgeryBillingDTO mapToSurgeryBillingDTO(IPDSurgeryBilling surgery) {
        IPDSurgeryBillingDTO dto = IPDSurgeryBillingDTO.builder()
                .id(surgery.getId())
                .organizationId(surgery.getOrganizationId())
                .ipdAdmissionId(surgery.getIpdAdmissionId())
                .surgeryId(surgery.getSurgeryId())
                .ipdSurgeryBillNo(surgery.getIpdSurgeryBillNo())
                .billingDateTime(surgery.getBillingDateTime())
                .totalSurgeryAmount(surgery.getTotalSurgeryAmount())
                .paidAmount(surgery.getPaidAmount())
                .balanceAmount(surgery.getBalanceAmount())
                .hasCancelled(surgery.getHasCancelled())
                .hasSettled(surgery.getHasSettled())
                .build();

        // Map Surgeon Details
        if (surgery.getSurgeonDetails() != null) {
            dto.setSurgeonDetails(surgery.getSurgeonDetails().stream()
                    .map(p -> ParticipantChargeDTO.builder()
                            .doctorId(p.getId())
                            .doctorName(p.getName())
                            .charge(p.getCharge())
                            .build())
                    .toList());
        }

        // 🧑‍⚕️ Map Anaesthetist Details
        if (surgery.getAnaesthetistDetails() != null) {
            dto.setAnaesthetistDetails(surgery.getAnaesthetistDetails().stream()
                    .map(p -> ParticipantChargeDTO.builder()
                            .doctorId(p.getId())
                            .doctorName(p.getName())
                            .charge(p.getCharge())
                            .build())
                    .toList());
        }

        // 👶 Map Pediatrics Details
        if (surgery.getPediatricsDetails() != null) {
            dto.setPediatricsDetails(surgery.getPediatricsDetails().stream()
                    .map(p -> ParticipantChargeDTO.builder()
                            .doctorId(p.getId())
                            .doctorName(p.getName())
                            .charge(p.getCharge())
                            .build())
                    .toList());
        }

        return dto;
    }

    /**
     * Helper to calculate refundable amount.
     * Refund = (Deposit - NetAmount) if deposit > net else 0
     */
    private BigDecimal calculateRefundAmount(BigDecimal totalNetAmount, BigDecimal totalDepositAmount) {
        if (totalDepositAmount == null || totalNetAmount == null) {
            return BigDecimal.ZERO;
        }
        return totalDepositAmount.compareTo(totalNetAmount) > 0
                ? totalDepositAmount.subtract(totalNetAmount)
                : BigDecimal.ZERO;
    }


    // -------------------------------
    // MAKE PAYMENT / SETTLEMENT
    // -------------------------------
    @Override
    @Transactional
    public String makeFinalBillPayment(IPDFinalBillingPaymentDTO paymentDto) {
        logger.info("Processing final bill payment for Admission ID: {}", paymentDto.getAdmissionId());

        // 1️ Fetch Final Bill and Financial Summary
        IPDFinalBill finalBill = finalBillRepository.findByAdmission_Id(paymentDto.getAdmissionId())
                .orElseThrow(() -> new IPDException(ErrorConstant.ITEM_NOT_FOUND, HttpStatus.NOT_FOUND));

        IPDFinancialSummary summary = financialSummaryRepository.findByIpdAdmissionId(paymentDto.getAdmissionId())
                .orElseThrow(() -> new IPDException(ErrorConstant.ITEM_NOT_FOUND, HttpStatus.NOT_FOUND));

        // 2️ Safe monetary values
        BigDecimal totalBill = nullSafe(finalBill.getTotalBillAmount());
        BigDecimal discount = nullSafe(finalBill.getTotalDiscountAmount());
        BigDecimal refunded = nullSafe(finalBill.getTotalRefundAmount());
        BigDecimal advance = nullSafe(finalBill.getTotalAdvanceAmount());
        BigDecimal currentPaid = nullSafe(finalBill.getTotalPaidAmount());
        BigDecimal newPayment = nullSafe(paymentDto.getAmountPaid());

        // 3 Validate new payment
        if (newPayment.compareTo(BigDecimal.ZERO) < 0) {
            throw new IPDException("Invalid payment amount", HttpStatus.BAD_REQUEST);
        }

        // 4 Determine how much advance to use
        BigDecimal advanceUsed = BigDecimal.ZERO;
        if (paymentDto.isSettleUsingAdvance()) {
            BigDecimal remainingDue = totalBill
                    .subtract(currentPaid)
                    .subtract(discount)
                    .add(refunded)
                    .subtract(newPayment);
            advanceUsed = remainingDue.min(advance).max(BigDecimal.ZERO);
        }

        // 5 Compute new totals
        BigDecimal totalReceived = currentPaid.add(newPayment).add(advanceUsed);
        BigDecimal newBalance = totalBill
                .subtract(totalReceived)
                .subtract(discount)
                .add(refunded);

        boolean isSettled = newBalance.compareTo(BigDecimal.ZERO) == 0;

        // 6 Handle over-advance refund
        BigDecimal remainingAdvance = advance.subtract(advanceUsed);
        BigDecimal refundToBeIssued = BigDecimal.ZERO;
        if (isSettled && remainingAdvance.compareTo(BigDecimal.ZERO) > 0) {
            refundToBeIssued = remainingAdvance;
            finalBill.setTotalRefundAmount(nullSafe(finalBill.getTotalRefundAmount()).add(refundToBeIssued));
            summary.setTotalRefundedAmount(nullSafe(summary.getTotalRefundedAmount()).add(refundToBeIssued));
        }

        // 7 Update Financial Summary
        summary.setTotalPaidAmount(currentPaid.add(newPayment).add(advanceUsed));
        summary.setPendingAmount(newBalance.max(BigDecimal.ZERO));
        financialSummaryRepository.save(summary);

        // 8 Update Final Bill
        finalBill.setTotalPaidAmount(currentPaid.add(newPayment).add(advanceUsed));
        finalBill.setBalanceAmount(newBalance.max(BigDecimal.ZERO));
        finalBill.setHasSettled(isSettled);
        finalBill.setSettledDate(isSettled ? LocalDateTime.now() : null);
        finalBill.setTotalAdvanceAmount(advance.subtract(advanceUsed)); // adjust advance left

        StringBuilder remark = new StringBuilder();
        if (paymentDto.isSettleUsingAdvance()) {
            remark.append("Advance adjusted: ").append(advanceUsed).append(". ");
        }
        if (newPayment.compareTo(BigDecimal.ZERO) > 0) {
            remark.append("Payment received: ").append(newPayment)
                    .append(" via ").append(paymentDto.getPaymentMode()).append(". ");
        }
        if (refundToBeIssued.compareTo(BigDecimal.ZERO) > 0) {
            remark.append("Refund initiated: ").append(refundToBeIssued).append(". ");
        }
        finalBill.setRemarks(remark.toString());
        finalBillRepository.save(finalBill);

        // 9️ Generate receipt only if payment > 0
        if (newPayment.compareTo(BigDecimal.ZERO) > 0) {
            IPDReceiptReqDTO receiptReq = new IPDReceiptReqDTO();
            receiptReq.setOrganizationId(paymentDto.getOrganizationId());
            receiptReq.setAdmissionId(paymentDto.getAdmissionId());
            receiptReq.setTotalRecieveAmount(newPayment);
            receiptReq.setPaymentMode(paymentDto.getPaymentMode());
            receiptReq.setRemarks(paymentDto.getRemarks());
            receiptReq.setReceiptType(ReceiptType.FINAL_BILL);
            receiptReq.setBillingId(finalBill.getId());

            if (paymentDto.getPaymentMode() == PaymentMode.CHEQUE) {
                receiptReq.setChequeNumber(paymentDto.getChequeNumber());
                receiptReq.setChequeDateTime(paymentDto.getChequeDateTime());
                receiptReq.setBankName(paymentDto.getBankName());
                receiptReq.setBranchName(paymentDto.getBranchName());
                receiptReq.setAccountHolderName(paymentDto.getAccountHolderName());
                receiptReq.setIfscCode(paymentDto.getIfscCode());
            }

            String receiptId = ipdReceiptService.createReceipt(receiptReq);
            logger.info("Final bill payment successful for Admission ID: {} | Receipt ID: {}",
                    paymentDto.getAdmissionId(), receiptId);
            return receiptId;
        }

        logger.info("Final bill settled using advance adjustment for Admission ID: {} | Refund: {}",
                paymentDto.getAdmissionId(), refundToBeIssued);
        return finalBill.getId();
    }

    // -------------------------------
    // PDF Methods
    // -------------------------------


    @Override
    public IPDFinalBillSummaryDTO getFinalBillSummary(String admissionId) {
        logger.info("Fetching final bill summary for Admission ID: {}", admissionId);

        IPDFinalBill finalBill = finalBillRepository.findByAdmission_Id(admissionId)
                .orElseThrow(() -> new IPDException(ErrorConstant.ITEM_NOT_FOUND, HttpStatus.NOT_FOUND));

        var admission = finalBill.getAdmission();
        if (admission == null || admission.getPatient() == null) {
            throw new IPDException(ErrorConstant.ADMISSION_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        var patient = admission.getPatient();

        // Build simple summary
        return IPDFinalBillSummaryDTO.builder()
                .finalBillId(finalBill.getId())
                .finalBillNo(finalBill.getFinalBillNo())
                .finalBillDate(finalBill.getFinalBillDate())
                .patientId(patient.getPId())
                .patientName(String.format("%s %s",
                        patient.getPatientPersonalInformation().getFirstName(),
                        patient.getPatientPersonalInformation().getLastName()))
                .gender(patient.getPatientPersonalInformation().getGender())
                .mobileNumber(patient.getPatientContactInformation().getPrimaryPhone())
                .ipdNo(admission.getIpdNo())
                .totalBillAmount(finalBill.getTotalBillAmount())
                .totalSurgeryCharges(finalBill.getTotalSurgeryCharges())
                .totalAdvanceAmount(finalBill.getTotalAdvanceAmount())
                .totalPaidAmount(finalBill.getTotalPaidAmount())
                .totalDiscountAmount(finalBill.getTotalDiscountAmount())
                .totalRefundAmount(finalBill.getTotalRefundAmount())
                .netPayableAmount(finalBill.getNetPayableAmount())
                .balanceAmount(finalBill.getBalanceAmount())
                .hasSettled(finalBill.getHasSettled())
                .build();
    }

    @Override
    public PdfResponseDTO generateFinalBillSummaryPdf(String admissionId, String orgId) {
        logger.info("Generating Final Bill Summary PDF for Admission ID: {}", admissionId);


        entityFetcherUtil.getOrganizationOrThrow(orgId);


        IPDFinalBillSummaryDTO summaryDTO = getFinalBillSummary(admissionId);

        byte[] pdfBytes = finalBillPdfGenerator.generate(summaryDTO);

        return PdfResponseDTO.builder()
                .body(pdfBytes)
                .fileName("Final_Bill_Summary_" + summaryDTO.getFinalBillNo() + ".pdf")
                .build();
    }

    @Override
    public PdfResponseDTO generateFinalBillPdf(String admissionId, String orgId) {
        logger.info("Generating Full Final Bill PDF for Admission ID: {}", admissionId);

        entityFetcherUtil.getOrganizationOrThrow(orgId);

        IPDFinalBillFullResDTO finalBillDTO = getFinalBillByAdmissionId(admissionId);

        byte[] pdfBytes;
        try {
            pdfBytes = IPDFinalBillPdfGenerator.generateFinalBillPdf(finalBillDTO);
        } catch (IOException e) {
            throw new IPDException("Error while generating Final Bill PDF",HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return PdfResponseDTO.builder()
                .body(pdfBytes)
                .fileName("Final_Bill_" + finalBillDTO.getFinalBillNo() + ".pdf")
                .build();
    }


    // -------------------------------
    // Helper Methods
    // -------------------------------
    private BigDecimal nullSafe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private BigDecimal calculateRefundBalance(IPDFinancialSummary summary) {
        BigDecimal refunded = nullSafe(summary.getTotalRefundedAmount());
        BigDecimal pending = nullSafe(summary.getPendingAmount());
        return refunded.subtract(pending).max(BigDecimal.ZERO);
    }

    private String generateFinalBillNo(String orgId) {
        long count = finalBillRepository.count();
        return String.format("FB-%s-%s-%04d", orgId, LocalDate.now().getYear(), count + 1);
    }
}