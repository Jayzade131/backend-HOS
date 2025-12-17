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
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "ipd_billing")
public class IPDBilling extends BaseModel {

    @Id
    private String id;

    @Field("organization_id")
    private String organizationId;

    @DBRef
    @Field("admission_id")
    private IPDAdmission admissionId; // Reference to IPDAdmission

    @Field("billing_no")
    private String billingNo; // Auto-generated unique billing number (BILL-2025-XXXXXX)

    @Field("billing_date_time")
    private LocalDateTime billingDateTime; // Bill generation date

    @Field("billing_items")
    private List<IPDBillingItem> billingItems; // Line items for this bill

    @Field("total_amount")
    private BigDecimal totalAmount; // Sum of all item charges

    @Field("discount_amount")
    private BigDecimal discountAmount; // Discount applied

    @Field("net_amount")
    private BigDecimal netAmount; // totalAmount - discountAmount

    @Field("paid_amount")
    private BigDecimal paidAmount;

    @Field("balance_amount")
    private BigDecimal balanceAmount;

    @Field("refund_amount")
    private BigDecimal refundAmount;

    @Field("remarks")
    private String remarks;

    @Field("canceled")
    private Boolean canceled;

    @Field("cancel_reason")
    private String cancelReason;

    @Field("cancel_date_time")
    private LocalDateTime cancelDateTime;

    @Field("canceled_by")
    private String canceledBy;

    @Field("is_settled")
    private Boolean hasSettled = false;

    @Field("is_advance_adjusted")
    private Boolean hasAdvanceAdjusted = false;

}
