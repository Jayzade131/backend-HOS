package com.org.hosply360.service.globalMaster;

import com.org.hosply360.dto.globalMasterDTO.IdentificationDocumentDTO;
import com.org.hosply360.dto.globalMasterDTO.IdentificationDocumentReqDTO;

import java.util.List;

public interface IdentificationDocumentService
{
    IdentificationDocumentDTO createIdentificationDocument(IdentificationDocumentReqDTO identificationDocumentDto);

    IdentificationDocumentDTO updateIdentificationDocument(String id,IdentificationDocumentReqDTO identificationDocumentDto);

    IdentificationDocumentDTO getIdentificationDocumentById(String id);

    List<IdentificationDocumentDTO> getAllIdentificationDocuments(String organizationId);

    void deleteIdentificationDocumentById(String id);




}
