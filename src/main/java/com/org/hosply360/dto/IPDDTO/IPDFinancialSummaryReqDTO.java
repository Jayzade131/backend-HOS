package com.org.hosply360.dto.IPDDTO;

import com.org.hosply360.constant.Enums.PaymentMode;
import com.org.hosply360.constant.Enums.ReceiptType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class IPDFinancialSummaryReqDTO {
    private String id;
    private String organizationId;
    private String ipdAdmission;
    private BigDecimal totalDepositAmount;
    private BigDecimal totalRefundedAmount;
    private PaymentMode paymentMode;
    private ReceiptType receiptType;
    private String chequeNumber;
    private String bankName;
    private String branchName;
    private String accountHolderName;
    private String ifscCode;
    private String remarks;
}
