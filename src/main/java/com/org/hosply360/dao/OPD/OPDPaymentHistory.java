package com.org.hosply360.dao.OPD;
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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "opd_payment_history")
public class OPDPaymentHistory extends BaseModel {

    @Id
    private String id;

    @Field("invoice_id")
    private String invoiceId;

    @Field("receipt_id")
    private String receiptId;

    @Field("payment_amount")
    private Double paymentAmount;

    @Field("payment_type")
    private String paymentType;

    @Field("cheque_number")
    private String chequeNumber;

    @Field("bank_name")
    private String bankName;

    @Field("cheque_date")
    private LocalDate chequeDate;

    @Field("payment_date")
    private LocalDateTime paymentDate;

    @Field("recieved_by")
    private String recievedBy;

    @Field("defunct")
    private Boolean defunct;
}
