package com.org.hosply360.service.IPD;

import com.org.hosply360.dto.IPDDTO.IPDDocumentRequestDTO;
import com.org.hosply360.dto.IPDDTO.IPDDocumentResDTO;
import com.org.hosply360.dto.IPDDTO.IPDDocumentResponseDTO;

import java.util.List;

public interface IPDDocumentService {

    String createIPDDocument(IPDDocumentRequestDTO requestDTO);

    String updateIPDDocument(IPDDocumentRequestDTO requestDTO);

    void deleteIPDDocument(String id);

    IPDDocumentResponseDTO getIPDDocumentById(String id);

    List<IPDDocumentResponseDTO> getByIpdAdmission(String ipdAdmissionId);

    IPDDocumentResDTO getDocumentPdfById(String docId);
}
