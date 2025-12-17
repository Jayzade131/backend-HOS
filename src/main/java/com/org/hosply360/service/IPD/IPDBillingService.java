package com.org.hosply360.service.IPD;

import com.org.hosply360.dto.IPDDTO.CancelBillingItemsRequestDTO;
import com.org.hosply360.dto.IPDDTO.IPDBillingDTO;
import com.org.hosply360.dto.IPDDTO.IPDBillingPaymentDTO;
import com.org.hosply360.dto.IPDDTO.IPDBillingReqDTO;
import com.org.hosply360.dto.OPDDTO.PdfResponseDTO;

import java.util.List;

public interface IPDBillingService {
    String createIPDBilling(IPDBillingReqDTO billingDTO);

    List<IPDBillingDTO> getAllIPDBillings(String organizationId, String ipdAdmissionId, String id);

    String updateIPDBilling(String id, IPDBillingDTO billingDTO);

    String billPayment(String billingId, IPDBillingPaymentDTO paymentDTO);

    String cancelIPDBilling(String billingId, String reason);

    String cancelMultipleIPDBillingItems(String billingId, CancelBillingItemsRequestDTO request);

    PdfResponseDTO generateIPDBillingPdf(String billingId, String orgId);


}
