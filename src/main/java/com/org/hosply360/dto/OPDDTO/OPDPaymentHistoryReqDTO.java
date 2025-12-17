package com.org.hosply360.dto.OPDDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OPDPaymentHistoryReqDTO {
    private String invoiceId;
    private String receiptId;
    private Double paymentAmount;
    private String paymentType;
    private String chequeNumber;
    private String bankName;
    private LocalDate chequeDate;
    private LocalDateTime paymentDate;
    private String recievedBy;
}
