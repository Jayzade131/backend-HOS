package com.org.hosply360.service.IPD;

import com.org.hosply360.dto.IPDDTO.DischargeFormPdfResponseDTO;
import com.org.hosply360.dto.IPDDTO.IPDDischargeFormReqDTO;

public interface IPDDischargeFormService {
    String createDischargeFrom(IPDDischargeFormReqDTO ipdDischargeFormReqDTO);

    DischargeFormPdfResponseDTO getDischargeFormPdf(String dischargeFormId);

}
