package com.org.hosply360.service.IPD.impl;
import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dto.IPDDTO.IPDSurgeryBillCancelDTO;
import com.org.hosply360.dto.IPDDTO.IPDSurgeryReqDTO;
import com.org.hosply360.dao.IPD.IPDSurgery;
import com.org.hosply360.dao.IPD.ParticipantCharge;
import com.org.hosply360.dao.frontDeskDao.Doctor;
import com.org.hosply360.dto.IPDDTO.IPDSurgeryBillingReqDTO;
import com.org.hosply360.dto.IPDDTO.IPDSurgeryDTO;
import com.org.hosply360.dto.IPDDTO.ParticipantChargeDTO;
import com.org.hosply360.exception.IPDException;
import com.org.hosply360.exception.UserException;
import com.org.hosply360.repository.IPD.IPDSurgeryFormRepository;
import com.org.hosply360.service.IPD.IPDSurgeryBillingService;
import com.org.hosply360.service.IPD.IPDSurgeryService;
import com.org.hosply360.util.Others.EntityFetcherUtil;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IPDSurgeryServiceImpl implements IPDSurgeryService {

    private static final Logger logger = LoggerFactory.getLogger(IPDSurgeryServiceImpl.class);

    private final IPDSurgeryFormRepository ipdSurgeryFormRepository;
    private final EntityFetcherUtil entityFetcherUtil;
    private final IPDSurgeryBillingService ipdSurgeryBillingService;


    @Override
    @Transactional
    public String createIPDSurgeryForm(IPDSurgeryReqDTO requestDTO) {
        logger.info("Creating IPD Surgery Form for Admission ID: {}", requestDTO.getIpdAdmissionId());
        validateRequest(requestDTO);

        // Validate and fetch required entities
        entityFetcherUtil.getIPDAdmissionOrThrow(requestDTO.getIpdAdmissionId());
        Doctor consultant = entityFetcherUtil.getDoctorOrThrow(requestDTO.getConsultantId());
        Doctor secondConsultant = fetchOptionalDoctor(requestDTO.getSecondConsultantId());

        // Map request → entity
        IPDSurgery entity = mapToEntity(requestDTO);
        entity.setConsultant(consultant);
        entity.setSecondConsultant(secondConsultant);

        initializeSurgeryFlags(entity);
        IPDSurgery saved = ipdSurgeryFormRepository.save(entity);

        // Build billing DTO
        IPDSurgeryBillingReqDTO billingReq = buildBillingRequest(saved);
        ipdSurgeryBillingService.createSurgeryBilling(billingReq);

        logger.info("IPD Surgery Form created successfully. Surgery ID: {}", saved.getId());
        return saved.getId();
    }
    private Doctor fetchOptionalDoctor(String doctorId) {
        return StringUtils.hasText(doctorId)
                ? entityFetcherUtil.getDoctorOrThrow(doctorId)
                : null;
    }
    private void initializeSurgeryFlags(IPDSurgery entity) {
        entity.setDefunct(false);
        entity.setHasCancelled(false);
        entity.setCancelDateTime(null);
        entity.setCancelledReason(null);
        entity.setTotalSurgeryExpense(entity.getTotalSurgeryExpense());
    }
    private IPDSurgeryBillingReqDTO buildBillingRequest(IPDSurgery saved) {
        return IPDSurgeryBillingReqDTO.builder()
                .organizationId(saved.getOrgId())
                .ipdAdmissionId(saved.getIpdAdmissionId())
                .surgeryId(saved.getId())
                .surgeonDetails(mapToParticipantChargeDTOList(saved.getSurgeons()))
                .anaesthetistDetails(mapToParticipantChargeDTOList(saved.getAnaesthetists()))
                .pediatricsDetails(mapToParticipantChargeDTOList(saved.getPediatrics()))
                .otInstrumentationCharges(saved.getOtInstrumentationCharges())
                .surgeonsOtCharges(saved.getSurgeonsOtCharges())
                .surgeonsOtConsumableCharges(saved.getSurgeonsOtConsumableCharges())
                .anaesthetistsOtCharges(saved.getAnaesthetistsOtCharges())
                .anaesthetistsOtConsumableCharges(saved.getAnaesthetistsOtConsumableCharges())
                .pediatricsOtCharges(saved.getPediatricsOtCharges())
                .pediatricsOtConsumableCharges(saved.getPediatricsOtConsumableCharges())
                .surgeryCharges(saved.getSurgeryCharge())
                .totalSurgeryExpenses(saved.getTotalSurgeryExpense())
                .build();
    }


//    private BigDecimal safe(BigDecimal value) {
//        return value == null ? BigDecimal.ZERO : value;
//    }

    private List<ParticipantChargeDTO> mapToParticipantChargeDTOList (List<ParticipantCharge> participants) {
        return participants.stream()
                .map(p -> ParticipantChargeDTO.builder()
                        .doctorId(p.getId())
                        .doctorName(p.getName())
                        .charge(p.getCharge())
                        .build())
                .toList();
    }

    /**
     * Validate the incoming request DTO and ensure mandatory fields are present.
     *
     * @param dto the request DTO to validate
     * @throws UserException if validation fails; carries {@link HttpStatus#BAD_REQUEST}.
     */
    private void validateRequest(IPDSurgeryReqDTO dto) {
        if (dto == null) throw new UserException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST);

        if (!StringUtils.hasText(dto.getIpdAdmissionId()))
            throw new IPDException(ErrorConstant.IPD_ADMISSION_ID_NOT_FOUND, HttpStatus.BAD_REQUEST);

        if (!StringUtils.hasText(dto.getConsultantId()))
            throw new IPDException(ErrorConstant.CONSULTANT_ID_REQUIRED, HttpStatus.BAD_REQUEST);

        if (dto.getDate() == null)
            throw new IPDException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST);
    }

    /**
     * Map a request DTO to the persistence entity.
     *
     * <p>Performs deep mapping of participant lists (surgeons, anaesthetists, pediatrics).
     *
     * @param dto the source {@link IPDSurgeryReqDTO}
     * @return a new instance of {@link IPDSurgery} populated from the DTO
     */
    private IPDSurgery mapToEntity(IPDSurgeryReqDTO dto) {
        IPDSurgery entity = ObjectMapperUtil.copyObject(dto, IPDSurgery.class);
        entity.setSurgeons(mapParticipantList(dto.getSurgeons()));
        entity.setAnaesthetists(mapParticipantList(dto.getAnaesthetists()));
        entity.setPediatrics(mapParticipantList(dto.getPediatrics()));
        return entity;
    }

    /**
     * Convert a list of participant charge DTOs to persistence participant entities.
     *
     * @param dtoList list of {@link ParticipantChargeDTO}; may be {@code null} or empty
     * @return list of {@link ParticipantCharge} or an empty list if input is {@code null} or empty
     */
    private List<ParticipantCharge> mapParticipantList(List<ParticipantChargeDTO> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) return Collections.emptyList();

        return dtoList.stream()
                .map(dto -> ParticipantCharge.builder()
                        .id(dto.getDoctorId())
                        .name(dto.getDoctorName())
                        .charge(dto.getCharge())
                        .build())
                .toList();
    }

    /**
     * Fetch a single surgery form by its identifier.
     *
     * @param id identifier of the surgery form
     * @return the corresponding {@link IPDSurgeryDTO}
     * @throws IPDException if the entity is not found ({@link HttpStatus#NOT_FOUND})
     */
    @Override
    public IPDSurgeryDTO getSurgeryFormById(String id) {
        logger.info("Fetching IPD Surgery Form by ID: {}", id);

        IPDSurgery entity = ipdSurgeryFormRepository.findById(id)
                .orElseThrow(() -> new IPDException(ErrorConstant.ITEM_NOT_FOUND, HttpStatus.NOT_FOUND));
        return mapToDTO(entity);
    }

    /**
     * Fetch all surgery forms for a given organization and optionally for a specific IPD admission.
     *
     * @param orgId          the organization identifier; must be provided to scope the query
     * @param ipdAdmissionId optional IPD admission identifier; when provided, results are filtered to that admission
     * @return list of {@link IPDSurgeryDTO} matching the query; may be empty
     */
    @Override
    public List<IPDSurgeryDTO> getSurgeryFormsByIpdAdmission(String orgId, String ipdAdmissionId) {
        logger.info("Fetching IPD Surgery Forms for Org ID: {} and Admission ID: {}", orgId, ipdAdmissionId);
        List<IPDSurgery> entities;
        if (StringUtils.hasText(ipdAdmissionId)) {
            entities = ipdSurgeryFormRepository.findByOrgIdAndIpdAdmissionIdAndDefunctFalse(orgId, ipdAdmissionId);
        } else {
            entities = ipdSurgeryFormRepository.findByOrgIdAndDefunctFalse(orgId);
        }
        return entities.stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Map the persistence entity to its DTO representation.
     *
     * @param entity the source {@link IPDSurgery}
     * @return a populated {@link IPDSurgeryDTO}
     */
    private IPDSurgeryDTO mapToDTO(IPDSurgery entity) {
        IPDSurgeryDTO dto = ObjectMapperUtil.copyObject(entity, IPDSurgeryDTO.class);

        if (entity.getConsultant() != null) {
            dto.setConsultantId(entity.getConsultant().getId());
            dto.setConsultantName(entity.getConsultant().getShortName());
        }
        if (entity.getSecondConsultant() != null) {
            dto.setSecondConsultantId(entity.getSecondConsultant().getId());
            dto.setSecondConsultantName(entity.getSecondConsultant().getShortName());
        }
        dto.setSurgeons(mapParticipantChargeDTOList(entity.getSurgeons()));
        dto.setAnaesthetists(mapParticipantChargeDTOList(entity.getAnaesthetists()));
        dto.setPediatrics(mapParticipantChargeDTOList(entity.getPediatrics()));

        return dto;
    }

    /**
     * Convert a list of persistence participant charges to DTOs.
     *
     * @param list list of {@link ParticipantCharge}; may be {@code null} or empty
     * @return list of {@link ParticipantChargeDTO} or an empty list if input is {@code null} or empty
     */
    private List<ParticipantChargeDTO> mapParticipantChargeDTOList(List<ParticipantCharge> list) {
        if (list == null || list.isEmpty()) return Collections.emptyList();
        return list.stream()
                .map(p -> ParticipantChargeDTO.builder()
                        .doctorId(p.getId())
                        .doctorName(p.getName())
                        .charge(p.getCharge())
                        .build())
                .toList();
    }


    @Override
    @Transactional
    public String updateSurgeryForm(IPDSurgeryDTO requestDTO) {

        IPDSurgery existing = ipdSurgeryFormRepository.findById(requestDTO.getId())
                .orElseThrow(() -> new IPDException(ErrorConstant.ITEM_NOT_FOUND, HttpStatus.NOT_FOUND));

        updateSurgeryEntity(existing, requestDTO);

        IPDSurgery saved = ipdSurgeryFormRepository.save(existing);
        ipdSurgeryBillingService.createSurgeryBilling(buildBillingRequest(saved));

        logger.info("IPD Surgery Form updated successfully with ID: {}", saved.getId());
        return saved.getId();
    }
    private void updateSurgeryEntity(IPDSurgery existing, IPDSurgeryDTO requestDTO) {
        ObjectMapperUtil.safeCopyObject(requestDTO, existing);

        existing.setSurgeons(mapParticipantList(requestDTO.getSurgeons()));
        existing.setAnaesthetists(mapParticipantList(requestDTO.getAnaesthetists()));
        existing.setPediatrics(mapParticipantList(requestDTO.getPediatrics()));
        existing.setUpdatedDate(LocalDateTime.now());
        existing.setTotalSurgeryExpense(requestDTO.getTotalSurgeryExpense());

        resetCancellationFlags(existing);
    }
    private void resetCancellationFlags(IPDSurgery entity) {
        entity.setHasCancelled(false);
        entity.setCancelDateTime(null);
        entity.setCancelledReason(null);
    }


    /**
     * Soft-delete a surgery form by setting its {@code defunct} flag to {@code true}.
     *
     * <p>This is a non-destructive delete that preserves the record for audits/history.
     *
     * @param id identifier of the surgery form to soft-delete
     * @return the identifier of the soft-deleted entity
     * @throws IPDException if the entity is not found ({@link HttpStatus#NOT_FOUND})
     */
    @Override
    @Transactional
    public String deleteSurgeryForm(String id) {
        logger.info("Soft deleting IPD Surgery Form with ID: {}", id);

        IPDSurgery existing = ipdSurgeryFormRepository.findById(id)
                .orElseThrow(() -> new IPDException(ErrorConstant.ITEM_NOT_FOUND, HttpStatus.NOT_FOUND));
        existing.setDefunct(true);
        existing.setUpdatedDate(LocalDateTime.now());
        ipdSurgeryFormRepository.save(existing);
        logger.info("IPD Surgery Form soft deleted successfully. ID: {}", existing.getId());
        return existing.getId();
    }

    @Override
    public String cancelIPDSurgery(String surgeryId, String reason) {
        logger.info("Canceling Surgery Billing ID: {} with reason: {}", surgeryId, reason);
        IPDSurgery billing = ipdSurgeryFormRepository.findById(surgeryId)
                .orElseThrow(() -> new IPDException(ErrorConstant.SURGERY_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (Boolean.TRUE.equals(billing.getHasCancelled())) {
            throw new IPDException(ErrorConstant.BILLING_ALREADY_CANCELLED, HttpStatus.BAD_REQUEST);
        }
        billing.setHasCancelled(true);
        billing.setCancelledReason(reason);
        billing.setCancelDateTime(LocalDateTime.now());
        ipdSurgeryFormRepository.save(billing);
        IPDSurgeryBillCancelDTO ipdSurgeryBillCancelDTO =IPDSurgeryBillCancelDTO.builder()
                .organizationId(billing.getOrgId())
                .ipdAdmissionId(billing.getIpdAdmissionId())
                .surgeryId(surgeryId)
                .cancelReason(reason)
                .build();
        ipdSurgeryBillingService.cancelIPDSurgeryBilling(ipdSurgeryBillCancelDTO);
        logger.info("Surgery Billing ID: {} canceled successfully", surgeryId);
        return surgeryId;
    }
}
