package com.org.hosply360.dao.IPD;

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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "ipd_final_bill")
public class IPDFinalBill extends BaseModel {

    @Id
    private String id;

    private String organizationId;

    @DBRef
    @Field("admission")
    private IPDAdmission admission;

    @Field("final_bill_no")
    private String finalBillNo;

    @Field("final_bill_date")
    private LocalDate finalBillDate;

    @Field("total_bill_amount")
    private BigDecimal totalBillAmount;

    @Field("total_surgery_charges")
    private BigDecimal totalSurgeryCharges;

    @Field("total_advance_amount")
    private BigDecimal totalAdvanceAmount;

    @Field("total_paid_amount")
    private BigDecimal totalPaidAmount;

    @Field("total_discount_amount")
    private BigDecimal totalDiscountAmount;

    @Field("total_refund_amount")
    private BigDecimal totalRefundAmount;

    @Field("net_payable_amount")
    private BigDecimal netPayableAmount;

    @Field("balance_amount")
    private BigDecimal balanceAmount;

    @Field("refund_balance")
    private BigDecimal refundBalance;

    @Field("settled_date")
    private LocalDateTime settledDate;

    @Field("remarks")
    private String remarks;

    @Field("additional_discount_amount")
    private BigDecimal additionalDiscountAmount;


    @DBRef
    @Field("expense_summary")
    private List<IPDBilling> expenseSummary;

    @DBRef
    @Field("surgeries")
    private List<IPDSurgeryBilling> surgeries;

    @Field("is_settled")
    private Boolean hasSettled = false;

    @Field("is_preview")
    private Boolean isPreview = true; // default true for drafts



}
