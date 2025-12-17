package com.org.hosply360.dto.OPDDTO;

import com.org.hosply360.constant.Enums.ReceiptStatus;
import com.org.hosply360.dto.frontDeskDTO.PatientInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OPDInvoiceDTO {
    private String id;
    private String orgId;
    private PatientInfoDTO patient;
    private AppointmentDTO appointment;
    private String consultant;
    private String invoiceNumber;
    private LocalDateTime invoiceDate;
    private List<InvoiceItemsDto> invoiceItems;
    private Double totalAmount;
    private Double discountAmount;
    private Double amountToPay;
    private Double paidAmount;
    private Double lastPaidAmount;
    private Double totalPaidAmount;
    private Double balanceAmount;
    private String status;
    private ReceiptStatus receiptGiven;
    private String remark;
    private boolean defunct;
}
