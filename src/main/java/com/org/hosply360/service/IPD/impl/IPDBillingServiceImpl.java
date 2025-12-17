package com.org.hosply360.service.IPD.impl;

import com.org.hosply360.constant.Enums.PaymentMode;
import com.org.hosply360.constant.Enums.ReceiptType;
import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.IPD.IPDAdmission;
import com.org.hosply360.dao.IPD.IPDBilling;
import com.org.hosply360.dao.IPD.IPDBillingItem;
import com.org.hosply360.dao.globalMaster.BillingItem;
import com.org.hosply360.dao.globalMaster.BillingItemGroup;
import com.org.hosply360.dto.IPDDTO.CancelBillingItemsRequestDTO;
import com.org.hosply360.dto.IPDDTO.IPDBillingDTO;
import com.org.hosply360.dto.IPDDTO.IPDBillingItemDTO;
import com.org.hosply360.dto.IPDDTO.IPDBillingPaymentDTO;
import com.org.hosply360.dto.IPDDTO.IPDBillingReqDTO;
import com.org.hosply360.dto.IPDDTO.IPDReceiptReqDTO;
import com.org.hosply360.dto.OPDDTO.PdfResponseDTO;
import com.org.hosply360.exception.IPDException;
import com.org.hosply360.repository.IPD.IPDBillingRepository;
import com.org.hosply360.service.IPD.IPDBillingService;
import com.org.hosply360.service.IPD.IPDFinancialSummaryService;
import com.org.hosply360.service.IPD.IPDReceiptService;
import com.org.hosply360.util.Others.EntityFetcherUtil;
import com.org.hosply360.util.Others.ScaleUtil;
import com.org.hosply360.util.Others.SequenceGeneratorService;
import com.org.hosply360.util.PDFGenUtil.IPD.IPDBillingPdfGenerator;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import com.org.hosply360.util.validator.ValidatorHelper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class IPDBillingServiceImpl implements IPDBillingService {

    private final IPDBillingRepository ipdBillingRepository;
    private final EntityFetcherUtil entityFetcherUtil;
    private final IPDReceiptService ipdReceiptService;
    private final IPDFinancialSummaryService ipdFinancialSummaryService;
    private final MongoTemplate mongoTemplate;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final IPDBillingPdfGenerator ipdBillingPdfGenerator;


    private static final Logger logger = LoggerFactory.getLogger(IPDBillingServiceImpl.class);

    // -------------------------------------------------------
    //  Create Billing
    // have to remove the async methods
    // -------------------------------------------------------
    @Override
    public String createIPDBilling(IPDBillingReqDTO billingDTO) {
        logger.info("Creating new IPD Billing for Admission ID: {}", billingDTO.getAdmissionId());

        validateCreateRequest(billingDTO);

        IPDAdmission admission = entityFetcherUtil.getIPDAdmissionOrThrow(billingDTO.getAdmissionId());
        IPDBilling billing = mapToEntity(billingDTO, admission);
        billing.getBillingItems().forEach(item -> item.setItemCanceled(false));
        billing.setCanceled(false);
        billing.setHasSettled(false);

        recalculateBillingTotals(billing);
        IPDBilling saved = ipdBillingRepository.save(billing);
        ipdFinancialSummaryService.updateSummaryAfterBillingChange(saved);

        logger.info("IPD Billing created successfully with ID: {}", saved.getId());
        return saved.getId();
    }

    // -------------------------------------------------------
    // Update Billing (Partial Update Supported)
    // -------------------------------------------------------
    @Override
    public String updateIPDBilling(String id, IPDBillingDTO billingDTO) {
        logger.info("Updating IPD Billing with ID: {}", id);

        // Validate request data early
        if (billingDTO == null) {
            throw new IPDException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST);
        }

        IPDBilling existing = ipdBillingRepository.findById(id)
                .orElseThrow(() -> new IPDException(ErrorConstant.ITEM_NOT_FOUND, HttpStatus.NOT_FOUND));

        // Validate consistency
        if (!Objects.equals(existing.getAdmissionId().getId(), billingDTO.getAdmissionId())) {
            throw new IPDException(ErrorConstant.ADMISSION_ID_MISMATCH_DURING_UPDATE, HttpStatus.BAD_REQUEST);
        }

        if (!Objects.equals(existing.getOrganizationId(), billingDTO.getOrganizationId())) {
            throw new IPDException(ErrorConstant.ORGANIZATION_ID_MISMATCH_DURING_UPDATE, HttpStatus.BAD_REQUEST);
        }

        // Update only non-null fields from DTO
        Optional.ofNullable(billingDTO.getRemarks()).ifPresent(existing::setRemarks);
        Optional.ofNullable(billingDTO.getDiscountAmount()).ifPresent(existing::setDiscountAmount);
        Optional.ofNullable(billingDTO.getRefundAmount()).ifPresent(existing::setRefundAmount);
        Optional.ofNullable(billingDTO.getIsSettled()).ifPresent(existing::setHasSettled);
        Optional.ofNullable(billingDTO.getIsAdvanceAdjusted()).ifPresent(existing::setHasAdvanceAdjusted);

        // Always update timestamp
        existing.setBillingDateTime(LocalDateTime.now());

        // Update billing items if provided
        if (billingDTO.getBillingItems() != null && !billingDTO.getBillingItems().isEmpty()) {
            existing.setBillingItems(mapBillingItems(billingDTO.getBillingItems()));
        }

        // Recalculate totals and persist
        recalculateBillingTotals(existing);
        ipdBillingRepository.save(existing);

        // Update financial summary
        ipdFinancialSummaryService.updateSummaryAfterBillingChange(existing);

        logger.info("Successfully updated IPD Billing with ID: {}", id);
        return id;
    }


    // -------------------------------------------------------
    // Cancel Entire Billing
    // -------------------------------------------------------
    @Override
    public String cancelIPDBilling(String billingId, String reason) {
        logger.info("Canceling entire billing ID: {} with reason: {}", billingId, reason);

        var billing = ipdBillingRepository.findById(billingId).orElseThrow(() -> new IPDException(ErrorConstant.ITEM_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (Boolean.TRUE.equals(billing.getCanceled())) {
            throw new IPDException(ErrorConstant.BILLING_ALREADY_CANCELLED, HttpStatus.BAD_REQUEST);
        }

        billing.setCanceled(true);
        billing.setCancelReason(reason);
        billing.setCancelDateTime(LocalDateTime.now());

        if (billing.getBillingItems() != null) {
            billing.getBillingItems().forEach(item -> {
                item.setItemCanceled(true);
                item.setCancelReason(reason);
            });
        }

        recalculateBillingTotals(billing);
        ipdBillingRepository.save(billing);
        ipdFinancialSummaryService.updateSummaryAfterBillingChange(billing);
        //ipdFinancialSummaryAsyncService.updateSummaryAfterBillingChangeAsync(billing);

        logger.info("Billing ID: {} canceled successfully", billingId);
        return billingId;
    }

    // -------------------------------------------------------
    // Bill payment
    // -------------------------------------------------------

    @Override
    public String billPayment(String billingId, IPDBillingPaymentDTO paymentDTO) {
        logger.info("Processing bill payment for Billing ID: {}", billingId);

        // 1. Fetch billing
        IPDBilling billing = ipdBillingRepository.findById(billingId)
                .orElseThrow(() -> new IPDException(ErrorConstant.ITEM_NOT_FOUND, HttpStatus.NOT_FOUND));

        // 2. Validate billing state
        if (Boolean.TRUE.equals(billing.getCanceled())) {
            throw new IPDException(ErrorConstant.CANNOT_MAKE_PAYMENT_FOR_A_CANCELED_BILL, HttpStatus.BAD_REQUEST);
        }

        if (billing.getHasSettled() != null && billing.getHasSettled()) {
            throw new IPDException(ErrorConstant.BILL_ALREADY_SETTLED, HttpStatus.BAD_REQUEST);
        }

        if (paymentDTO.getAmountPaid() == null || paymentDTO.getAmountPaid().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IPDException(ErrorConstant.INVALID_PAYMENT_AMOUNT, HttpStatus.BAD_REQUEST);
        }

        // 3. Calculate updated payment and balance
        BigDecimal currentPaid = billing.getPaidAmount() != null ? billing.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal newPaidAmount = currentPaid.add(paymentDTO.getAmountPaid());
        BigDecimal netAmount = billing.getNetAmount() != null ? billing.getNetAmount() : BigDecimal.ZERO;

        BigDecimal newBalance = netAmount.subtract(newPaidAmount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IPDException(ErrorConstant.PAYMENT_EXCEEDS_REMAINING_BALANCE, HttpStatus.BAD_REQUEST);
        }

        billing.setPaidAmount(newPaidAmount);
        billing.setBalanceAmount(newBalance);
        billing.setBillingDateTime(LocalDateTime.now());
        if (newBalance.compareTo(BigDecimal.ZERO) == 0) {
            billing.setHasSettled(true);
        }

        // 4. Save updated billing
        ipdBillingRepository.save(billing);
        ipdFinancialSummaryService.updateSummaryAfterBillingChange(billing);
        // ipdFinancialSummaryAsyncService.updateSummaryAfterBillingChangeAsync(billing);

        // 5. Build receipt request
        IPDReceiptReqDTO receiptReq = new IPDReceiptReqDTO();
        receiptReq.setOrganizationId(paymentDTO.getOrganizationId());
        receiptReq.setAdmissionId(paymentDTO.getAdmissionId());
        receiptReq.setTotalRecieveAmount(paymentDTO.getAmountPaid());
        receiptReq.setPaymentMode(paymentDTO.getPaymentMode());
        receiptReq.setRemarks(paymentDTO.getRemarks());
        receiptReq.setReceiptType(ReceiptType.GENERAL);
        receiptReq.setBillingId(billingId);

        // If payment mode is cheque, add cheque details
        if (paymentDTO.getPaymentMode() == PaymentMode.CHEQUE) {
            receiptReq.setChequeNumber(paymentDTO.getChequeNumber());
            receiptReq.setChequeDateTime(paymentDTO.getChequeDateTime());
            receiptReq.setBankName(paymentDTO.getBankName());
            receiptReq.setBranchName(paymentDTO.getBranchName());
            receiptReq.setAccountHolderName(paymentDTO.getAccountHolderName());
            receiptReq.setIfscCode(paymentDTO.getIfscCode());
        }

        // 6. Create receipt via existing receipt service
        String receiptId = ipdReceiptService.createReceipt(receiptReq);

        logger.info("Bill payment successful for Billing ID: {} ", billingId);
        return receiptId;
    }

    // -------------------------------------------------------
    //  Cancel Multiple Billing Items
    // -------------------------------------------------------
    @Override
    public String cancelMultipleIPDBillingItems(String billingId, CancelBillingItemsRequestDTO request) {
        logger.info("Canceling billing items {} in billing ID: {}", request.getBillingItemIds(), billingId);

        var billing = ipdBillingRepository.findById(billingId).orElseThrow(() -> new IPDException(ErrorConstant.ITEM_NOT_FOUND, HttpStatus.NOT_FOUND));

        var billingItems = billing.getBillingItems();
        if (billingItems == null || billingItems.isEmpty()) {
            throw new IPDException(ErrorConstant.BILLING_ITEM_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        var itemIdsToCancel = request.getBillingItemIds();
        if (itemIdsToCancel == null || itemIdsToCancel.isEmpty()) {
            throw new IPDException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST);
        }

        int canceledCount = 0;

        for (var item : billingItems) {
            if (item.getBillingItem() != null && itemIdsToCancel.contains(item.getBillingItem().getId())) {
                if (Boolean.TRUE.equals(item.getItemCanceled())) {
                    logger.warn("Billing item {} is already canceled. Skipping.", item.getBillingItem().getId());
                    continue;
                }

                item.setItemCanceled(true);
                item.setCancelReason(request.getReason());
                canceledCount++;
            }
        }

        if (canceledCount == 0) {
            throw new IPDException(ErrorConstant.BILLING_ITEM_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        recalculateBillingTotals(billing);
        ipdBillingRepository.save(billing);
        ipdFinancialSummaryService.updateSummaryAfterBillingChange(billing);

        //ipdFinancialSummaryAsyncService.updateSummaryAfterBillingChangeAsync(billing);

        logger.info("{} billing items canceled successfully in billing ID: {}", canceledCount, billingId);
        return billingId;
    }

    // -------------------------------------------------------
    // Fetch Methods
    // -------------------------------------------------------

    @Override
    public List<IPDBillingDTO> getAllIPDBillings(String organizationId, String admissionId, String id) {
        ValidatorHelper.validateObject(organizationId);
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("organizationId").is(organizationId));
        if (admissionId != null && !admissionId.isBlank()) {
            criteriaList.add(Criteria.where("admission_id.$id").is(new ObjectId(admissionId)));
        }
        if (id != null && !id.isBlank()) {
            criteriaList.add(Criteria.where("_id").is(new ObjectId(id)));
        }
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }
        query.with(Sort.by(Sort.Direction.DESC, "billing_date_time"));
        System.out.println("Executing Billing Query: " + query);

        List<IPDBilling> billings = mongoTemplate.find(query, IPDBilling.class);
        return billings.stream().map(billing -> {
            IPDBillingDTO dto = ObjectMapperUtil.copyObject(billing, IPDBillingDTO.class);
            dto.setAdmissionId(billing.getAdmissionId() != null ? billing.getAdmissionId().getId() : null);
            dto.setOrganizationId(billing.getOrganizationId());
            dto.setBillingDate(billing.getBillingDateTime().toLocalDate());
            dto.setCancelReason(billing.getCancelReason());
            dto.setCancelDate(billing.getCancelDateTime());
            dto.setIsSettled(billing.getHasSettled());
            dto.setIsAdvanceAdjusted(billing.getHasAdvanceAdjusted());
            dto.setCanceledBy(billing.getCanceledBy());
            dto.setBillingItems(billing.getBillingItems().stream().map(item -> {
                IPDBillingItemDTO itemDTO = ObjectMapperUtil.copyObject(item, IPDBillingItemDTO.class);
                if (item.getBillingItemGroup() != null) {
                    itemDTO.setBillingItemGroupId(item.getBillingItemGroup().getId());
                    itemDTO.setBillingItemGroupName(item.getBillingItemGroup().getItemGroupName());
                }
                if (item.getBillingItem() != null) {
                    itemDTO.setBillingItemId(item.getBillingItem().getId());
                    itemDTO.setBillingItemName(item.getBillingItem().getItemName());
                }
                itemDTO.setCanceled(item.getItemCanceled());
                return itemDTO;
            }).toList());

            return dto;
        }).toList();
    }

    // -------------------------------------------------------
    // Internal Utilities
    // -------------------------------------------------------
    private void validateCreateRequest(IPDBillingReqDTO dto) {
        if (ObjectUtils.isEmpty(dto.getAdmissionId()) || ObjectUtils.isEmpty(dto.getOrganizationId())) {
            throw new IPDException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST);
        }
    }

    private IPDBilling mapToEntity(IPDBillingReqDTO dto, IPDAdmission admission) {
        List<IPDBillingItem> billingItems = mapBillingItems(dto.getBillingItems());

        return IPDBilling.builder()
                .organizationId(dto.getOrganizationId())
                .admissionId(admission)
                .billingNo(sequenceGeneratorService.generateIPDBillingNumber())
                .billingDateTime(LocalDateTime.now())
                .billingItems(billingItems)
                .totalAmount(dto.getTotalAmount())
                .discountAmount(dto.getDiscountAmount())
                .netAmount(dto.getNetAmount())
                .balanceAmount(dto.getBalanceAmount())
                .refundAmount(dto.getRefundAmount())
                .remarks(dto.getRemarks())
                .hasSettled(dto.getIsSettled())
                .hasAdvanceAdjusted(false)
                .build();
    }

    private List<IPDBillingItem> mapBillingItems(List<IPDBillingItemDTO> itemDTOs) {
        if (itemDTOs == null || itemDTOs.isEmpty()) return List.of();

        return itemDTOs.stream().map(dto -> {
            BillingItemGroup group = entityFetcherUtil.getBillingItemGroupOrThrow(dto.getBillingItemGroupId());
            BillingItem item = entityFetcherUtil.getBillingItemOrThrow(dto.getBillingItemId());

            return IPDBillingItem.builder()
                    .billingItemGroup(group)
                    .billingItem(item)
                    .quantity(dto.getQuantity())
                    .rate(dto.getRate())
                    .discountPercent(dto.getDiscountPercent())
                    .discountAmount(dto.getDiscountAmount())
                    .amount(dto.getAmount())
                    .itemCanceled(dto.getCanceled() != null ? dto.getCanceled() : false)
                    .cancelReason(dto.getCancelReason()).build();
        }).toList();
    }


    private void recalculateBillingTotals(IPDBilling billing) {
        if (billing.getBillingItems() == null) return;

        // Calculate total of non-canceled items
        double activeTotal = billing.getBillingItems().stream()
                .filter(item -> !Boolean.TRUE.equals(item.getItemCanceled()))
                .mapToDouble(item -> item.getAmount() != null ? item.getAmount() : 0.0)
                .sum();

        // Scale total to 2 decimals
        BigDecimal totalAmount = ScaleUtil.scaleToTwo(BigDecimal.valueOf(activeTotal));
        billing.setTotalAmount(totalAmount);

        // If no active items left, reset amounts and skip calculation
        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.info("No active items found for billing ID: {} — resetting amounts", billing.getId());
            billing.setDiscountAmount(ScaleUtil.scaleToTwo(BigDecimal.ZERO));
            billing.setNetAmount(ScaleUtil.scaleToTwo(BigDecimal.ZERO));
            billing.setBalanceAmount(ScaleUtil.scaleToTwo(BigDecimal.ZERO));
            return;
        }

        // Apply discount (if any) — keep original logic for persisting discount unchanged except use scaled value for calculation
        BigDecimal discount = billing.getDiscountAmount() != null
                ? ScaleUtil.scaleToTwo(billing.getDiscountAmount())
                : ScaleUtil.scaleToTwo(BigDecimal.ZERO);

        // Ensure discount doesn’t exceed total
        if (discount.compareTo(totalAmount) > 0) {
            discount = totalAmount;
            billing.setDiscountAmount(discount); // same as original behavior (only set when exceeds)
        }

        BigDecimal netAmount = totalAmount.subtract(discount);
        netAmount = ScaleUtil.scaleToTwo(netAmount);
        billing.setNetAmount(netAmount);

        // Paid amount (safe default) — scale for calculation but do not change original persisted paid amount here (mirrors your logic)
        BigDecimal paidAmount = billing.getPaidAmount() != null
                ? ScaleUtil.scaleToTwo(billing.getPaidAmount())
                : ScaleUtil.scaleToTwo(BigDecimal.ZERO);

        // Balance = net - paid
        BigDecimal balanceAmount = netAmount.subtract(paidAmount);
        balanceAmount = balanceAmount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : balanceAmount;
        billing.setBalanceAmount(ScaleUtil.scaleToTwo(balanceAmount));
    }

    @Override
    public PdfResponseDTO generateIPDBillingPdf(String billingId, String orgId) {
        IPDBilling billing = ipdBillingRepository.findById(billingId)
                .orElseThrow(() -> new IPDException(ErrorConstant.ITEM_NOT_FOUND, HttpStatus.NOT_FOUND));

        entityFetcherUtil.getOrganizationOrThrow(orgId);

        byte[] pdfBytes = ipdBillingPdfGenerator.generateIPDBill(billing);

        return PdfResponseDTO.builder()
                .body(pdfBytes)
                .fileName("IPD_Billing_" + billing.getBillingNo() + ".pdf")
                .build();
    }


}
