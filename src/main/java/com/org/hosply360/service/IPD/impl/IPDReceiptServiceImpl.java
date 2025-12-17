package com.org.hosply360.service.IPD.impl;

import com.org.hosply360.constant.Enums.PaymentMode;
import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.IPD.IPDAdmission;
import com.org.hosply360.dao.IPD.IPDReceipt;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dto.IPDDTO.IPDReceiptDTO;
import com.org.hosply360.dto.IPDDTO.IPDReceiptReqDTO;
import com.org.hosply360.dto.OPDDTO.PdfResponseDTO;
import com.org.hosply360.exception.IPDException;
import com.org.hosply360.repository.IPD.IPDReceiptRepository;
import com.org.hosply360.service.IPD.IPDReceiptService;
import com.org.hosply360.util.Others.EntityFetcherUtil;
import com.org.hosply360.util.PDFGenUtil.IPD.ReceiptPdfGenerator;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import com.org.hosply360.util.validator.ValidatorHelper;
import lombok.RequiredArgsConstructor;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class IPDReceiptServiceImpl implements IPDReceiptService {
    private static final Logger logger = LoggerFactory.getLogger(IPDReceiptServiceImpl.class);

    private final IPDReceiptRepository receiptRepository;
    private final EntityFetcherUtil entityFetcherUtil;
    private final ReceiptPdfGenerator receiptPdfGenerator;
    private final MongoTemplate mongoTemplate;

    @Override
    public String createReceipt(IPDReceiptReqDTO reqDTO) {
        ValidatorHelper.validateObject(reqDTO);
        IPDAdmission admission = entityFetcherUtil.getIPDAdmissionOrThrow(reqDTO.getAdmissionId());
        Organization organization = entityFetcherUtil.getOrganizationOrThrow(reqDTO.getOrganizationId());
        validatePaymentDetails(reqDTO);
        IPDReceipt receipt = IPDReceipt.builder()
                .receiptNo(generateReceiptNo())
                .receiptDate(LocalDate.now())
                .organizationId(organization.getId())
                .IPDadmissionId(admission)
                .billingId(reqDTO.getBillingId() != null ? reqDTO.getBillingId() : null)
                .totalRecieveAmount(reqDTO.getTotalRecieveAmount())
                .paymentMode(reqDTO.getPaymentMode())
                .remarks(reqDTO.getRemarks())
                .receiptType(reqDTO.getReceiptType())
                .chequeNumber(reqDTO.getChequeNumber())
                .chequeDateTime(reqDTO.getChequeDateTime())
                .bankName(reqDTO.getBankName())
                .branchName(reqDTO.getBranchName())
                .accountHolderName(reqDTO.getAccountHolderName())
                .ifscCode(reqDTO.getIfscCode())
                .pdfGenerated(false)
                .build();
         receiptRepository.save(receipt);
         return receipt.getId();
    }

    @Override
    public List<IPDReceiptDTO> getReceipts(
            String orgId,
            LocalDate fromDate,
            LocalDate toDate,
            String ipdAdmissionId,
            String ipdReceiptId,
            String receiptType
    ) {
        ValidatorHelper.validateObject(orgId);
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        // Always filter by organization
        criteriaList.add(Criteria.where("organizationId").is(orgId));

        // Filter by IPD Admission ID
        if (ipdAdmissionId != null && !ipdAdmissionId.isBlank()) {
            // Assuming IPDadmissionId is a DBRef pointing to an ObjectId
            criteriaList.add(Criteria.where("IPDadmissionId.$id").is(new ObjectId(ipdAdmissionId)));
        }

        // Filter by Receipt ID
        if (ipdReceiptId != null && !ipdReceiptId.isBlank()) {
            criteriaList.add(Criteria.where("_id").is(new ObjectId(ipdReceiptId)));
        }

        // 💡 NEW FILTER: Filter by Receipt Type
        if (receiptType != null && !receiptType.isBlank()) {
            // The field name in the document is "receiptType"
            criteriaList.add(Criteria.where("receiptType").is(receiptType));
        }

        // Filter by Date Range
        if (fromDate != null && toDate != null) {
            criteriaList.add(Criteria.where("receiptDate")
                    .gte(fromDate.atStartOfDay())
                    .lte(toDate.atTime(23, 59, 59)));
        } else if (fromDate != null) {
            criteriaList.add(Criteria.where("receiptDate").gte(fromDate.atStartOfDay()));
        } else if (toDate != null) {
            criteriaList.add(Criteria.where("receiptDate").lte(toDate.atTime(23, 59, 59)));
        }

        // Combine all criteria using AND operator
        query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));

        // Sort by receiptDate in descending order
        query.with(Sort.by(Sort.Direction.DESC, "receiptDate"));

        System.out.println("Executing Receipt Query: " + query);

        // Execute query and map to DTOs
        List<IPDReceipt> receipts = mongoTemplate.find(query, IPDReceipt.class);

        return receipts.stream().map(receipt -> {
            IPDReceiptDTO dto = ObjectMapperUtil.copyObject(receipt, IPDReceiptDTO.class);
            dto.setAdmissionId(receipt.getIPDadmissionId() != null ? receipt.getIPDadmissionId().getId() : null);
            dto.setOrganizationId(receipt.getOrganizationId());
            return dto;
        }).toList();
    }

    private String generateReceiptNo() {
        long count = receiptRepository.count() + 1;
        return String.format("REC-%d-%06d", LocalDate.now().getYear(), count);
    }

    private void validatePaymentDetails(IPDReceiptReqDTO reqDTO) {
        BigDecimal totalReceiveAmount = reqDTO.getTotalRecieveAmount();
        if (Objects.isNull(totalReceiveAmount) || totalReceiveAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IPDException(ErrorConstant.IPD_RECEIPT_TOTAL_PAID_AMOUNT_MUST_BE_GREATER_THAN_ZERO, HttpStatus.BAD_REQUEST);
        }
        PaymentMode paymentMode = reqDTO.getPaymentMode();
        if (Objects.isNull(paymentMode)) {
            throw new IPDException(ErrorConstant.IPD_RECEIPT_PAYMENT_MODE_IS_REQUIRED, HttpStatus.BAD_REQUEST);
        }
        switch (paymentMode) {
            case CHEQUE: validateChequeDetails(reqDTO);break;
            case CASH: clearNonCashFields(reqDTO);break;
            default: clearNonCashFields(reqDTO);break;
        }
    }
    private void validateChequeDetails(IPDReceiptReqDTO reqDTO) {
        if (isEmpty(reqDTO.getChequeNumber())) {
            throw new IPDException(ErrorConstant.IPD_RECEIPT_CHEQUE_NUMBER_IS_REQUIRED, HttpStatus.BAD_REQUEST);
        }
        if (isEmpty(reqDTO.getBankName())) {
            throw new IPDException(ErrorConstant.IPD_RECEIPT_BANK_NAME_IS_REQUIRED, HttpStatus.BAD_REQUEST);
        }
    }
    private void clearNonCashFields(IPDReceiptReqDTO reqDTO) {
        reqDTO.setChequeNumber(null);
        reqDTO.setChequeDateTime(null);
        reqDTO.setBankName(null);
        reqDTO.setBranchName(null);
        reqDTO.setAccountHolderName(null);
        reqDTO.setIfscCode(null);
    }

    private boolean isEmpty(String value) {
        return Objects.isNull(value) || value.trim().isEmpty();
    }

    @Override
    public PdfResponseDTO generateReceiptPdf(String receiptId) {
        IPDReceipt receipt = entityFetcherUtil.getIPDReceiptOrThrow(receiptId);
        IPDAdmission admission = entityFetcherUtil.getIPDAdmissionOrThrow(receipt.getIPDadmissionId().getId());
        boolean isDuplicate = receipt.isPdfGenerated();
        byte[] pdfBytes = receiptPdfGenerator.generateReceiptPdf(
                receipt.getReceiptNo(),
                receipt.getReceiptDate(),
                admission,
                receipt.getIPDadmissionId().getPatient().getPatientPersonalInformation().getFirstName() + " " +
                        receipt.getIPDadmissionId().getPatient().getPatientPersonalInformation().getLastName(),
                receipt.getIPDadmissionId().getPatient().getPatientPersonalInformation().getMiddleName(),
                receipt.getTotalRecieveAmount(),
                receipt.getPaymentMode().name(),
                receipt.getRemarks(),
                receipt.getReceiptType().name(),
                "admin",
                isDuplicate
        );
        if (!isDuplicate) {
            receipt.setPdfGenerated(true);
            receiptRepository.save(receipt);
        }
        String fileName = "IPD_Receipt_" + receipt.getReceiptNo() + ".pdf";
        PdfResponseDTO dto = new PdfResponseDTO();
        dto.setBody(pdfBytes);
        dto.setFileName(fileName);
        return dto;
    }
}
