package com.org.hosply360.service.IPD;

import com.org.hosply360.dto.IPDDTO.IPDFinalBillDTO;
import com.org.hosply360.dto.IPDDTO.IPDFinalBillDiscountUpdateDTO;
import com.org.hosply360.dto.IPDDTO.IPDFinalBillFullResDTO;
import com.org.hosply360.dto.IPDDTO.IPDFinalBillReqDTO;
import com.org.hosply360.dto.IPDDTO.IPDFinalBillSummaryDTO;
import com.org.hosply360.dto.IPDDTO.IPDFinalBillingPaymentDTO;
import com.org.hosply360.dto.OPDDTO.PdfResponseDTO;

public interface IPDFinalBillService {
    String createFinalBill(IPDFinalBillReqDTO requestDto);

    IPDFinalBillFullResDTO getFinalBillByAdmissionId(String admissionId);

    String makeFinalBillPayment(IPDFinalBillingPaymentDTO paymentDto);

    PdfResponseDTO generateFinalBillSummaryPdf(String admissionId, String orgId);

    IPDFinalBillSummaryDTO getFinalBillSummary(String admissionId);

    IPDFinalBillDTO refreshFinalBill(String admissionId);

    PdfResponseDTO generateFinalBillPdf(String admissionId, String orgId);

    String updateFinalBillDiscount(IPDFinalBillDiscountUpdateDTO requestDto);
}

