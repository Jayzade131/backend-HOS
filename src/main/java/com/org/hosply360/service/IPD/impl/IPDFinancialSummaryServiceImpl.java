package com.org.hosply360.service.IPD.impl;

import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.IPD.IPDAdmission;
import com.org.hosply360.dao.IPD.IPDBilling;
import com.org.hosply360.dao.IPD.IPDFinancialSummary;
import com.org.hosply360.dao.IPD.IPDSurgeryBilling;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dto.IPDDTO.IPDFinalBillReqDTO;
import com.org.hosply360.dto.IPDDTO.IPDFinancialSummaryDTO;
import com.org.hosply360.dto.IPDDTO.IPDFinancialSummaryReqDTO;
import com.org.hosply360.dto.IPDDTO.IPDReceiptReqDTO;
import com.org.hosply360.exception.IPDException;
import com.org.hosply360.repository.IPD.IPDBillingRepository;
import com.org.hosply360.repository.IPD.IPDFinancialSummaryRepository;
import com.org.hosply360.repository.IPD.IPDSurgeryBillingRepository;
import com.org.hosply360.service.IPD.IPDFinancialSummaryService;
import com.org.hosply360.util.Others.EntityFetcherUtil;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import com.org.hosply360.util.validator.ValidatorHelper;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class IPDFinancialSummaryServiceImpl implements IPDFinancialSummaryService {
    private static final Logger logger = LoggerFactory.getLogger(IPDFinancialSummaryServiceImpl.class);
    private final IPDFinancialSummaryRepository financialSummaryRepository;
    private final EntityFetcherUtil entityFetcherUtil;
    private final IPDReceiptServiceImpl ipdReceiptService;
    private final IPDBillingRepository ipdBillingRepository;
    private final MongoTemplate mongoTemplate;
    private final IPDSurgeryBillingRepository ipdSurgeryBillingRepository;

    @Override
    public String createFinancialSummary(IPDFinancialSummaryReqDTO reqDTO) {
        ValidatorHelper.validateObject(reqDTO);

        Optional<IPDFinancialSummary> existingSummary = financialSummaryRepository.findByIpIndAdmissionId(reqDTO.getIpdAdmission());
        if (existingSummary.isPresent()) {
            updateFinancialSummary(reqDTO);
            return existingSummary.get().getId();
        }

        IPDAdmission admission = entityFetcherUtil.getIPDAdmissionOrThrow(reqDTO.getIpdAdmission());
        Organization organization = entityFetcherUtil.getOrganizationOrThrow(reqDTO.getOrganizationId());

        List<IPDBilling> billingList = ipdBillingRepository.findByAdmissionIdAndCanceledStatus(admission.getId(), false);
        List<IPDSurgeryBilling> surgeryBillingList = ipdSurgeryBillingRepository.findByAdmissionIdAndCanceledStatus(admission.getId(), false);

        BigDecimal totalBilledAmount = billingList.stream()
                .filter(bill -> bill.getTotalAmount() != null)
                .map(IPDBilling::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalNetBillAmount = billingList.stream()
                .filter(bill -> bill.getNetAmount() != null)
                .map(IPDBilling::getNetAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalBillPaidAmount = billingList.stream()
                .filter(bill -> bill.getPaidAmount() != null)
                .map(IPDBilling::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        BigDecimal totalSurgeryAmount = surgeryBillingList.stream()
                .filter(bill -> bill.getTotalSurgeryAmount() != null)
                .map(IPDSurgeryBilling::getTotalSurgeryAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        BigDecimal totalSurchargePaidAmount = surgeryBillingList.stream()
                .filter(bill -> bill.getPaidAmount() != null)
                .map(IPDSurgeryBilling::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAmount = totalBilledAmount.add(totalSurgeryAmount);
        BigDecimal totalPaidAmount = totalBillPaidAmount.add(totalSurchargePaidAmount);
        BigDecimal totalNetAmount = totalNetBillAmount.add(totalSurgeryAmount);
        BigDecimal totalDiscountAmount = totalAmount.subtract(totalNetAmount);
        BigDecimal pendingAmount = totalNetAmount.subtract(totalPaidAmount);

        IPDFinancialSummary financialSummary = IPDFinancialSummary.builder()
                .organizationId(organization.getId())
                .ipdAdmission(admission)
                .lastUpdated(LocalDateTime.now())
                .totalDepositAmount(reqDTO.getTotalDepositAmount())
                .totalBillPaidAmount(totalBillPaidAmount)
                .totalBilledAmount(totalBilledAmount)
                .totalNetBillAmount(totalNetBillAmount)
                .totalSurgeryAmount(totalSurgeryAmount)
                .totalSurgeryPaidAmount(totalSurchargePaidAmount)
                .totalAmount(totalAmount)
                .totalPaidAmount(totalPaidAmount)
                .totalNetAmount(totalNetAmount)
                .totalDiscountAmount(totalDiscountAmount)
                .totalRefundedAmount(reqDTO.getTotalRefundedAmount())
                .pendingAmount(pendingAmount)
                .remarks(reqDTO.getRemarks())
                .build();

        IPDFinancialSummary savedSummary = financialSummaryRepository.save(financialSummary);
        String id = savedSummary.getId();
        if (reqDTO.getTotalDepositAmount() != null && reqDTO.getTotalDepositAmount().compareTo(BigDecimal.ZERO) > 0) {
            IPDReceiptReqDTO receiptReqDTO = IPDReceiptReqDTO.builder()
                    .admissionId(reqDTO.getIpdAdmission())
                    .organizationId(reqDTO.getOrganizationId())
                    .totalRecieveAmount(reqDTO.getTotalDepositAmount())
                    .paymentMode(reqDTO.getPaymentMode())
                    .receiptType(reqDTO.getReceiptType())
                    .remarks(reqDTO.getRemarks())
                    .chequeNumber(reqDTO.getChequeNumber())
                    .chequeDateTime(LocalDateTime.now())
                    .bankName(reqDTO.getBankName())
                    .branchName(reqDTO.getBranchName())
                    .accountHolderName(reqDTO.getAccountHolderName())
                    .ifscCode(reqDTO.getIfscCode())
                    .build();
            id = ipdReceiptService.createReceipt(receiptReqDTO);
        }

        return id;
    }


    @Override
    public void updateFinancialSummary(IPDFinancialSummaryReqDTO reqDTO) {
        ValidatorHelper.validateObject(reqDTO);
        IPDAdmission admission = entityFetcherUtil.getIPDAdmissionOrThrow(reqDTO.getIpdAdmission());
        Organization organization = entityFetcherUtil.getOrganizationOrThrow(reqDTO.getOrganizationId());
        IPDFinancialSummary financialSummary = financialSummaryRepository
                .findByIpIndAdmissionId(admission.getId())
                .orElseThrow(() -> new IPDException(ErrorConstant.FINANCIAL_SUMMARY_NOT_FOUND, HttpStatus.NOT_FOUND));
        List<IPDBilling> billingList = ipdBillingRepository.findByAdmissionIdAndCanceledStatus(admission.getId(), false);
        List<IPDSurgeryBilling> surgeryBillingList = ipdSurgeryBillingRepository.findByAdmissionIdAndCanceledStatus(admission.getId(), false);
        BigDecimal totalBilledAmount = billingList.stream()
                .filter(bill -> bill.getTotalAmount() != null)
                .map(IPDBilling::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalNetBillAmount = billingList.stream()
                .filter(bill -> bill.getNetAmount() != null)
                .map(IPDBilling::getNetAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalBillPaidAmount = billingList.stream()
                .filter(bill -> bill.getPaidAmount() != null)
                .map(IPDBilling::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        BigDecimal totalSurgeryAmount = surgeryBillingList.stream()
                .filter(bill -> bill.getTotalSurgeryAmount() != null)
                .map(IPDSurgeryBilling::getTotalSurgeryAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        BigDecimal totalSurchargePaidAmount = surgeryBillingList.stream()
                .filter(bill -> bill.getPaidAmount() != null)
                .map(IPDSurgeryBilling::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAmount = totalBilledAmount.add(totalSurgeryAmount);
        BigDecimal totalPaidAmount = totalBillPaidAmount.add(totalSurchargePaidAmount);
        BigDecimal totalNetAmount = totalNetBillAmount.add(totalSurgeryAmount);
        BigDecimal totalDiscountAmount = totalAmount.subtract(totalNetAmount);
        BigDecimal pendingAmount = totalNetAmount.subtract(totalPaidAmount);
        BigDecimal updatedDeposit = financialSummary.getTotalDepositAmount()
                .add(reqDTO.getTotalDepositAmount() != null ? reqDTO.getTotalDepositAmount() : BigDecimal.ZERO);
        financialSummary.setLastUpdated(LocalDateTime.now());

        financialSummary.setTotalDepositAmount(updatedDeposit);
        financialSummary.setTotalBillPaidAmount(totalBillPaidAmount);
        financialSummary.setTotalBilledAmount(totalBilledAmount);
        financialSummary.setTotalNetBillAmount(totalNetBillAmount);

        financialSummary.setTotalSurgeryAmount(totalSurgeryAmount);
        financialSummary.setTotalSurgeryPaidAmount(totalSurchargePaidAmount);

        financialSummary.setTotalAmount(totalAmount);
        financialSummary.setTotalPaidAmount(totalPaidAmount);
        financialSummary.setTotalNetAmount(totalNetAmount);

        financialSummary.setTotalDiscountAmount(totalDiscountAmount);
        financialSummary.setTotalRefundedAmount(reqDTO.getTotalRefundedAmount());
        financialSummary.setPendingAmount(totalNetBillAmount.subtract(pendingAmount));

        financialSummary.setRemarks(reqDTO.getRemarks());

        financialSummaryRepository.save(financialSummary);
        if (reqDTO.getTotalDepositAmount() != null && reqDTO.getTotalDepositAmount().compareTo(BigDecimal.ZERO) > 0) {
            IPDReceiptReqDTO receiptReqDTO = IPDReceiptReqDTO.builder()
                    .admissionId(admission.getId())
                    .organizationId(organization.getId())
                    .totalRecieveAmount(reqDTO.getTotalDepositAmount())
                    .paymentMode(reqDTO.getPaymentMode())
                    .receiptType(reqDTO.getReceiptType())
                    .remarks(reqDTO.getRemarks())
                    .chequeNumber(reqDTO.getChequeNumber())
                    .chequeDateTime(LocalDateTime.now())
                    .bankName(reqDTO.getBankName())
                    .branchName(reqDTO.getBranchName())
                    .accountHolderName(reqDTO.getAccountHolderName())
                    .ifscCode(reqDTO.getIfscCode())
                    .build();
            ipdReceiptService.createReceipt(receiptReqDTO);
        }
    }

    @Override
    public List<IPDFinancialSummaryDTO> getFinancialSummary(String organizationId, String ipdAdmissionId, String id) {
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();
        if (organizationId != null && !organizationId.isBlank()) {
            criteriaList.add(Criteria.where("organizationId").is(organizationId));
        }
        if (ipdAdmissionId != null && !ipdAdmissionId.isBlank()) {
            criteriaList.add(Criteria.where("ipdAdmission.$id").is(new ObjectId(ipdAdmissionId)));
        }
        if (id != null && !id.isBlank()) {
            criteriaList.add(Criteria.where("_id").is(new ObjectId(id)));
        }
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }
        List<IPDFinancialSummary> summaries = mongoTemplate.find(query, IPDFinancialSummary.class);
        return summaries.stream().map(summary -> {
            IPDFinancialSummaryDTO dto = ObjectMapperUtil.copyObject(summary, IPDFinancialSummaryDTO.class);
            dto.setIpdAdmission(summary.getIpdAdmission() != null ? summary.getIpdAdmission().getId() : null);
            return dto;
        }).toList();
    }


    @Override
    public String processRefund(IPDFinancialSummaryReqDTO reqDTO) {
        IPDFinancialSummary financialSummary = entityFetcherUtil.getFinancialSummaryOrThrow(reqDTO.getId());
        if (financialSummary.getTotalRefundedAmount().compareTo(financialSummary.getTotalDepositAmount()) >= 0)
            throw new IPDException(ErrorConstant.IPD_ADVANCE_ALREADY_REFUNDED, HttpStatus.BAD_REQUEST);
        financialSummary.setTotalRefundedAmount(financialSummary.getTotalRefundedAmount().add(reqDTO.getTotalRefundedAmount()));
        financialSummary.setLastUpdated(LocalDateTime.now());
        IPDFinancialSummary savedSummary = financialSummaryRepository.save(financialSummary);
        return savedSummary.getId();
    }


    @Override
    public void updateSummaryAfterBillingChange(IPDBilling billing) {
        try {
            if (billing == null || billing.getAdmissionId() == null) {
                logger.warn("Skipping FinancialSummary update — billing or admission is null");
                return;
            }

            String admissionId = billing.getAdmissionId().getId();
            logger.info("Updating Financial Summary for Admission ID: {}", admissionId);

            // Fetch or create summary
            IPDFinancialSummary summary = financialSummaryRepository.findByIpdAdmissionId(admissionId)
                    .stream()
                    .findFirst()
                    .orElseGet(() -> createEmptySummary(billing));

            // ---- (1) Billing Aggregation ----
            List<IPDBilling> activeBills = ipdBillingRepository.findByAdmissionIdAndCanceledStatus(admissionId, false);

            BigDecimal totalBilledAmount = sum(activeBills, IPDBilling::getTotalAmount);
            BigDecimal totalNetAmount = sum(activeBills, IPDBilling::getNetAmount);
            BigDecimal totalDiscountAmount = sum(activeBills, IPDBilling::getDiscountAmount);
            BigDecimal totalPaidAmount = sum(activeBills, IPDBilling::getPaidAmount);
            BigDecimal totalRefundedAmount = sum(activeBills, IPDBilling::getRefundAmount);

            // ---- (2) Surgery Aggregation ----
            List<IPDSurgeryBilling> surgeryBills = ipdSurgeryBillingRepository.findByAdmissionIdAndCanceledStatus(admissionId, false);

            BigDecimal totalSurgeryAmount = sum(surgeryBills, IPDSurgeryBilling::getTotalSurgeryAmount);
            BigDecimal totalSurgeryPaidAmount = sum(surgeryBills, IPDSurgeryBilling::getPaidAmount);

            // ---- (3) Compute Combined Totals ----
            BigDecimal totalDeposit = nvl(summary.getTotalDepositAmount());
            BigDecimal combinedTotalAmount = totalBilledAmount.add(totalSurgeryAmount);
            BigDecimal combinedNetAmount = totalNetAmount.add(totalSurgeryAmount);
            BigDecimal combinedPaidAmount = totalPaidAmount.add(totalSurgeryPaidAmount);

            // pending = net - (paid + deposit - refund)
            BigDecimal pending = combinedNetAmount.subtract(combinedPaidAmount.subtract(totalRefundedAmount));


            // ---- (4) Update Summary Entity ----
            summary.setTotalBilledAmount(totalBilledAmount);
            summary.setTotalNetBillAmount(totalNetAmount);
            summary.setTotalBillPaidAmount(totalPaidAmount);
            summary.setTotalDiscountAmount(totalDiscountAmount);
            summary.setTotalRefundedAmount(totalRefundedAmount);
            summary.setTotalDepositAmount(totalDeposit);

            summary.setTotalSurgeryAmount(totalSurgeryAmount);
            summary.setTotalSurgeryPaidAmount(totalSurgeryPaidAmount);

            summary.setTotalAmount(combinedTotalAmount);
            summary.setTotalNetAmount(combinedNetAmount);
            summary.setTotalPaidAmount(combinedPaidAmount);
            summary.setPendingAmount(pending.max(BigDecimal.ZERO));
            summary.setLastUpdated(LocalDateTime.now());

            financialSummaryRepository.save(summary);
            logger.info("Financial Summary updated successfully for Admission ID: {}", admissionId);

        } catch (Exception e) {
            logger.error("Error while updating Financial Summary for Billing ID: {}", billing != null ? billing.getId() : "null", e);
        }
    }

    private <T> BigDecimal sum(List<T> list, Function<T, BigDecimal> getter) {
        return list.stream()
                .map(item -> {
                    BigDecimal value = getter.apply(item);
                    return value != null ? value : BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal nvl(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }


    private IPDFinancialSummary createEmptySummary(IPDBilling billing) {
        return IPDFinancialSummary.builder()
                .ipdAdmission(billing.getAdmissionId())
                .organizationId(billing.getOrganizationId())
                .totalDepositAmount(BigDecimal.ZERO)
                .totalBillPaidAmount(BigDecimal.ZERO)
                .totalBilledAmount(BigDecimal.ZERO)
                .totalNetBillAmount(BigDecimal.ZERO)
                .totalRefundedAmount(BigDecimal.ZERO)
                .pendingAmount(BigDecimal.ZERO)
                .lastUpdated(LocalDateTime.now())
                .build();
    }

    @Override
    public void updateSummaryAfterSurgeryBillingChange(IPDSurgeryBilling surgeryBilling) {

        if (surgeryBilling == null || surgeryBilling.getIpdAdmissionId() == null) {
            logger.warn("Skipping FinancialSummary update — surgery billing or admission ID is null");
            return;
        }

        String admissionId = surgeryBilling.getIpdAdmissionId();
        logger.info("Updating Financial Summary after Surgery Billing — Admission ID: {}", admissionId);

        // Fetch summary or create new
        IPDFinancialSummary summary = financialSummaryRepository
                .findByIpdAdmissionId(admissionId)
                .stream()
                .findFirst()
                .orElseGet(() -> createEmptySummaryForSurgery(surgeryBilling));

        // ---- Fetch all billing & surgery data ----
        List<IPDBilling> billingList =
                ipdBillingRepository.findByAdmissionIdAndCanceledStatus(admissionId, false);

        List<IPDSurgeryBilling> surgeryBills =
                ipdSurgeryBillingRepository.findByAdmissionIdAndCanceledStatus(admissionId, false);


        // ---- BILLING TOTALS ----
        BigDecimal totalBilledAmount = sum(billingList, IPDBilling::getTotalAmount);
        BigDecimal totalNetBillAmount = sum(billingList, IPDBilling::getNetAmount);
        BigDecimal totalBillPaidAmount = sum(billingList, IPDBilling::getPaidAmount);
        BigDecimal totalBillDiscount = sum(billingList, IPDBilling::getDiscountAmount);
        BigDecimal totalBillRefund = sum(billingList, IPDBilling::getRefundAmount);


        // ---- SURGERY TOTALS ----
        BigDecimal totalSurgeryAmount =
                sum(surgeryBills, IPDSurgeryBilling::getTotalSurgeryAmount); // net = totalSurgeryAmount

        BigDecimal totalSurgeryPaidAmount =
                sum(surgeryBills, IPDSurgeryBilling::getPaidAmount);

        BigDecimal totalSurgeryRefund =
                sum(surgeryBills, IPDSurgeryBilling::getRefundAmount);


        // ---- MERGED TOTALS (billing + surgery) ----
        BigDecimal combinedTotalAmount =
                totalBilledAmount.add(totalSurgeryAmount);

        BigDecimal combinedNetAmount =
                totalNetBillAmount.add(totalSurgeryAmount);

        BigDecimal combinedPaidAmount =
                totalBillPaidAmount.add(totalSurgeryPaidAmount);

        BigDecimal combinedRefund =
                totalBillRefund.add(totalSurgeryRefund);

        BigDecimal deposit = nvl(summary.getTotalDepositAmount());
        BigDecimal pending =
                combinedNetAmount
                        .subtract(combinedPaidAmount.add(deposit).subtract(combinedRefund))
                        .max(BigDecimal.ZERO);
        // ---- UPDATE SUMMARY ----
        summary.setTotalBilledAmount(totalBilledAmount);
        summary.setTotalNetBillAmount(totalNetBillAmount);
        summary.setTotalBillPaidAmount(totalBillPaidAmount);
        summary.setTotalDiscountAmount(totalBillDiscount);
        summary.setTotalRefundedAmount(combinedRefund);

        summary.setTotalSurgeryAmount(totalSurgeryAmount);
        summary.setTotalSurgeryPaidAmount(totalSurgeryPaidAmount);

        summary.setTotalAmount(combinedTotalAmount);
        summary.setTotalNetAmount(combinedNetAmount);
        summary.setTotalPaidAmount(combinedPaidAmount);
        summary.setPendingAmount(pending);
        summary.setLastUpdated(LocalDateTime.now());

        financialSummaryRepository.save(summary);

        logger.info("Financial Summary updated successfully for Admission ID: {}", admissionId);
    }


    private IPDFinancialSummary createEmptySummaryForSurgery(IPDSurgeryBilling surgeryBilling) {
        return IPDFinancialSummary.builder()
                .ipdAdmission(entityFetcherUtil.getIPDAdmissionOrThrow(surgeryBilling.getIpdAdmissionId()))
                .organizationId(surgeryBilling.getOrganizationId())
                .totalDepositAmount(BigDecimal.ZERO)
                .totalSurgeryPaidAmount(BigDecimal.ZERO)
                .totalSurgeryAmount(BigDecimal.ZERO)
                .totalRefundedAmount(BigDecimal.ZERO)
                .pendingAmount(BigDecimal.ZERO)
                .lastUpdated(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public void updateSummaryAfterFinalBill(IPDFinalBillReqDTO requestDto) {

        if (requestDto == null || requestDto.getAdmissionId() == null || requestDto.getAdmissionId().isBlank()) {
            throw new IPDException("Admission ID is required for Financial Summary update", HttpStatus.BAD_REQUEST);
        }

        String admissionId = requestDto.getAdmissionId();
        logger.info("Updating Financial Summary after Final Bill operation for Admission ID: {}", admissionId);

        // Fetch Financial Summary
        IPDFinancialSummary summary = financialSummaryRepository.findByIpdAdmissionId(admissionId)
                .orElseThrow(() -> new IPDException(ErrorConstant.ITEM_NOT_FOUND, HttpStatus.NOT_FOUND));

        // Prepare discount values
        BigDecimal existingDiscount = nvl(summary.getTotalDiscountAmount());
        BigDecimal additionalDiscount = nvl(requestDto.getAdditionalDiscountAmount());

        // Skip recalculation if discount not provided
        if (additionalDiscount.compareTo(BigDecimal.ZERO) == 0) {
            logger.info("No additional discount provided — skipping discount recalculation for Admission ID: {}", admissionId);
            return;
        }

        //  Compute new discount & net amounts
        BigDecimal totalDiscount = existingDiscount.add(additionalDiscount);
        BigDecimal totalBilledAmount = nvl(summary.getTotalBilledAmount());
        BigDecimal totalSurgeryAmount = nvl(summary.getTotalSurgeryAmount());
        BigDecimal totalAmount = totalBilledAmount.add(totalSurgeryAmount);

        // totalNetAmount = totalAmount - totalDiscount
        BigDecimal totalNetAmount = totalAmount.subtract(totalDiscount).max(BigDecimal.ZERO);

        BigDecimal totalPaid = nvl(summary.getTotalPaidAmount());
        BigDecimal totalRefund = nvl(summary.getTotalRefundedAmount());

        // pending = totalNetAmount - (totalPaid - totalRefund)
        BigDecimal pending = totalNetAmount.subtract(totalPaid.subtract(totalRefund)).max(BigDecimal.ZERO);

        // Update entity
        summary.setTotalDiscountAmount(totalDiscount);
        summary.setTotalNetAmount(totalNetAmount);
        summary.setPendingAmount(pending);
        summary.setLastUpdated(LocalDateTime.now());

        financialSummaryRepository.save(summary);
        logger.info("Financial Summary updated successfully after Final Bill discount adjustment | Admission ID: {}", admissionId);

    }


}
