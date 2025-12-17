package com.org.hosply360.service.OPD;

import com.org.hosply360.dto.OPDDTO.InvoiceResponseDTO;
import com.org.hosply360.dto.OPDDTO.OPDInvoiceDTO;
import com.org.hosply360.dto.OPDDTO.OPDInvoicePaymentUpdateDTO;
import com.org.hosply360.dto.OPDDTO.OPDInvoiceReqDTO;
import com.org.hosply360.dto.OPDDTO.ReceiptResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface OPDInvoiceService {
    String createInvoice(OPDInvoiceReqDTO requestDTO);

    OPDInvoiceDTO updateInvoice(OPDInvoiceReqDTO requestDTO);

    OPDInvoiceDTO getInvoiceById(String id, String orgId);

    List<OPDInvoiceDTO> getInvoicesByFilters(String orgId, String patientId, LocalDate fromDate, LocalDate toDate);

    void deleteInvoice(String id, String orgId);

    String updatePaidAmount(OPDInvoicePaymentUpdateDTO dto);

     ReceiptResponseDTO getReceiptData(String receiptId);

    List<OPDInvoiceDTO> getInvoicesWithAppointments(String orgId, LocalDate fromDate, LocalDate toDate);

    InvoiceResponseDTO generateInvoiceDetailsByIdentifier(String identifier );
}
