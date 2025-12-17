package com.org.hosply360.service.IPD.impl;

import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.Enums.DietTime;
import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.IPD.IPDAdmission;
import com.org.hosply360.dao.IPD.IPDDiet;
import com.org.hosply360.dao.frontDeskDao.Patient;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dto.IPDDTO.DietPlanPdfDTO;
import com.org.hosply360.dto.IPDDTO.IPDDeitDTO;
import com.org.hosply360.dto.IPDDTO.IPDDietReqDTO;
import com.org.hosply360.dto.OPDDTO.PdfResponseDTO;
import com.org.hosply360.exception.IPDException;
import com.org.hosply360.repository.IPD.IPDAdmissionRepository;
import com.org.hosply360.repository.IPD.IPDDietRepository;
import com.org.hosply360.service.IPD.IPDDietService;
import com.org.hosply360.util.Others.AgeUtil;
import com.org.hosply360.util.Others.EntityFetcherUtil;
import com.org.hosply360.util.PDFGenUtil.IPD.DietPlanPdfGenerator;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import com.org.hosply360.util.validator.ValidatorHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class IPDDietServiceImpl implements IPDDietService {


    // Repositories & Services
    private final EntityFetcherUtil entityFetcherUtil;
    private final IPDDietRepository ipdDietRepository;
    private final DietPlanPdfGenerator dietPlanPdfGenerator;
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

    private DietPlanPdfDTO mapToPdfDTO(IPDDiet diet, IPDAdmission admission, Patient patient) {
        return DietPlanPdfDTO.builder()
                .mrdNo(admission.getRegMrdNo())
                .ipdNo(admission.getIpdNo())
                .admDate(admission.getAdmitDateTime() != null ? admission.getAdmitDateTime().toString() : "")
                .consultant(admission.getPrimaryConsultant() != null ? admission.getPrimaryConsultant().getFirstName() : "")
                .referredBy(admission.getRefBy() != null ? admission.getRefBy().getFirstName() : "")
                .patientName(patient.getPatientPersonalInformation().getFirstName() + " " +
                        patient.getPatientPersonalInformation().getLastName())
                .ageGender(AgeUtil.getAge(patient.getPatientPersonalInformation().getDateOfBirth())
                        + " / " + patient.getPatientPersonalInformation().getGender())
                .mobileNo(patient.getPatientContactInformation().getPrimaryPhone())
                .address("Raipur, (C.G)")
                .remark(diet.getRemark())
                .date(diet.getDateTime() != null ? diet.getDateTime().toLocalDate().toString() : "")
                .dietTime(diet.getDietTime() != null ? diet.getDietTime().name() : "")
                .diet(diet.getDiet() != null ? diet.getDiet().getName() : "")
                .dietRemark(diet.getRemark())
                .build();
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
    public PdfResponseDTO generateDietPlanPdf(String ipdAdmissionId) {
        log.debug("Generating PDF for admission: {}", ipdAdmissionId);

        try {
            // Fetch admission with patient data in a single query
            IPDAdmission admission = ipdAdmissionRepository.findById(ipdAdmissionId)
                    .orElseThrow(() -> {
                        log.warn("Admission not found: {}", ipdAdmissionId);
                        return new IPDException(ErrorConstant.IPD_ADMISSION_NOT_FOUND, HttpStatus.NOT_FOUND);
                    });

            // Fetch and sort diets
            List<IPDDiet> diets = ipdDietRepository
                    .findByIpdAdmissionIdAndDefunct(ipdAdmissionId, false)
                    .stream()
                    .sorted(DIET_TIME_COMPARATOR)
                    .toList();

            if (diets.isEmpty()) {
                log.warn("No active diets found for admission: {}", ipdAdmissionId);
                throw new IPDException(ErrorConstant.IPD_DIET_NOT_FOUND, HttpStatus.NOT_FOUND);
            }

            // Generate PDF
            Patient patient = admission.getPatient();
            String patientName = Optional.ofNullable(patient)
                    .map(Patient::getPatientPersonalInformation)
                    .map(pi -> pi.getFirstName() + "_" + pi.getLastName())
                    .orElse("diet_plan");

            List<DietPlanPdfDTO> dtoList = diets.stream()
                    .map(diet -> mapToPdfDTO(diet, admission, patient))
                    .filter(Objects::nonNull)
                    .toList();

            byte[] pdf = dietPlanPdfGenerator.generateDietPlanPdf(dtoList);

            // Create a safe filename
            String fileName = String.format("%s%s_%s.pdf",
                    ApplicationConstant.PDF_FILENAME_PREFIX_DIET_PLAN,
                    patientName.replaceAll("[^a-zA-Z0-9.-]", "_"),
                    LocalDate.now());

            log.info("Generated diet plan PDF for admission: {}", ipdAdmissionId);
            return PdfResponseDTO.builder()
                    .body(pdf)
                    .fileName(fileName)
                    .build();

        } catch (IPDException e) {
            throw e; // Re-throw known exceptions
        } catch (Exception e) {
            log.error("Failed to generate diet plan PDF: {}", e.getMessage(), e);
            throw new IPDException(ErrorConstant.CANNOT_CANCEL_IPD, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}


