package com.org.hosply360.service.IPD.impl;

import com.org.hosply360.constant.Enums.PaymentMode;
import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.IPD.IPDAdmission;
import com.org.hosply360.dao.IPD.IPDSurgeryBilling;
import com.org.hosply360.dao.IPD.ParticipantCharge;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dto.IPDDTO.IPDBillingPaymentDTO;
import com.org.hosply360.dto.IPDDTO.IPDReceiptReqDTO;
import com.org.hosply360.dto.IPDDTO.IPDSurgeryBillCancelDTO;
import com.org.hosply360.dto.IPDDTO.IPDSurgeryBillingDTO;
import com.org.hosply360.dto.IPDDTO.IPDSurgeryBillingReqDTO;
import com.org.hosply360.dto.IPDDTO.ParticipantChargeDTO;
import com.org.hosply360.exception.IPDException;
import com.org.hosply360.repository.IPD.IPDSurgeryBillingRepository;
import com.org.hosply360.service.IPD.IPDFinancialSummaryService;
import com.org.hosply360.service.IPD.IPDReceiptService;
import com.org.hosply360.service.IPD.IPDSurgeryBillingService;
import com.org.hosply360.util.Others.EntityFetcherUtil;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IPDSurgeryBillingServiceImpl implements IPDSurgeryBillingService {

    private static final Logger logger = LoggerFactory.getLogger(IPDSurgeryBillingServiceImpl.class);

    private final IPDSurgeryBillingRepository surgeryBillingRepository;
    private final EntityFetcherUtil entityFetcherUtil;
    private final IPDFinancialSummaryService ipdFinancialSummaryService;
    private final IPDSurgeryBillingRepository ipdSurgeryBillingRepository;
    private final IPDReceiptService ipdReceiptService;
    private final MongoTemplate mongoTemplate;

    @Override
    @Transactional
    public String createSurgeryBilling(IPDSurgeryBillingReqDTO reqDTO) {
        ValidatorHelper.validateObject(reqDTO);
        logger.info("Creating IPD Surgery Billing for Surgery ID: {}", reqDTO.getSurgeryId());

        if (reqDTO.getSurgeryId() != null) {
            Optional<IPDSurgeryBilling> existing = surgeryBillingRepository.findBySurgeryId(reqDTO.getSurgeryId());
            if (existing.isPresent()) {
                updateSurgeryBilling(reqDTO);
                return existing.get().getId();
            }
        }

        IPDAdmission admission = entityFetcherUtil.getIPDAdmissionOrThrow(reqDTO.getIpdAdmissionId());
        Organization organization = entityFetcherUtil.getOrganizationOrThrow(reqDTO.getOrganizationId());

        List<ParticipantCharge> surgeonList = mapParticipantList(reqDTO.getSurgeonDetails());
        List<ParticipantCharge> anaesthetistList = mapParticipantList(reqDTO.getAnaesthetistDetails());
        List<ParticipantCharge> pediatricsList = mapParticipantList(reqDTO.getPediatricsDetails());

        BigDecimal surgeonsTotal = safe(calculateTotal(surgeonList));
        BigDecimal anaesthetistTotal = safe(calculateTotal(anaesthetistList));
        BigDecimal pediatricsTotal = safe(calculateTotal(pediatricsList));


        BigDecimal otInstrumentation = safe(reqDTO.getOtInstrumentationCharges());
        BigDecimal surgeonsOt = safe(reqDTO.getSurgeonsOtCharges());
        BigDecimal surgeonsConsumable = safe(reqDTO.getSurgeonsOtConsumableCharges());
        BigDecimal anaesthetistOt = safe(reqDTO.getAnaesthetistsOtCharges());
        BigDecimal anaesthetistConsumable = safe(reqDTO.getAnaesthetistsOtConsumableCharges());
        BigDecimal pediatricsOt = safe(reqDTO.getPediatricsOtCharges());
        BigDecimal pediatricsConsumable = safe(reqDTO.getPediatricsOtConsumableCharges());
        BigDecimal surgeryCharge = safe(reqDTO.getSurgeryCharges());
        BigDecimal totalSurgeryAmount = reqDTO.getTotalSurgeryExpenses();

        IPDSurgeryBilling billing = IPDSurgeryBilling.builder()
                .organizationId(organization.getId())
                .ipdAdmissionId(admission.getId())
                .surgeryId(reqDTO.getSurgeryId())
                .billingDateTime(LocalDateTime.now())
                .ipdSurgeryBillNo(generateBillingNumber())

                .surgeonDetails(surgeonList)
                .anaesthetistDetails(anaesthetistList)
                .pediatricsDetails(pediatricsList)

                .surgeonsTotal(surgeonsTotal)
                .anaesthetistTotal(anaesthetistTotal)
                .pediatricsTotal(pediatricsTotal)

                .surgeonsOtCharges(surgeonsOt)
                .surgeonsOtConsumableCharges(surgeonsConsumable)
                .anaesthetistOtCharges(anaesthetistOt)
                .anaesthetistOtConsumableCharges(anaesthetistConsumable)
                .pediatricsOtCharges(pediatricsOt)
                .pediatricsOtConsumableCharges(pediatricsConsumable)
                .surgeryCharges(surgeryCharge)
                .otInstrumentationCharges(otInstrumentation)

                .paidAmount(BigDecimal.ZERO)
                .balanceAmount(totalSurgeryAmount)
                .refundAmount(BigDecimal.ZERO)

                .totalSurgeryAmount(totalSurgeryAmount)
                .hasCancelled(false)
                .hasSettled(false)
                .build();
        IPDSurgeryBilling saved = surgeryBillingRepository.save(billing);
        ipdFinancialSummaryService.updateSummaryAfterSurgeryBillingChange(billing);


        return saved.getId();
    }


    @Override
    @Transactional
    public void updateSurgeryBilling(IPDSurgeryBillingReqDTO reqDTO) {
        ValidatorHelper.validateObject(reqDTO);
        logger.info("Updating IPD Surgery Billing for Surgery ID: {}", reqDTO.getSurgeryId());

        IPDSurgeryBilling existing = surgeryBillingRepository.findBySurgeryId(reqDTO.getSurgeryId())
                .orElseThrow(() -> new IPDException(ErrorConstant.ITEM_NOT_FOUND, HttpStatus.NOT_FOUND));

        List<ParticipantCharge> surgeonList = mapParticipantList(reqDTO.getSurgeonDetails());
        List<ParticipantCharge> anaesthetistList = mapParticipantList(reqDTO.getAnaesthetistDetails());
        List<ParticipantCharge> pediatricsList = mapParticipantList(reqDTO.getPediatricsDetails());

        BigDecimal surgeonsTotal = calculateTotal(surgeonList);
        BigDecimal anaesthetistTotal = calculateTotal(anaesthetistList);
        BigDecimal pediatricsTotal = calculateTotal(pediatricsList);


        BigDecimal otInstrumentation = nvl(reqDTO.getOtInstrumentationCharges());
        BigDecimal surgeonsOt = nvl(reqDTO.getSurgeonsOtCharges());
        BigDecimal surgeonsConsumable = nvl(reqDTO.getSurgeonsOtConsumableCharges());
        BigDecimal anaesthetistsOt = nvl(reqDTO.getAnaesthetistsOtCharges());
        BigDecimal anaesthetistsConsumable = nvl(reqDTO.getAnaesthetistsOtConsumableCharges());
        BigDecimal pediatricsOt = nvl(reqDTO.getPediatricsOtCharges());
        BigDecimal pediatricsConsumable = nvl(reqDTO.getPediatricsOtConsumableCharges());
        BigDecimal surgeryCharges = nvl(reqDTO.getSurgeryCharges());

        BigDecimal totalSurgeryAmount = reqDTO.getTotalSurgeryExpenses();

        existing.setSurgeonDetails(surgeonList);
        existing.setAnaesthetistDetails(anaesthetistList);
        existing.setPediatricsDetails(pediatricsList);

        existing.setSurgeonsTotal(surgeonsTotal);
        existing.setAnaesthetistTotal(anaesthetistTotal);
        existing.setPediatricsTotal(pediatricsTotal);

        existing.setSurgeonsOtCharges(surgeonsOt);
        existing.setSurgeonsOtConsumableCharges(surgeonsConsumable);
        existing.setAnaesthetistOtCharges(anaesthetistsOt);
        existing.setAnaesthetistOtConsumableCharges(anaesthetistsConsumable);
        existing.setPediatricsOtCharges(pediatricsOt);
        existing.setPediatricsOtConsumableCharges(pediatricsConsumable);
        existing.setSurgeryCharges(surgeryCharges);
        existing.setOtInstrumentationCharges(otInstrumentation);
        existing.setPaidAmount(BigDecimal.ZERO);
        existing.setBalanceAmount(totalSurgeryAmount.subtract(existing.getPaidAmount()));
        existing.setRefundAmount(BigDecimal.ZERO);

        existing.setTotalSurgeryAmount(totalSurgeryAmount);
        existing.setBillingDateTime(LocalDateTime.now());
        surgeryBillingRepository.save(existing);
        ipdFinancialSummaryService.updateSummaryAfterSurgeryBillingChange(existing);

    }



    @Override
    public String surgeryBillPayment(IPDBillingPaymentDTO paymentDTO) {

        // 1. Fetch surgery billing record
        IPDSurgeryBilling surgeryBilling = ipdSurgeryBillingRepository.findById(paymentDTO.getBillingId())
                .orElseThrow(() -> new IPDException(ErrorConstant.ITEM_NOT_FOUND, HttpStatus.NOT_FOUND));

        // 2. Validate billing state
        if (Boolean.TRUE.equals(surgeryBilling.getHasCancelled())) {
            throw new IPDException("Cannot make payment for a cancelled surgery bill", HttpStatus.BAD_REQUEST);
        }

        if (Boolean.TRUE.equals(surgeryBilling.getHasSettled())) {
            throw new IPDException("Surgery bill already settled", HttpStatus.BAD_REQUEST);
        }

        if (paymentDTO.getAmountPaid() == null || paymentDTO.getAmountPaid().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IPDException("Invalid payment amount", HttpStatus.BAD_REQUEST);
        }

        // 3. Calculate updated payment and balance
        BigDecimal currentPaid = surgeryBilling.getPaidAmount() != null ? surgeryBilling.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal newPaidAmount = currentPaid.add(paymentDTO.getAmountPaid());
        BigDecimal totalAmount =surgeryBilling.getTotalSurgeryAmount();

        BigDecimal newBalance = totalAmount.subtract(newPaidAmount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IPDException("Payment exceeds remaining balance", HttpStatus.BAD_REQUEST);
        }

        // 4. Update billing
        surgeryBilling.setPaidAmount(newPaidAmount);
        surgeryBilling.setBalanceAmount(newBalance);
        surgeryBilling.setBillingDateTime(LocalDateTime.now());
        if (newBalance.compareTo(BigDecimal.ZERO) == 0) {
            surgeryBilling.setHasSettled(true);
        }

        // 5. Save updated surgery billing
        ipdSurgeryBillingRepository.save(surgeryBilling);
        ipdFinancialSummaryService.updateSummaryAfterSurgeryBillingChange(surgeryBilling);
        // or async version if you have one
        // ipdFinancialSummaryAsyncService.updateSummaryAfterBillingChangeAsync(surgeryBilling);

        // 6. Build receipt request
        IPDReceiptReqDTO receiptReq = new IPDReceiptReqDTO();
        receiptReq.setOrganizationId(paymentDTO.getOrganizationId());
        receiptReq.setAdmissionId(paymentDTO.getAdmissionId());
        receiptReq.setTotalRecieveAmount(paymentDTO.getAmountPaid());
        receiptReq.setPaymentMode(paymentDTO.getPaymentMode());
        receiptReq.setRemarks(paymentDTO.getRemarks());
        receiptReq.setReceiptType(paymentDTO.getReceiptType());
        receiptReq.setBillingId(paymentDTO.getBillingId());

        // If payment mode is CHEQUE, add cheque details
        if (paymentDTO.getPaymentMode() == PaymentMode.CHEQUE) {
            receiptReq.setChequeNumber(paymentDTO.getChequeNumber());
            receiptReq.setChequeDateTime(paymentDTO.getChequeDateTime());
            receiptReq.setBankName(paymentDTO.getBankName());
            receiptReq.setBranchName(paymentDTO.getBranchName());
            receiptReq.setAccountHolderName(paymentDTO.getAccountHolderName());
            receiptReq.setIfscCode(paymentDTO.getIfscCode());
        }

        // 7. Create receipt
        return ipdReceiptService.createReceipt(receiptReq);


    }

    @Override
    public List<IPDSurgeryBillingDTO> getAllIPDSurgeryBillings(String organizationId, String admissionId, String id) {
        Query query = new Query();
        if (organizationId != null && !organizationId.isBlank()) {
            query.addCriteria(Criteria.where("organizationId").is(organizationId));
        }
        if (admissionId != null && !admissionId.isBlank()) {
            query.addCriteria(Criteria.where("ipdAdmissionId").is(admissionId));
        }
        if (id != null && !id.isBlank()) {
            query.addCriteria(Criteria.where("_id").is(new ObjectId(id)));
        }
        query.with(Sort.by(Sort.Direction.DESC, "billingDateTime"));
        logger.info("Executing IPD Surgery Billing Query: {}", query);
        List<IPDSurgeryBilling> surgeryBillings = mongoTemplate.find(query, IPDSurgeryBilling.class);
        return surgeryBillings.stream().map(billing -> {
            IPDSurgeryBillingDTO dto = ObjectMapperUtil.copyObject(billing, IPDSurgeryBillingDTO.class);
            dto.setOrganizationId(billing.getOrganizationId());
            dto.setIpdAdmissionId(billing.getIpdAdmissionId());
            dto.setSurgeryId(billing.getSurgeryId());
            dto.setIpdSurgeryBillNo(billing.getIpdSurgeryBillNo());
            dto.setBillingDateTime(billing.getBillingDateTime());
            dto.setHasCancelled(billing.getHasCancelled());
            dto.setHasSettled(billing.getHasSettled());
            dto.setPaidAmount(billing.getPaidAmount());
            dto.setBalanceAmount(billing.getBalanceAmount());

            if (billing.getSurgeonDetails() != null) {
                dto.setSurgeonDetails(
                        billing.getSurgeonDetails().stream().map(participant -> {
                            ParticipantChargeDTO pc = new ParticipantChargeDTO();
                            pc.setDoctorId(participant.getId());
                            pc.setDoctorName(participant.getName());
                            pc.setCharge(participant.getCharge());
                            return pc;
                        }).toList()
                );
            }
            if (billing.getAnaesthetistDetails() != null) {
                dto.setAnaesthetistDetails(
                        billing.getAnaesthetistDetails().stream().map(participant -> {
                            ParticipantChargeDTO pc = new ParticipantChargeDTO();
                            pc.setDoctorId(participant.getId());
                            pc.setDoctorName(participant.getName());
                            pc.setCharge(participant.getCharge());
                            return pc;
                        }).toList()
                );
            }
            if (billing.getPediatricsDetails() != null) {
                dto.setPediatricsDetails(
                        billing.getPediatricsDetails().stream().map(participant -> {
                            ParticipantChargeDTO pc = new ParticipantChargeDTO();
                            pc.setDoctorId(participant.getId());
                            pc.setDoctorName(participant.getName());
                            pc.setCharge(participant.getCharge());
                            return pc;
                        }).toList()
                );
            }

            return dto;
        }).toList();
    }



    @Override
    public String cancelIPDSurgeryBilling(IPDSurgeryBillCancelDTO ipdSurgeryBillCancelDTO) {


        // 1. Fetch the surgery billing
        IPDSurgeryBilling billing = ipdSurgeryBillingRepository.findBySurgeryId(ipdSurgeryBillCancelDTO.getSurgeryId())
                .orElseThrow(() -> new IPDException(ErrorConstant.BILLING_NOT_FOUND, HttpStatus.NOT_FOUND));

        // 2. Check if already cancelled
        if (Boolean.TRUE.equals(billing.getHasCancelled())) {
            throw new IPDException(ErrorConstant.BILLING_ALREADY_CANCELLED, HttpStatus.BAD_REQUEST);
        }

        // 3. Mark as cancelled
        billing.setHasCancelled(true);
        billing.setCancelReason(ipdSurgeryBillCancelDTO.getCancelReason());
        billing.setHasSettled(true);
        billing.setCancelDateTime(LocalDateTime.now()); // or set a dedicated cancelDateTime if defined

        // 4. Optional — reset participant charges to zero to indicate cancellation
        if (billing.getSurgeonDetails() != null) {
            billing.getSurgeonDetails().forEach(p -> p.setCharge(BigDecimal.ZERO));
        }
        if (billing.getAnaesthetistDetails() != null) {
            billing.getAnaesthetistDetails().forEach(p -> p.setCharge(BigDecimal.ZERO));
        }
        if (billing.getPediatricsDetails() != null) {
            billing.getPediatricsDetails().forEach(p -> p.setCharge(BigDecimal.ZERO));
        }

        // 5. Recalculate billing totals
        recalculateSurgeryBillingTotals(billing);

        // 6. Save updated billing
        ipdSurgeryBillingRepository.save(billing);

        // 7. Update financial summary
        ipdFinancialSummaryService.updateSummaryAfterSurgeryBillingChange(billing);
        // or async version if available:
        // ipdFinancialSummaryAsyncService.updateSummaryAfterBillingChangeAsync(billing);

        logger.info("Surgery Billing ID: {} canceled successfully", ipdSurgeryBillCancelDTO.getSurgeryId());
        return ipdSurgeryBillCancelDTO.getSurgeryId();
    }


    private void recalculateSurgeryBillingTotals(IPDSurgeryBilling billing) {

        if (billing == null) return;

        // 1. Sum all relevant active totals
        BigDecimal surgeonsTotal = billing.getSurgeonsTotal() != null ? billing.getSurgeonsTotal() : BigDecimal.ZERO;
        BigDecimal anaesthetistTotal = billing.getAnaesthetistTotal() != null ? billing.getAnaesthetistTotal() : BigDecimal.ZERO;
        BigDecimal pediatricsTotal = billing.getPediatricsTotal() != null ? billing.getPediatricsTotal() : BigDecimal.ZERO;
        BigDecimal otInstrumentationCharges = billing.getOtInstrumentationCharges() != null ? billing.getOtInstrumentationCharges() : BigDecimal.ZERO;

        // 2. Calculate grand total
        BigDecimal totalAmount = surgeonsTotal
                .add(anaesthetistTotal)
                .add(pediatricsTotal)
                .add(otInstrumentationCharges);

        billing.setTotalSurgeryAmount(totalAmount);

        // 3. If no active total remains (zero), reset all amounts
        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.info("No active participant charges found for Surgery Billing ID: {} — resetting amounts", billing.getId());
            billing.setPaidAmount(BigDecimal.ZERO);
            billing.setBalanceAmount(BigDecimal.ZERO);
            billing.setRefundAmount(BigDecimal.ZERO);
            return;
        }

        // 4. Apply discount

        // 5. Calculate net, paid, and balanc

        BigDecimal paidAmount = billing.getPaidAmount() != null ? billing.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal balanceAmount = totalAmount.subtract(paidAmount);

        billing.setBalanceAmount(balanceAmount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : balanceAmount);
    }



    private List<ParticipantCharge> mapParticipantList(List<ParticipantChargeDTO> dtoList) {
        if (dtoList == null) return List.of();

        return dtoList.stream().map(dto -> ParticipantCharge.builder()
                .id(dto.getDoctorId())
                .name(dto.getDoctorName())
                .charge(dto.getCharge())
                .build()
        ).toList();
    }

    private BigDecimal calculateTotal(List<ParticipantCharge> list) {
        if (list == null) return BigDecimal.ZERO;
        return list.stream()
                .map(ParticipantCharge::getCharge)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Helper method
    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
    private BigDecimal nvl(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private String generateBillingNumber() {
        return "IPDSURGERYBILL-" + LocalDateTime.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

}
