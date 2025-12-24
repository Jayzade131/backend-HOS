package com.org.hosply360.dao.pathology;

import com.org.hosply360.constant.Enums.PaymentMode;
import com.org.hosply360.constant.Enums.ReceiptStatus;
import com.org.hosply360.dao.other.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "pathology_receipts")
public class PathologyReceipt extends BaseModel {
    @Id
    private String id;

    @Field("test_manager_id ")
    private String testManager;

    @Field("receipt_number")
    private String receiptNumber;

    @Field("receipt_date")
    private LocalDateTime receiptDate;

    @Field("generated_by")
    private String generatedBy;

    @Field("paid_amount")
    private Double paidAmount;

    @Field("paymentType")
    private PaymentMode paymentType;

    @Field("cheque_number")
    private String chequeNumber;

    @Field("bank_name")
    private String bankName;

    @Field("cheque_date")
    private LocalDate chequeDate;

    @Field("receipt_given")
    private ReceiptStatus receiptGiven;

    @Field("defunct")
    private Boolean defunct;

}
