package com.org.hosply360.service.OPD;

import com.org.hosply360.dto.OPDDTO.OPDPaymentHistoryDTO;
import com.org.hosply360.dto.OPDDTO.OPDPaymentHistoryReqDTO;

import java.util.List;

public interface OPDPaymentHistoryService {

    OPDPaymentHistoryDTO save(OPDPaymentHistoryReqDTO requestDto);

    List<OPDPaymentHistoryDTO> getByInvoiceId(String invoiceId);
}
