package com.org.hosply360.service.globalMaster.impl;

import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dao.globalMaster.PatientCategory;
import com.org.hosply360.dao.globalMaster.Tariff;
import com.org.hosply360.dto.globalMasterDTO.OrganizationDTO;
import com.org.hosply360.dto.globalMasterDTO.PatientCategoryDTO;
import com.org.hosply360.dto.globalMasterDTO.PatientCategoryReqDTO;
import com.org.hosply360.dto.globalMasterDTO.TariffDTO;
import com.org.hosply360.exception.GlobalMasterException;
import com.org.hosply360.repository.globalMasterRepo.OrganizationMasterRepository;
import com.org.hosply360.repository.globalMasterRepo.PatientCategoryRepository;
import com.org.hosply360.repository.globalMasterRepo.TariffMasterRepository;
import com.org.hosply360.service.globalMaster.PatientCategoryService;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import com.org.hosply360.util.validator.ValidatorHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientCategoryServiceImpl implements PatientCategoryService {
    private static final Logger logger = LoggerFactory.getLogger(PatientCategoryServiceImpl.class);
    private final PatientCategoryRepository patientCategoryRepository;
    private final OrganizationMasterRepository organizationRepository;
    private final TariffMasterRepository tariffRepository;

    // create patient category
    @Override
    public PatientCategoryDTO createPatientCategory(PatientCategoryReqDTO reqDTO) {
        ValidatorHelper.validateObject(reqDTO); // validate request object
        Organization organization = organizationRepository.findByIdAndDefunct(reqDTO.getOrganizationId(), false) // validate organization
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        Tariff tariff = tariffRepository.findByIdAndDefunct(reqDTO.getTariffId(), false) // validate tariff
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.TEST_NOT_FOUND, HttpStatus.NOT_FOUND));
        if (patientCategoryRepository.findByCategoryNameAndDefunct(reqDTO.getCategoryName(), false).isPresent()) { // validate patient category
            throw new GlobalMasterException(ErrorConstant.PATIENT_CATEGORY_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }
        PatientCategory patientCategory = ObjectMapperUtil.copyObject(reqDTO, PatientCategory.class); // map request object to entity
        patientCategory.setOrganization(organization);
        patientCategory.setTariff(tariff);
        patientCategory.setDefunct(false);
        patientCategory.setPatientCategoryStatus(reqDTO.getPatientCategoryStatus());
        PatientCategory saved = patientCategoryRepository.save(patientCategory); // save the patient category
        logger.info("Patient Category created successfully");
        PatientCategoryDTO dto = ObjectMapperUtil.copyObject(saved, PatientCategoryDTO.class); // map entity to dto
        dto.setOrganizationDTO(ObjectMapperUtil.copyObject(saved.getOrganization(), OrganizationDTO.class));
        TariffDTO tariffDTO = ObjectMapperUtil.copyObject(saved.getTariff(), TariffDTO.class); // map tariff to dto
        tariffDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(tariff.getOrganization(), OrganizationDTO.class));
        dto.setTariffDTO(tariffDTO);
        dto.setPatientCategoryStatus(saved.getPatientCategoryStatus());
        return dto; // return the dto
    }

    // update patient category
    @Override
    public PatientCategoryDTO updatePatientCategory(String id, PatientCategoryReqDTO reqDTO) {
        ValidatorHelper.ValidateAllObject(id, reqDTO); // validate request object
        PatientCategory existing = patientCategoryRepository.findByIdAndDefunct(id, false) // validate patient category
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.PATIENT_CATEGORY_NOT_FOUND, HttpStatus.NOT_FOUND));
        Organization organization = organizationRepository.findByIdAndDefunct(reqDTO.getOrganizationId(), false) // validate organization
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        Tariff tariff = tariffRepository.findByIdAndDefunct(reqDTO.getTariffId(), false) // validate tariff
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.TARIFF_NOT_FOUND, HttpStatus.NOT_FOUND));
        ObjectMapperUtil.safeCopyObjectAndIgnore(reqDTO, existing, List.of("id", "defunct", "organizationId")); // update the patient category object
        existing.setOrganization(organization);
        existing.setTariff(tariff);
        existing.setDefunct(false);
        PatientCategory updated = patientCategoryRepository.save(existing); // save the updated patient category
        logger.info("Patient Category updated successfully");
        PatientCategoryDTO dto = ObjectMapperUtil.copyObject(updated, PatientCategoryDTO.class); // map entity to dto
        dto.setOrganizationDTO(ObjectMapperUtil.copyObject(updated.getOrganization(), OrganizationDTO.class));
        TariffDTO tariffDTO = ObjectMapperUtil.copyObject(updated.getTariff(), TariffDTO.class); // map tariff to dto
        tariffDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(tariff.getOrganization(), OrganizationDTO.class));
        dto.setTariffDTO(tariffDTO);
        dto.setPatientCategoryStatus(updated.getPatientCategoryStatus());
        return dto; // return the dto
    }

    // get patient category by id
    @Override
    public PatientCategoryDTO getPatientCategoryById(String id) {
        ValidatorHelper.validateObject(id); // validate the request object
        PatientCategory entity = patientCategoryRepository.findByIdAndDefunct(id, false) // validate patient category
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.PATIENT_CATEGORY_NOT_FOUND, HttpStatus.NOT_FOUND));
        logger.info("Patient Category fetched successfully");
        PatientCategoryDTO dto = ObjectMapperUtil.copyObject(entity, PatientCategoryDTO.class); // map entity to dto
        dto.setOrganizationDTO(ObjectMapperUtil.copyObject(entity.getOrganization(), OrganizationDTO.class));
        TariffDTO tariffDTO = ObjectMapperUtil.copyObject(entity.getTariff(), TariffDTO.class); // map tariff to dto
        tariffDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(entity.getTariff().getOrganization(), OrganizationDTO.class));
        dto.setTariffDTO(tariffDTO);
        dto.setPatientCategoryStatus(entity.getPatientCategoryStatus());
        return dto; // return the dto
    }

    // get all patient categories
    @Override
    public List<PatientCategoryDTO> getAllPatientCategories(String organizationId) {
        logger.info("Fetching all patient categories for organization ID: {}", organizationId);
        Organization organization = organizationRepository.findByIdAndDefunct(organizationId, false) // validate organization
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        List<PatientCategory> categories = patientCategoryRepository.findByOrganizationId(organization.getId(), false); // validate patient categories
        return categories.stream() // stream the patient categories
                .map(category -> { // map each patient category to dto
                    PatientCategoryDTO dto = ObjectMapperUtil.copyObject(category, PatientCategoryDTO.class); // map entity to dto
                    dto.setOrganizationDTO(ObjectMapperUtil.copyObject(category.getOrganization(), OrganizationDTO.class));
                    TariffDTO tariffDTO = ObjectMapperUtil.copyObject(category.getTariff(), TariffDTO.class); // map tariff to dto
                    tariffDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(category.getTariff().getOrganization(), OrganizationDTO.class));
                    dto.setTariffDTO(tariffDTO);
                    dto.setPatientCategoryStatus(category.getPatientCategoryStatus());
                    return dto; // return the dto
                })
                .toList(); // collect the patient categories to list
    }

    // delete patient category
    @Override
    public void deletePatientCategory(String id) {
        ValidatorHelper.ValidateAllObject(id); // validate the request object
        PatientCategory entity = patientCategoryRepository.findByIdAndDefunct(id, false) // validate patient category
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.PATIENT_CATEGORY_NOT_FOUND, HttpStatus.NOT_FOUND));
        entity.setDefunct(true); // soft delete the patient category
        patientCategoryRepository.save(entity); // save the patient category
        logger.info("Patient Category soft-deleted with ID: {}", id);
    }
}
