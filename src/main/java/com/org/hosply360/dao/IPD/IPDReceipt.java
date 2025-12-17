package com.org.hosply360.dao.IPD;

import com.org.hosply360.constant.Enums.PaymentMode;
import com.org.hosply360.constant.Enums.ReceiptType;
import com.org.hosply360.dao.other.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "ipd_receipt")
public class IPDReceipt extends BaseModel {
    @Id
    private String id;
    private String receiptNo;
    private LocalDate receiptDate;
    private String organizationId;
    @DBRef
    private IPDAdmission IPDadmissionId;
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
    private boolean pdfGenerated;
}
