package com.org.hosply360.service.IPD.impl;

import com.org.hosply360.constant.Enums.IpdStatus;
import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.IPD.IPDAdmission;
import com.org.hosply360.dao.IPD.IPDDocument;
import com.org.hosply360.dao.auth.Users;
import com.org.hosply360.dto.IPDDTO.IPDDocumentRequestDTO;
import com.org.hosply360.dto.IPDDTO.IPDDocumentResDTO;
import com.org.hosply360.dto.IPDDTO.IPDDocumentResponseDTO;
import com.org.hosply360.exception.IPDException;
import com.org.hosply360.repository.IPD.IPDDocumentRepository;
import com.org.hosply360.repository.authRepo.UsersRepository;
import com.org.hosply360.service.IPD.IPDDocumentService;
import com.org.hosply360.util.Others.EntityFetcherUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IPDDocumentServiceImpl implements IPDDocumentService {

    private static final Logger logger = LoggerFactory.getLogger(IPDDocumentServiceImpl.class);

    private final IPDDocumentRepository ipdDocumentRepository;
    private final EntityFetcherUtil entityFetcherUtil;
    private final UsersRepository userRepository;

    @Override
    @Transactional
    public String createIPDDocument(IPDDocumentRequestDTO requestDTO) {
        logger.info("Creating new IPD Document for IPD Admission ID={}", requestDTO.getIpdAdmissionId());
        validateRequest(requestDTO);

        IPDAdmission ipdAdmission = entityFetcherUtil.getIPDAdmissionOrThrow(requestDTO.getIpdAdmissionId());
        if (requestDTO.getDoc()==null)
        {
            throw new IPDException(ErrorConstant.DOCUMENT_REQUIRED, HttpStatus.BAD_REQUEST);
        }

        IPDDocument document = IPDDocument.builder()
                .head(requestDTO.getHead())
                .ipdAdmission(ipdAdmission)
                .doc(requestDTO.getDoc())
                .docName(requestDTO.getDocName())
                .remark(requestDTO.getRemark())
                .defunct(false)
                .build();

        IPDDocument saved = ipdDocumentRepository.save(document);
        logger.info("Successfully created IPD Document with ID={}", saved.getId());
        return saved.getId();
    }

    @Override
    @Transactional
    public String updateIPDDocument(IPDDocumentRequestDTO requestDTO) {

        IPDDocument existing = ipdDocumentRepository.findById(requestDTO.getId())
                .orElseThrow(() -> new IPDException(ErrorConstant.ID_DOCUMENT_NOT_FOUND, HttpStatus.NOT_FOUND));

        validateAndSet(requestDTO, existing);

        existing.setDefunct(false);

        IPDDocument updated = ipdDocumentRepository.save(existing);
        logger.info("Successfully updated IPD Document with ID={}", updated.getId());
        return updated.getId();
    }

    private void validateAndSet(IPDDocumentRequestDTO requestDTO, IPDDocument existing) {
        if (existing.getIpdAdmission().getIpdStatus() == IpdStatus.DISCHARGED) {
            throw new IPDException(ErrorConstant.CANNOT_UPDATE_DISCHARGE_IPD_DOCUMENT, HttpStatus.BAD_REQUEST);
        }

        if (Objects.nonNull(requestDTO.getHead())) {
            existing.setHead(requestDTO.getHead());
        }

        if (Objects.nonNull(requestDTO.getIpdAdmissionId())) {
            IPDAdmission ipdAdmission = entityFetcherUtil.getIPDAdmissionOrThrow(requestDTO.getIpdAdmissionId());
            existing.setIpdAdmission(ipdAdmission);
        }

        if (Objects.nonNull(requestDTO.getDoc())) {
            existing.setDoc(requestDTO.getDoc());
        }

        if (Objects.nonNull(requestDTO.getDocName())) {
            existing.setDocName(requestDTO.getDocName());
        }

        if (Objects.nonNull(requestDTO.getRemark())) {
            existing.setRemark(requestDTO.getRemark());
        }

    }

    @Override
    @Transactional
    public void deleteIPDDocument(String id) {
        logger.info("Deleting IPD Document with ID={}", id);
        IPDDocument existing = ipdDocumentRepository.findById(id)
                .orElseThrow(() -> new IPDException(ErrorConstant.ID_DOCUMENT_NOT_FOUND, HttpStatus.NOT_FOUND));
        existing.setDefunct(true);
        ipdDocumentRepository.save(existing);
        logger.info("Successfully deleted IPD Document with ID={}", id);
    }


    @Override
    public IPDDocumentResponseDTO getIPDDocumentById(String id) {
        logger.info("Fetching IPD Document by ID={}", id);

        IPDDocument entity = ipdDocumentRepository.findById(id)
                .orElseThrow(() -> new IPDException(ErrorConstant.ID_DOCUMENT_NOT_FOUND, HttpStatus.NOT_FOUND));

        return toResponse(entity, new HashMap<>());
    }

    @Override
    public List<IPDDocumentResponseDTO> getByIpdAdmission(String ipdAdmissionId) {
        logger.info("Fetching IPD Document by IpdAdmissionId={}", ipdAdmissionId);

        return ipdDocumentRepository.findByIpdAdmissionIdAndDefunct(ipdAdmissionId,false)
                .stream()
                .map(entity -> toResponse(entity, new HashMap<>()))
                .collect(Collectors.toList());
    }

    @Override
    public IPDDocumentResDTO getDocumentPdfById(String docId) {
        logger.info("Fetching IPD Document by docId={}", docId);
        IPDDocument entity = ipdDocumentRepository.findByIdAndDefunct(docId,false)
                .orElseThrow(() -> new IPDException(ErrorConstant.ID_DOCUMENT_NOT_FOUND, HttpStatus.NOT_FOUND));
        IPDDocumentResDTO dto = new IPDDocumentResDTO();
        dto.setDoc(entity.getDoc());
        dto.setDocName(entity.getDocName());
        return dto;
    }

    private IPDDocumentResponseDTO toResponse(IPDDocument entity, Map<String, String> userCache) {
        String userId = entity.getCreatedBy();
        String userName = userCache.computeIfAbsent(userId, this::fetchUserNameById);
        return IPDDocumentResponseDTO.builder()
                .id(entity.getId())
                .head(entity.getHead())
                .ipdAdmissionId(entity.getIpdAdmission() != null ? entity.getIpdAdmission().getId() : null)
                .docName(entity.getDocName())
                .remark(entity.getRemark())
                .defunct(entity.getDefunct())
                .uploadedBy(userName)
                .uploadedDate(entity.getCreatedDate())
                .build();
    }
    private String fetchUserNameById(String userId) {
        return userRepository.findById(userId)
                .map(Users::getName)
                .orElse("Unknown User");
    }

    private void validateRequest(IPDDocumentRequestDTO requestDTO) {
        if (ObjectUtils.isEmpty(requestDTO.getIpdAdmissionId())) {
            throw new IPDException(ErrorConstant.IPD_ADMISSION_REQUIRED, HttpStatus.BAD_REQUEST);
        }

        if (ObjectUtils.isEmpty(requestDTO.getHead())) {
            throw new IPDException(ErrorConstant.DOCUMENT_HEAD_REQUIRED, HttpStatus.BAD_REQUEST);
        }
    }
}
