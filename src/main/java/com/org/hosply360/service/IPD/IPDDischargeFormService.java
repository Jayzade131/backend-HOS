package com.org.hosply360.service.IPD;

import com.org.hosply360.dto.IPDDTO.IPDDischargeFormReqDTO;
import com.org.hosply360.dto.OPDDTO.PdfResponseDTO;

public interface IPDDischargeFormService {
    String createDischargeFrom(IPDDischargeFormReqDTO ipdDischargeFormReqDTO);
    PdfResponseDTO generateDischargeFormPdf(String dischargeFormId);
}
