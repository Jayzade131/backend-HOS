package com.org.hosply360.service.IPD;

import com.org.hosply360.dto.IPDDTO.IPDReceiptDTO;
import com.org.hosply360.dto.IPDDTO.IPDReceiptReqDTO;
import com.org.hosply360.dto.OPDDTO.PdfResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface IPDReceiptService {

    String createReceipt(IPDReceiptReqDTO reqDTO);

    List<IPDReceiptDTO> getReceipts(
            String orgId,
            LocalDate fromDate,
            LocalDate toDate,
            String ipdAdmissionId,
            String ipdReceiptId,
            String receiptType);

    PdfResponseDTO generateReceiptPdf(String receiptId);
}
