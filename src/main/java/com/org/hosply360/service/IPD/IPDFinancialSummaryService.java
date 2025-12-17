package com.org.hosply360.service.IPD;

import com.org.hosply360.dao.IPD.IPDBilling;
import com.org.hosply360.dao.IPD.IPDSurgeryBilling;
import com.org.hosply360.dto.IPDDTO.IPDFinalBillReqDTO;
import com.org.hosply360.dto.IPDDTO.IPDFinancialSummaryDTO;
import com.org.hosply360.dto.IPDDTO.IPDFinancialSummaryReqDTO;

import java.util.List;

public interface IPDFinancialSummaryService {

    String createFinancialSummary(IPDFinancialSummaryReqDTO reqDTO);

    void updateFinancialSummary(IPDFinancialSummaryReqDTO reqDTO);

    List<IPDFinancialSummaryDTO> getFinancialSummary(String organizationId, String ipdAdmissionId, String id);

    String processRefund(IPDFinancialSummaryReqDTO reqDTO);

    void updateSummaryAfterBillingChange(IPDBilling billing);

    void updateSummaryAfterFinalBill(IPDFinalBillReqDTO requestDto);

    void updateSummaryAfterSurgeryBillingChange(IPDSurgeryBilling surgeryBilling);
}
