package com.org.hosply360.service.IPD;

import com.org.hosply360.dto.IPDDTO.IPDSurgeryReqDTO;
import com.org.hosply360.dto.IPDDTO.IPDSurgeryDTO;

import java.util.List;

public interface IPDSurgeryService {

    String createIPDSurgeryForm(IPDSurgeryReqDTO requestDTO);
    IPDSurgeryDTO getSurgeryFormById(String id);
    List<IPDSurgeryDTO> getSurgeryFormsByIpdAdmission(String orgId, String ipdAdmissionId);
    String updateSurgeryForm(IPDSurgeryDTO requestDTO);
    String deleteSurgeryForm(String id);
    String cancelIPDSurgery(String surgeryId, String reason);
}
