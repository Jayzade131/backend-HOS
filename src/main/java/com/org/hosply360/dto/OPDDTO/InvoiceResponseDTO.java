package com.org.hosply360.dto.OPDDTO;
import com.org.hosply360.controller.OPD.InvoiceItemsResponseDto;
import com.org.hosply360.dto.utils.PdfHeaderFooterDTO;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceResponseDTO {

    private String invoiceNumber;
    private LocalDate invoiceDate;

    // Patient Info
    private String patientId;
    private String patientName;
    private String age;
    private String gender;
    private String mobile;
    private String consultant;

    // Items
    private List<InvoiceItemsResponseDto> invoiceItems;

    // Summary
    private Double totalAmount;
    private Double discountAmount;
    private Double amountToPay;
    private String amountInWords;
    private String status;

    private PdfHeaderFooterDTO headerFooter;
}
