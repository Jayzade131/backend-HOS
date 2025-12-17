package com.org.hosply360.service.globalMaster.impl;

import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.globalMaster.IdentificationDocument;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dto.globalMasterDTO.IdentificationDocumentDTO;
import com.org.hosply360.dto.globalMasterDTO.IdentificationDocumentReqDTO;
import com.org.hosply360.dto.globalMasterDTO.OrganizationDTO;
import com.org.hosply360.exception.GlobalMasterException;
import com.org.hosply360.repository.globalMasterRepo.IdentificationDocumentRepository;
import com.org.hosply360.repository.globalMasterRepo.OrganizationMasterRepository;
import com.org.hosply360.service.globalMaster.IdentificationDocumentService;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import com.org.hosply360.util.validator.ValidatorHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IdentificationDocumentServiceImpl implements IdentificationDocumentService {

    private static final Logger logger = LoggerFactory.getLogger(IdentificationDocumentServiceImpl.class);
    private final IdentificationDocumentRepository identificationDocumentRepository;
    private final OrganizationMasterRepository organizationMasterRepository;

    // create identification document
    @Override
    @Transactional
    public IdentificationDocumentDTO createIdentificationDocument(IdentificationDocumentReqDTO identificationDocumentDto) {
        ValidatorHelper.validateObject(identificationDocumentDto); // validate the identification document dto
        Organization organization = organizationMasterRepository.findByIdAndDefunct(identificationDocumentDto.getOrganization(), false) // validate the organization id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        IdentificationDocument document = ObjectMapperUtil.copyObject(identificationDocumentDto, IdentificationDocument.class); // convert the identification document dto to object
        document.setOrganization(organization);
        document.setDefunct(false);
        IdentificationDocument savedDocument = identificationDocumentRepository.save(document); // save the identification document object
        logger.info("PatientIdentification Document created successfully");
        IdentificationDocumentDTO documentDTO = ObjectMapperUtil.copyObject(savedDocument, IdentificationDocumentDTO.class); // convert the identification document object to dto
        documentDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(savedDocument.getOrganization(), OrganizationDTO.class));
        return documentDTO; // return the dto
    }

    // update identification document
    @Override
    @Transactional
    public IdentificationDocumentDTO updateIdentificationDocument(String id, IdentificationDocumentReqDTO identificationDocumentDto) {
        ValidatorHelper.validateObject(identificationDocumentDto); // validate the identification document dto
        Organization organization = organizationMasterRepository.findByIdAndDefunct(identificationDocumentDto.getOrganization(), false) // validate the organization id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        IdentificationDocument existingDocument = identificationDocumentRepository.findByIdandDefunct(identificationDocumentDto.getId(), false) // validate the identification document id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ID_DOCUMENT_NOT_FOUND, HttpStatus.NOT_FOUND));
        IdentificationDocument document = ObjectMapperUtil.copyObject(identificationDocumentDto, IdentificationDocument.class); // convert the identification document dto to object
        document.setOrganization(organization);
        document.setDefunct(false);
        ObjectMapperUtil.safeCopyObjectAndIgnore(identificationDocumentDto, existingDocument, List.of("id", "defunct", "organizationId")); // update the identification document object
        IdentificationDocument savedDocument = identificationDocumentRepository.save(document); // save the identification document object
        logger.info("PatientIdentification Document updated successfully");
        IdentificationDocumentDTO documentDTO = ObjectMapperUtil.copyObject(savedDocument, IdentificationDocumentDTO.class); // convert the identification document object to dto
        documentDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(savedDocument.getOrganization(), OrganizationDTO.class));
        return documentDTO; // return the dto
    }

    // get identification document by id
    @Override
    public IdentificationDocumentDTO getIdentificationDocumentById(String id) {
        IdentificationDocument document = identificationDocumentRepository.findByIdandDefunct(id, false) // validate the identification document id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ID_DOCUMENT_NOT_FOUND, HttpStatus.NOT_FOUND));
        IdentificationDocumentDTO documentDTO = ObjectMapperUtil.copyObject(document, IdentificationDocumentDTO.class); // convert the identification document object to dto
        documentDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(document.getOrganization(), OrganizationDTO.class));
        return documentDTO; // return the dto
    }

    // get all identification documents
    @Override
    public List<IdentificationDocumentDTO> getAllIdentificationDocuments(String organizationId) {
        logger.info("Fetching all documents for organization ID: {}", organizationId);
        Organization organization = organizationMasterRepository.findByIdAndDefunct(organizationId, false) // validate the organization id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        List<IdentificationDocument> documents = identificationDocumentRepository.findAllByDefunct(organization.getId(), false); // get all identification documents
        return documents.stream() // convert the identification document object to dto
                .map(document -> { // map each identification document object to dto
                    IdentificationDocumentDTO dto = ObjectMapperUtil.copyObject(document, IdentificationDocumentDTO.class); // convert the identification document object to dto
                    dto.setOrganizationDTO(ObjectMapperUtil.copyObject(document.getOrganization(), OrganizationDTO.class));
                    return dto; // return the dto
                })
                .toList(); // collect the dtos
    }

    // delete identification document by id
    @Override
    public void deleteIdentificationDocumentById(String id) {
        ValidatorHelper.ValidateAllObject(id); // validate the request object
        identificationDocumentRepository.findByIdandDefunct(id, false) // validate the identification document id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ID_DOCUMENT_NOT_FOUND, HttpStatus.NOT_FOUND));
        identificationDocumentRepository.deleteById(id); // delete the identification document
        logger.info("deleted document with ID: {}", id);
    }
}
