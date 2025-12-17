package com.org.hosply360.dto.IPDDTO;

import com.org.hosply360.constant.Enums.PaymentMode;
import com.org.hosply360.constant.Enums.ReceiptType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IPDBillingPaymentDTO {

    private String billingId;
    private String organizationId;
    private String admissionId;
    private BigDecimal amountPaid;
    private PaymentMode paymentMode;
    private String remarks;
    private ReceiptType receiptType;

    // Cheque Details (only used if paymentMode == CHEQUE)
    private String chequeNumber;
    private LocalDateTime chequeDateTime;
    private String bankName;
    private String branchName;
    private String accountHolderName;
    private String ifscCode;
}
