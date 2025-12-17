package com.org.hosply360.service.globalMaster.impl;


import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dao.globalMaster.Speciality;
import com.org.hosply360.dto.globalMasterDTO.OrganizationDTO;
import com.org.hosply360.dto.globalMasterDTO.SpecialityDTO;
import com.org.hosply360.dto.globalMasterDTO.SpecialityReqDTO;
import com.org.hosply360.exception.GlobalMasterException;
import com.org.hosply360.repository.globalMasterRepo.OrganizationMasterRepository;
import com.org.hosply360.repository.globalMasterRepo.SpecialityMasterRepository;
import com.org.hosply360.service.globalMaster.SpecialityMasterService;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import com.org.hosply360.util.validator.ValidatorHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class SpecialityMasterServiceImpl implements SpecialityMasterService {

    private static final Logger logger = LoggerFactory.getLogger(SpecialityMasterServiceImpl.class);
    private final SpecialityMasterRepository specialityMasterRepository;
    private final OrganizationMasterRepository organizationMasterRepository;

    // create speciality
    @Override
    @Transactional
    public SpecialityDTO createSpeciality(SpecialityReqDTO dto) {
        ValidatorHelper.validateObject(dto);
        Organization organization = organizationMasterRepository.findByIdAndDefunct(dto.getOrganization(), false) // validate organization
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        Speciality speciality = ObjectMapperUtil.copyObject(dto, Speciality.class); // map dto to entity
        speciality.setOrganization(organization);
        speciality.setDefunct(false);
        if (Objects.equals(dto.getMasterType(), ApplicationConstant.SPECIALITY_MASTER_TYPE)) { // validate master type
            speciality.setMasterType(ApplicationConstant.SPECIALITY_MASTER_TYPE);
        } else if (Objects.equals(dto.getMasterType(), ApplicationConstant.DEPARTMENT_MASTER_TYPE)) { // validate master type
            speciality.setMasterType(ApplicationConstant.DEPARTMENT_MASTER_TYPE);
        } else {
            throw new GlobalMasterException(ErrorConstant.INVALID_MASTER_TYPE, HttpStatus.BAD_REQUEST);
        }
        Speciality saved = specialityMasterRepository.save(speciality); // save entity
        logger.info("Speciality created successfully with ID {}", saved.getId());
        SpecialityDTO specialityDTO = ObjectMapperUtil.copyObject(saved, SpecialityDTO.class); // map entity to dto
        specialityDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(saved.getOrganization(), OrganizationDTO.class));
        return specialityDTO; // return dto
    }

    // get all speciality
    @Override
    public List<SpecialityDTO> getAllSpeciality(String organizationId, String masterType) {
        logger.info("Fetching all occupation for organization ID: {}", organizationId);
        Organization organization = organizationMasterRepository.findByIdAndDefunct(organizationId, false) // find Organization by id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        List<Speciality> specialities = specialityMasterRepository.findAllByDefunctAndMasterType(organization.getId(), false, masterType); // find all specialities by organization id and defunct
        return specialities.stream() // stream specialities
                .map(speciality -> {  // map speciality to dto
                    SpecialityDTO dto = ObjectMapperUtil.copyObject(speciality, SpecialityDTO.class); // map entity to dto
                    dto.setOrganizationDTO(ObjectMapperUtil.copyObject(speciality.getOrganization(), OrganizationDTO.class));
                    return dto; // return dto
                })
                .toList(); // return list of dto
    }

    // get speciality by id
    @Override
    public SpecialityDTO getSpecialityById(String id) {
        Speciality speciality = specialityMasterRepository.findByIdAndDefunct(id, false) // find speciality by id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.SPECIALITY_NOT_FOUND, HttpStatus.NOT_FOUND));
        SpecialityDTO specialityDTO = ObjectMapperUtil.copyObject(speciality, SpecialityDTO.class); // map entity to dto
        specialityDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(speciality.getOrganization(), OrganizationDTO.class));
        return specialityDTO; // return dto
    }

    // update speciality
    @Override
    @Transactional
    public SpecialityDTO updateSpeciality(String id, SpecialityReqDTO specialityDto) {
        ValidatorHelper.ValidateAllObject(id, specialityDto); // validate the request object
        Organization organization = organizationMasterRepository.findByIdAndDefunct(specialityDto.getOrganization(), false) // find organization by id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        Speciality existing = specialityMasterRepository.findByIdAndDefunct(specialityDto.getId(), false) // find speciality by id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.SPECIALITY_NOT_FOUND, HttpStatus.NOT_FOUND));
        Speciality speciality = ObjectMapperUtil.copyObject(specialityDto, Speciality.class); // map dto to entity
        speciality.setOrganization(organization);
        speciality.setDefunct(false);
        ObjectMapperUtil.safeCopyObjectAndIgnore(specialityDto, existing, List.of("id", "defunct", "organizationId")); // update the speciality object
        Speciality updated = specialityMasterRepository.save(existing); // save the updated speciality
        logger.info("Speciality with ID {} updated successfully", updated.getId());
        SpecialityDTO specialityDTO = ObjectMapperUtil.copyObject(updated, SpecialityDTO.class); // map entity to dto
        specialityDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(updated.getOrganization(), OrganizationDTO.class));
        return specialityDTO; // return dto
    }

    // delete speciality
    @Override
    public void deleteSpeciality(String id) {
        ValidatorHelper.ValidateAllObject(id); // validate the request object
        Speciality speciality = specialityMasterRepository.findByIdAndDefunct(id, false) // find speciality by id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.SPECIALITY_NOT_FOUND, HttpStatus.NOT_FOUND));
        logger.info("Deleting Speciality with ID: {}", id);
        speciality.setDefunct(true); // set defunct to true
        specialityMasterRepository.save(speciality); // save the updated speciality
        logger.info("deleted Speciality with ID: {}", id);
    }
}
