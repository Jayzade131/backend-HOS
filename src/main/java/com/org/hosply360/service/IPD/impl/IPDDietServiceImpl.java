package com.org.hosply360.service.IPD.impl;

import com.org.hosply360.constant.Enums.DietTime;
import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.IPD.IPDAdmission;
import com.org.hosply360.dao.IPD.IPDDiet;
import com.org.hosply360.dao.frontDeskDao.Patient;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dto.IPDDTO.DietInfoDTO;
import com.org.hosply360.dto.IPDDTO.DietPlanPdfResponseDTO;
import com.org.hosply360.dto.IPDDTO.IPDDeitDTO;
import com.org.hosply360.dto.IPDDTO.IPDDietReqDTO;
import com.org.hosply360.dto.utils.PdfHeaderFooterDTO;
import com.org.hosply360.exception.IPDException;
import com.org.hosply360.repository.IPD.IPDAdmissionRepository;
import com.org.hosply360.repository.IPD.IPDDietRepository;
import com.org.hosply360.service.IPD.IPDDietService;
import com.org.hosply360.util.Others.AgeUtil;
import com.org.hosply360.util.Others.EntityFetcherUtil;
import com.org.hosply360.util.Others.TimeUtil;
import com.org.hosply360.util.mapper.HeaderFooterMapperUtil;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import com.org.hosply360.util.validator.ValidatorHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class IPDDietServiceImpl implements IPDDietService {


    // Repositories & Services
    private final EntityFetcherUtil entityFetcherUtil;
    private final IPDDietRepository ipdDietRepository;
    private final IPDAdmissionRepository ipdAdmissionRepository;

    // Comparator for sorting diets by meal time
    private static final Comparator<IPDDiet> DIET_TIME_COMPARATOR =
            Comparator.comparing(diet -> getMealOrder(diet.getDietTime()));

    // Helper method to get meal order for sorting
    private static int getMealOrder(DietTime time) {
        if (time == null) return Integer.MAX_VALUE;
        return switch (time) {
            case BREAKFAST -> 1;
            case LUNCH -> 2;
            case DINNER -> 3;
        };
    }

    private IPDDeitDTO mapToDTO(IPDDiet entity) {
        IPDDeitDTO dto = ObjectMapperUtil.copyObject(entity, IPDDeitDTO.class);
        dto.setOrganization(entity.getOrganizationId().getId());
        dto.setIpdAdmission(entity.getIpdAdmissionId().getId());
        dto.setDateTime(entity.getDateTime());
        return dto;
    }


    // ------------------------ PUBLIC METHODS ------------------------

    @Override
    @Transactional
    public String createDiet(IPDDietReqDTO dto) {
        log.debug("Creating new diet plan for admission: {}", dto.getIpdAdmissionId());
        ValidatorHelper.ValidateAllObject(dto);

        try {
            Organization org = entityFetcherUtil.getOrganizationOrThrow(dto.getOrganizationId());
            IPDAdmission admission = entityFetcherUtil.getIPDAdmissionOrThrow(dto.getIpdAdmissionId());

            IPDDiet diet = IPDDiet.builder()
                    .organizationId(org)
                    .ipdAdmissionId(admission)
                    .diet(dto.getDiet())
                    .dateTime(LocalDateTime.now())
                    .dietTime(dto.getDietTime())
                    .remark(dto.getRemark())
                    .time(dto.getTime())
                    .defunct(false)
                    .build();

            String dietId = ipdDietRepository.save(diet).getId();
            log.info("Created diet plan with ID: {} for admission: {}", dietId, dto.getIpdAdmissionId());
            return dietId;
        } catch (Exception e) {
            log.error("Failed to create diet plan: {}", e.getMessage(), e);
            throw new IPDException(ErrorConstant.DIET_CREATION_FAILED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public String updateDiet(IPDDietReqDTO dto) {
        log.debug("Updating diet plan: {}", dto.getId());
        ValidatorHelper.ValidateAllObject(dto.getId(), dto);

        try {
            IPDDiet diet = entityFetcherUtil.getIPDDietOrThrow(dto.getId());

            diet.setDiet(dto.getDiet());
            diet.setDateTime(LocalDateTime.now());
            diet.setDietTime(dto.getDietTime());
            diet.setRemark(dto.getRemark());
            diet.setTime(dto.getTime());
            diet.setDefunct(false);

            String updatedId = ipdDietRepository.save(diet).getId();
            log.info("Updated diet plan: {}", updatedId);
            return updatedId;
        } catch (Exception e) {
            log.error("Failed to update diet plan {}: {}", dto.getId(), e.getMessage(), e);
            throw new IPDException(ErrorConstant.DIET_UPDATE_FAILED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public IPDDeitDTO getDietById(String id) {
        log.debug("Fetching diet plan by ID: {}", id);
        ValidatorHelper.ValidateAllObject(id);

        try {
            IPDDiet diet = entityFetcherUtil.getIPDDietOrThrow(id);
            return mapToDTO(diet);
        } catch (Exception e) {
            log.error("Failed to fetch diet plan {}: {}", id, e.getMessage(), e);
            throw new IPDException(ErrorConstant.DIET_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public void deleteDiet(String id) {
        log.debug("Deleting diet plan: {}", id);
        ValidatorHelper.ValidateAllObject(id);

        try {
            IPDDiet diet = entityFetcherUtil.getIPDDietOrThrow(id);
            diet.setDefunct(true);
            ipdDietRepository.save(diet);
            log.info("Marked diet plan as deleted: {}", id);
        } catch (Exception e) {
            log.error("Failed to delete diet plan {}: {}", id, e.getMessage(), e);
            throw new IPDException(ErrorConstant.DIET_DELETION_FAILED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<IPDDeitDTO> getAllDiet(String ipdAdmissionId) {
        log.debug("Fetching all active diets for admission: {}", ipdAdmissionId);
        ValidatorHelper.ValidateAllObject(ipdAdmissionId);

        IPDAdmission admission = entityFetcherUtil.getIPDAdmissionOrThrow(ipdAdmissionId);

        return ipdDietRepository
                .findByIpdAdmissionIdAndDefunct(admission.getId(), false)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DietPlanPdfResponseDTO getDietPlanPdf(String ipdAdmissionId) {
        IPDAdmission admission = ipdAdmissionRepository.findById(ipdAdmissionId)
                .orElseThrow(() ->
                        new IPDException(ErrorConstant.IPD_ADMISSION_NOT_FOUND, HttpStatus.NOT_FOUND));

        Patient patient = admission.getPatient();
        List<IPDDiet> diets = ipdDietRepository
                .findByIpdAdmissionIdAndDefunct(ipdAdmissionId, false)
                .stream()
                .sorted(DIET_TIME_COMPARATOR)
                .toList();

        if (diets.isEmpty()) {
            throw new IPDException(ErrorConstant.IPD_DIET_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        Organization organization = admission.getOrgId();
        PdfHeaderFooterDTO headerFooter = HeaderFooterMapperUtil.buildHeaderFooter(organization);

        var personal = patient.getPatientPersonalInformation();
        String ageGender = AgeUtil.getAge(personal) + " / " + personal.getGender();

        List<IPDDiet> diet = ipdDietRepository
                .findByIpdAdmissionIdAndDefunct(ipdAdmissionId, false)
                .stream()
                .sorted(DIET_TIME_COMPARATOR)
                .toList();

        List<DietInfoDTO> diet1 = diet.stream()
                .map(ipdDiet -> {
                    DietInfoDTO dto = new DietInfoDTO();
                    dto.setDiet(ipdDiet.getDiet());
                    dto.setDateTime(ipdDiet.getDateTime());
                    dto.setDietTime(ipdDiet.getDietTime().toString());
                    dto.setTime(ipdDiet.getTime());
                    dto.setRemark(ipdDiet.getRemark());
                    return dto;
                })
                .toList();

        return DietPlanPdfResponseDTO.builder()
                .headerFooter(headerFooter)
                .ipdNo(admission.getIpdNo())
                .admDate(TimeUtil.formatDate(admission.getAdmitDateTime()))
                .consultant(admission.getPrimaryConsultant().getFirstName())
                .patientName(personal.getFirstName() + " " + personal.getLastName())
                .ageGender(ageGender)
                .mobileNo(patient.getPatientContactInformation().getPrimaryPhone())
                .dietRemark(diet.getFirst().getRemark())
                .dites(diet1)
                .build();

    }
}


