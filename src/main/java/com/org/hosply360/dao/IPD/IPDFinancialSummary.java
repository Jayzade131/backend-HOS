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

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "ipd_financial_summary")
public class IPDFinancialSummary extends BaseModel {
    @Id
    private String id;
    private String organizationId;
    @DBRef
    private IPDAdmission ipdAdmission;
    private LocalDateTime lastUpdated;
    private BigDecimal totalDepositAmount;

    private BigDecimal totalBilledAmount;
    private BigDecimal totalNetBillAmount;
    private BigDecimal totalBillPaidAmount;



    private BigDecimal totalSurgeryAmount;
    private BigDecimal totalSurgeryPaidAmount;

    private BigDecimal totalAmount; // total billied amount + totalsurgery amount
    private BigDecimal totalNetAmount; // total netbilledamount + total surgery amount
    private BigDecimal totalDiscountAmount; // total amount - totalNetamount
    private BigDecimal totalPaidAmount; // total billpaid amount + total surgery paid amount
    private BigDecimal pendingAmount; // total net amount - total paid amounnt
    private BigDecimal totalRefundedAmount; // refunded amount
    private String remarks;
}
