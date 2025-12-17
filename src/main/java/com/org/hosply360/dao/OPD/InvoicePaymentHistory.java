package com.org.hosply360.dao.OPD;

import com.org.hosply360.dao.other.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "invoice_payment_history")
public class InvoicePaymentHistory extends BaseModel {

    @Id
    private String id;

    @DBRef
    private OPDInvoice opdInvoice;

    @Field("payment_mode")
    private String paymentMode;

    @Field("amount")
    private String amount;

    @Field("payment_date")
    private LocalDateTime paymentDate;
}
