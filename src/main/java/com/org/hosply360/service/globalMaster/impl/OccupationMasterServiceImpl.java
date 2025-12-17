package com.org.hosply360.service.globalMaster.impl;


import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.globalMaster.Occupation;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dto.globalMasterDTO.OccupationDTO;
import com.org.hosply360.dto.globalMasterDTO.OccupationReqDTO;
import com.org.hosply360.dto.globalMasterDTO.OrganizationDTO;
import com.org.hosply360.exception.GlobalMasterException;
import com.org.hosply360.repository.globalMasterRepo.OccupationMasterRepository;
import com.org.hosply360.repository.globalMasterRepo.OrganizationMasterRepository;
import com.org.hosply360.service.globalMaster.OccupationMasterService;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import com.org.hosply360.util.validator.ValidatorHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OccupationMasterServiceImpl implements OccupationMasterService {

    private static final Logger logger = LoggerFactory.getLogger(OccupationMasterServiceImpl.class);
    private final OccupationMasterRepository occupationMasterRepository;
    private final OrganizationMasterRepository organizationMasterRepository;

    // create occupation
    @Override
    @Transactional
    public OccupationDTO createOccupation(OccupationReqDTO dto) {
        ValidatorHelper.validateObject(dto); // validate the dto
        Organization organization = organizationMasterRepository.findByIdAndDefunct(dto.getOrganization(), false) // find the organization by id and defunct
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        if (occupationMasterRepository.findByOccupationCodeAndDefunct(dto.getOccupationCode(), false).isPresent()) { // check if the occupation code already exists
            logger.info("Occupation code {} already exists", dto.getOccupationCode());
            throw new GlobalMasterException(ErrorConstant.OCCUPATION_CODE_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        }
        Occupation occupation = ObjectMapperUtil.copyObject(dto, Occupation.class); // convert the dto to occupation object
        occupation.setOrganization(organization);
        occupation.setDefunct(false);
        Occupation saved = occupationMasterRepository.save(occupation);
        logger.info("Occupation created successfully");
        OccupationDTO occupationDTO = ObjectMapperUtil.copyObject(saved, OccupationDTO.class); // convert the occupation object to dto
        occupationDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(saved.getOrganization(), OrganizationDTO.class));
        return occupationDTO;  // return the occupation dto
    }

    // get all occupations
    @Override
    public List<OccupationDTO> getAllOccupations(String organizationId) {
        logger.info("Fetching all occupation for organization ID: {}", organizationId);
        Organization organization = organizationMasterRepository.findByIdAndDefunct(organizationId, false) // find the organization by id and defunct
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        List<Occupation> occupations = occupationMasterRepository.findAllByDefunct(organization.getId(), false); // find all occupations by organization id and defunct
        return occupations.stream() // convert the list of occupations to list of occupation dtos
                .map(occupation -> { // map each occupation to occupation dto
                    OccupationDTO dto = ObjectMapperUtil.copyObject(occupation, OccupationDTO.class); // convert the occupation object to dto
                    dto.setOrganizationDTO(ObjectMapperUtil.copyObject(occupation.getOrganization(), OrganizationDTO.class));
                    return dto; // return the occupation dto
                })
                .collect(Collectors.toList()); // collect the list of occupation dtos
    }

    // get occupation by id
    @Override
    public OccupationDTO getOccupationById(String id) {
        Occupation occupation = occupationMasterRepository.findByIdAndDefunct(id, false) // find the occupation by id and defunct
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.OCCUPATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        OccupationDTO occupationDTO = ObjectMapperUtil.copyObject(occupation, OccupationDTO.class); // convert the occupation object to dto
        occupationDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(occupation.getOrganization(), OrganizationDTO.class));
        return occupationDTO; // return the occupation dto
    }

    // update occupation
    @Override
    @Transactional
    public OccupationDTO updateOccupation(String id, OccupationReqDTO dto) {
        ValidatorHelper.ValidateAllObject(id, dto); // validate the id and dto
        Organization organization = organizationMasterRepository.findByIdAndDefunct(dto.getOrganization(), false) // find the organization by id and defunct
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        Occupation existing = occupationMasterRepository.findByIdAndDefunct(dto.getId(), false) // find the occupation by id and defunct
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.OCCUPATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        Occupation occupation = ObjectMapperUtil.copyObject(dto, Occupation.class); // convert the dto to occupation object
        occupation.setOrganization(organization);
        occupation.setDefunct(false);
        ObjectMapperUtil.safeCopyObjectAndIgnore(dto, existing, List.of("id", "defunct", "organizationId")); // update the occupation object
        Occupation updated = occupationMasterRepository.save(occupation); // save the occupation object
        logger.info("Occupation with ID {} updated successfully", dto.getId());
        OccupationDTO occupationDTO = ObjectMapperUtil.copyObject(updated, OccupationDTO.class); // convert the occupation object to dto
        occupationDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(updated.getOrganization(), OrganizationDTO.class));
        return occupationDTO; // return the occupation dto
    }

    // delete occupation
    @Override
    public void deleteOccupation(String id) {
        ValidatorHelper.ValidateAllObject(id); // validate the id
        Occupation occupation = occupationMasterRepository.findByIdAndDefunct(id, false) // find the occupation by id and defunct
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.OCCUPATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        occupation.setDefunct(true); // soft delete the occupation
        occupationMasterRepository.save(occupation); // save the occupation object
        logger.info("deleted occupation with ID: {}", id);
    }
}
