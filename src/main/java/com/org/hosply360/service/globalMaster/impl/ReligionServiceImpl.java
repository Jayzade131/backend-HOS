package com.org.hosply360.service.globalMaster.impl;

import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dao.globalMaster.Religion;
import com.org.hosply360.dto.globalMasterDTO.OrganizationDTO;
import com.org.hosply360.dto.globalMasterDTO.ReligionDTO;
import com.org.hosply360.dto.globalMasterDTO.ReligionReqDTO;
import com.org.hosply360.exception.GlobalMasterException;
import com.org.hosply360.repository.globalMasterRepo.OrganizationMasterRepository;
import com.org.hosply360.repository.globalMasterRepo.ReligionRepository;
import com.org.hosply360.service.globalMaster.ReligionService;
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
public class ReligionServiceImpl implements ReligionService {

    private final ReligionRepository religionRepository;
    private final OrganizationMasterRepository organizationMasterRepository;
    private static final Logger logger = LoggerFactory.getLogger(ReligionServiceImpl.class);

    // create religion
    @Override
    @Transactional
    public ReligionDTO createReligion(ReligionReqDTO religionDto) {
        ValidatorHelper.validateObject(religionDto); // validate the request object
        Organization organization = organizationMasterRepository.findByIdAndDefunct(religionDto.getOrganization(), false) // validate organization
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        Religion religion = ObjectMapperUtil.copyObject(religionDto, Religion.class); // map dto to entity
        religion.setOrganization(organization);
        religion.setDefunct(false);
        Religion religion1 = religionRepository.save(religion); // save the religion
        logger.info("Religion created successfully");
        ReligionDTO religionDTO = ObjectMapperUtil.copyObject(religion1, ReligionDTO.class); // map entity to dto
        religionDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(religion1.getOrganization(), OrganizationDTO.class));
        return religionDTO; // return the dto
    }

    // update religion
    @Override
    @Transactional
    public ReligionDTO updateReligion(String id, ReligionReqDTO religionDto) {
        ValidatorHelper.ValidateAllObject(id, religionDto); // validate the request object
        Organization organization = organizationMasterRepository.findByIdAndDefunct(religionDto.getOrganization(), false) // validate organization
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        Religion existingReligion = religionRepository.findByIdAndDefunct(religionDto.getId(), false) // validate religion
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.RELIGION_NOT_FOUND, HttpStatus.NOT_FOUND));
        Religion religion = ObjectMapperUtil.copyObject(religionDto, Religion.class); // map dto to entity
        religion.setOrganization(organization);
        religion.setDefunct(false);
        ObjectMapperUtil.safeCopyObjectAndIgnore(religionDto, existingReligion, List.of("id", "defunct", "organizationId")); // update the religion
        Religion save = religionRepository.save(religion); // save the religion
        logger.info("Religion updated successfully");
        ReligionDTO religionDTO = ObjectMapperUtil.copyObject(save, ReligionDTO.class); // map entity to dto
        religionDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(save.getOrganization(), OrganizationDTO.class));
        return religionDTO; // return the dto
    }

    // get religion by id
    @Override
    public ReligionDTO getReligionById(String id) {
        Religion religion = religionRepository.findByIdAndDefunct(id, false) // validate religion
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.RELIGION_NOT_FOUND, HttpStatus.NOT_FOUND));
        ReligionDTO religionDTO = ObjectMapperUtil.copyObject(religion, ReligionDTO.class); // map entity to dto
        religionDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(religion.getOrganization(), OrganizationDTO.class));
        return religionDTO; // return the dto
    }

    // get all religions
    @Override
    public List<ReligionDTO> getAllReligions(String organizationId) {
        logger.info("Fetching all occupation for organization ID: {}", organizationId);
        Organization organization = organizationMasterRepository.findByIdAndDefunct(organizationId, false) // validate organization
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        List<Religion> religions = religionRepository.findAllByDefunct(organization.getId(), false); // validate religions
        return religions.stream() // stream the religions
                .map(religion -> { // map the religions to religion dtos
                    ReligionDTO dto = ObjectMapperUtil.copyObject(religion, ReligionDTO.class); // map entity to dto
                    dto.setOrganizationDTO(ObjectMapperUtil.copyObject(religion.getOrganization(), OrganizationDTO.class));
                    return dto; // return the dto
                })
                .toList(); // return the list of dtos
    }

    // delete religion by id
    @Override
    public void deleteReligionById(String id) {
        ValidatorHelper.ValidateAllObject(id); // validate the request object
        Religion religion = religionRepository.findByIdAndDefunct(id, false) // validate religion
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.RELIGION_NOT_FOUND, HttpStatus.NOT_FOUND));
        logger.info("Deleting religion with ID: {}", id);
        religion.setDefunct(true); // soft delete the religion
        religionRepository.save(religion); // save the religion object
        logger.info("deleted religion with ID: {}", id);
    }
}


