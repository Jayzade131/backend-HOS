package com.org.hosply360.dto.OPDDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OPDInvoiceReqDTO {

    private String id;

    private String orgId;

    private String appointmentId;

    private String patientId;

    private String consultant;

    private LocalDateTime invoiceDate;

    private List<InvoiceItemsReqDto> invoiceItems;

    private Double paidAmount;

    private String remark;


}