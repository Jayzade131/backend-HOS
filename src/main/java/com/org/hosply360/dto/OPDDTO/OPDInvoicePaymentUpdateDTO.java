package com.org.hosply360.dto.OPDDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OPDInvoicePaymentUpdateDTO {
    private String invoiceId;
    private String orgId;
    private String paymentType;
    private Double newAmount;
    private String chequeNumber;
    private String bankName;
    private LocalDate chequeDate;
}
