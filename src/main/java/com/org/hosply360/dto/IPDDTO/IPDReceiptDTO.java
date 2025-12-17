package com.org.hosply360.dto.IPDDTO;

import com.org.hosply360.constant.Enums.PaymentMode;
import com.org.hosply360.constant.Enums.ReceiptType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
public class IPDReceiptDTO {
    private String id;
    private String receiptNo;
    private LocalDate receiptDate;
    private String organizationId;
    private String admissionId;
    private String billingId;
    private BigDecimal totalRecieveAmount;
    private PaymentMode paymentMode;
    private String remarks;
    private ReceiptType receiptType;
    private String chequeNumber;
    private LocalDateTime chequeDateTime;
    private String bankName;
    private String branchName;
    private String accountHolderName;
    private String ifscCode;
}
