package com.org.hosply360.dao.OPD;

import com.org.hosply360.constant.Enums.ReceiptStatus;
import com.org.hosply360.dao.frontDeskDao.Patient;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dao.other.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Document(collection = "opd_invoice")
public class OPDInvoice extends BaseModel {

    @Id
    private String id;

    @DBRef
    private Organization org;

    @DBRef
    private Patient patient;

    @DBRef
    private Appointment appointment;

    @Field("consultant")
    private String consultant;

    @Field("invoice_number")
    private String invoiceNumber;

    @Field("invoice_date")
    private LocalDateTime invoiceDate;

    @Field("invoice_items")
    private List<InvoiceItems> invoiceItems;

    @Field("total_amount")
    private Double totalAmount;

    @Field("discount_amount")
    private Double discountAmount;

    @Field("amount_to_pay")
    private Double amountToPay;

    @Field("paid_amount")
    private Double paidAmount;

    @Field("balance_amount")
    private Double balanceAmount;

    @Field("last_paid_amount")
    private Double lastPaidAmount;

    @Field("last_payment_type")
    private String lastPaymentType;

    @Field("payment_status")
    private String status;

    @Field("receipt_given")
    private ReceiptStatus receiptGiven;

    @Field("remark")
    private String remark;

    @Field("defunct")
    private boolean defunct;

}
