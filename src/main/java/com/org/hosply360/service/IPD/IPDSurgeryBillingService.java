package com.org.hosply360.service.IPD;

import com.org.hosply360.dto.IPDDTO.IPDBillingPaymentDTO;
import com.org.hosply360.dto.IPDDTO.IPDSurgeryBillCancelDTO;
import com.org.hosply360.dto.IPDDTO.IPDSurgeryBillingDTO;
import com.org.hosply360.dto.IPDDTO.IPDSurgeryBillingReqDTO;

import java.util.List;

public interface IPDSurgeryBillingService {
    String createSurgeryBilling(IPDSurgeryBillingReqDTO reqDTO);
    void updateSurgeryBilling(IPDSurgeryBillingReqDTO reqDTO);
    String surgeryBillPayment(IPDBillingPaymentDTO paymentDTO);
    List<IPDSurgeryBillingDTO> getAllIPDSurgeryBillings(String organizationId, String admissionId, String id);
    String cancelIPDSurgeryBilling(IPDSurgeryBillCancelDTO cancelDTO);

}
