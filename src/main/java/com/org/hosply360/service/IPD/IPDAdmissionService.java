package com.org.hosply360.service.IPD;

import com.org.hosply360.dto.IPDDTO.BarcodeResDTO;
import com.org.hosply360.dto.IPDDTO.BedResponseDTO;
import com.org.hosply360.dto.IPDDTO.IPDAdmissionDTO;
import com.org.hosply360.dto.IPDDTO.IPDAdmissionReqDTO;
import com.org.hosply360.dto.IPDDTO.IPDAdmissionStatusReqDTO;
import com.org.hosply360.dto.IPDDTO.IPDPatientListDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface IPDAdmissionService {
    String createAdmission(IPDAdmissionReqDTO requestDTO);
    String updateAdmission(IPDAdmissionReqDTO requestDTO);
    BedResponseDTO getBedsByWardId(String orgId, String wardId);
    Page<IPDAdmissionDTO> getAdmissions(
            String orgId,
            String id,
            String wardId,
            String ipdStatus,
            LocalDate fromDate,
            LocalDate toDate,
            int page,
            int size
    );

    Page<IPDPatientListDTO> getPatientList(
            String orgId,
            String id,
            String wardId,
            String ipdStatus,
            LocalDate fromDate,
            LocalDate toDate,
            int page,
            int size
    );

    String cancelAdmission(IPDAdmissionStatusReqDTO requestDTO);

    BarcodeResDTO getIpdBarcode(String IpdId);

}
