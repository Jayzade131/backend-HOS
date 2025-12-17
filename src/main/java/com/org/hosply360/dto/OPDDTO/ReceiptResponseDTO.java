package com.org.hosply360.dto.OPDDTO;

import com.org.hosply360.dto.utils.PdfHeaderFooterDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptResponseDTO {

    private String receiptNumber;
    private LocalDateTime receiptDate;
    private String invoiceNumber;

    private String patientId;
    private String patientName;

    private Double amountReceived;
    private String paymentMode;
    private String chequeNumber;
    private String chequeDate;
    private String bankName;
    private String amountInWords;

    private PdfHeaderFooterDTO headerFooter;
}
