package com.org.hosply360.service.IPD.impl;

import com.org.hosply360.dao.IPD.IPDBilling;
import com.org.hosply360.dao.IPD.IPDFinancialSummary;
import com.org.hosply360.repository.IPD.IPDBillingRepository;
import com.org.hosply360.repository.IPD.IPDFinancialSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IPDFinancialSummaryAsyncService {

        private static final Logger logger = LoggerFactory.getLogger(IPDFinancialSummaryAsyncService.class);

        private final IPDFinancialSummaryRepository financialSummaryRepository;
        private final IPDBillingRepository ipdBillingRepository;


        @Async("financialTaskExecutor")
        public void updateSummaryAfterBillingChangeAsync(IPDBilling billing) {
            try {
                if (billing == null || billing.getAdmissionId() == null) {
                    logger.warn("Skipping FinancialSummary update — billing or admission is null");
                    return;
                }
                String admissionId = billing.getAdmissionId().getId();
                logger.info("Async update started for Financial Summary — Admission ID: {}", admissionId);

                // Fetch or create summary
                IPDFinancialSummary summary = financialSummaryRepository.findByIpdAdmissionId(admissionId)
                        .stream()
                        .findFirst()
                        .orElseGet(() -> createEmptySummary(billing));

                // Recompute totals based on all non-canceled bills
                List<IPDBilling> activeBills = ipdBillingRepository.findByAdmissionIdAndCanceledStatus(admissionId, false);

                BigDecimal totalBilledAmount = activeBills.stream()
                        .map(b -> b.getTotalAmount() == null ? BigDecimal.ZERO : b.getTotalAmount())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalNetAmount = activeBills.stream()
                        .map(b -> b.getNetAmount() == null ? BigDecimal.ZERO : b.getNetAmount())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalPaidAmount = activeBills.stream()
                        .map(b -> b.getPaidAmount() == null ? BigDecimal.ZERO : b.getPaidAmount())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalRefundedAmount = activeBills.stream()
                        .map(b -> b.getRefundAmount() == null ? BigDecimal.ZERO : b.getRefundAmount())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal deposit = summary.getTotalDepositAmount() == null ? BigDecimal.ZERO : summary.getTotalDepositAmount();

                // pending = net - (paid + deposit - refund)
                BigDecimal pending = totalNetAmount.subtract(totalPaidAmount.add(deposit).subtract(totalRefundedAmount));

                summary.setTotalBilledAmount(totalBilledAmount);
                summary.setTotalNetBillAmount(totalNetAmount);
                summary.setTotalBillPaidAmount(totalPaidAmount);
                summary.setTotalRefundedAmount(totalRefundedAmount);
                summary.setPendingAmount(pending.max(BigDecimal.ZERO));
                summary.setLastUpdated(LocalDateTime.now());

                financialSummaryRepository.save(summary);
                logger.info("Async Financial Summary updated for Admission ID: {}", admissionId);

            } catch (Exception e) {
                logger.error("Error in async summary update for billing id: {} ", billing != null ? billing.getId() : "null", e);
                // optional: send to alerting / metrics / dead-letter
            }
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

}
