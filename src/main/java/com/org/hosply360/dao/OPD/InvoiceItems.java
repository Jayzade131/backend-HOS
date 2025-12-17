package com.org.hosply360.dao.OPD;

import com.org.hosply360.dao.globalMaster.BillingItem;
import com.org.hosply360.dao.globalMaster.BillingItemGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvoiceItems {

    @DBRef
    private BillingItemGroup billingItemGroup;

    @DBRef
    private BillingItem billingItem;

    @Field("quantity")
    private Long quantity;

    @Field("rate")
    private Double rate;

    @Field("discount_percent")
    private Double discountPercent;

    @Field("discount_amount")
    private Double discountAmount;

    @Field("amount")
    private Double amount;
}
