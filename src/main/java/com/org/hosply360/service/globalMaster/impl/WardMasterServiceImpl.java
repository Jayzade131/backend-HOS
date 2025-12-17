package com.org.hosply360.service.globalMaster.impl;

import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.globalMaster.WardMaster;
import com.org.hosply360.dto.globalMasterDTO.WardMasterDto;
import com.org.hosply360.exception.GlobalMasterException;
import com.org.hosply360.repository.globalMasterRepo.WardMasterRepository;
import com.org.hosply360.repository.globalMasterRepo.OrganizationMasterRepository;
import com.org.hosply360.service.globalMaster.WardMasterService;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import com.org.hosply360.util.validator.ValidatorHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WardMasterServiceImpl implements WardMasterService {

    private static final Logger logger = LoggerFactory.getLogger(WardMasterServiceImpl.class);
    private final WardMasterRepository wardMasterRepository;
    private final OrganizationMasterRepository organizationRepository;

    // convert entity to dto
    private WardMasterDto toDto(WardMaster ward) {
        return ObjectMapperUtil.copyObject(ward, WardMasterDto.class);
    }

    // validate organization
    private void validateOrganization(String orgId) {
        organizationRepository.findByIdAndDefunct(orgId, false) // find the organization by id and defunct
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    // create ward
    @Override
    @Transactional
    public WardMasterDto createWard(WardMasterDto dto) {
        ValidatorHelper.validateObject(dto); // validate the request object
        validateOrganization(dto.getOrgId()); // validate the organization
        if (wardMasterRepository.existsByWardNameAndOrgId(dto.getWardName(), dto.getOrgId())) { // check if the ward already exists
            logger.warn("Ward '{}' already exists in organization '{}'", dto.getWardName(), dto.getOrgId());
            throw new GlobalMasterException(ErrorConstant.DUPLICATE_ENTRY, HttpStatus.CONFLICT);
        }
        WardMaster ward = ObjectMapperUtil.copyObject(dto, WardMaster.class); // map dto to entity
        ward.setDefunct(false);
        WardMaster saved = wardMasterRepository.save(ward); // save the entity
        logger.info("Ward created with ID: {}", saved.getId());
        return toDto(saved); // return the dto
    }

    // get all wards
    @Override
    public List<WardMasterDto> getAllWards(String orgId) {
        logger.info("Fetching all active (non-defunct) wards for Org ID: {}", orgId);
        validateOrganization(orgId); // validate the organization
        return wardMasterRepository.findAllByOrgIdAndDefunctFalse(orgId).stream() // find all wards by organization and defunct
                .map(this::toDto) // map entity to dto
                .toList(); // return the list of wards
    }

    // get ward by id
    @Override
    public WardMasterDto getWardById(String orgId, String id) {
        logger.info("Fetching ward with ID: {} for Org ID: {}", id, orgId);
        validateOrganization(orgId); // validate the organization
        return wardMasterRepository.findByIdAndOrgIdAndDefunct(id, orgId, false) // find ward by id and organization and defunct
                .map(this::toDto) // map entity to dto
                .orElseThrow(() -> {
                    logger.warn("Ward not found or is defunct with ID: {}", id);
                    return new GlobalMasterException(ErrorConstant.DATA_NOT_FOUND, HttpStatus.NOT_FOUND);
                });
    }

    @Override
    @Transactional
    public WardMasterDto updateWard(WardMasterDto dto) {
        ValidatorHelper.validateObject(dto); // validate the request object
        validateOrganization(dto.getOrgId()); // validate the organization
        WardMaster existing = wardMasterRepository.findById(dto.getId()) // find ward by id
                .orElseThrow(() -> {
                    logger.warn("Attempt to update non-existing ward with ID: {}", dto.getId());
                    return new GlobalMasterException(ErrorConstant.DATA_NOT_FOUND, HttpStatus.NOT_FOUND);
                });
        Optional<WardMaster> duplicate = wardMasterRepository.findByWardNameAndOrgId(dto.getWardName(), dto.getOrgId()); // find ward by name and organization
        if (duplicate.isPresent() && !duplicate.get().getId().equals(dto.getId())) {
            logger.warn("Ward '{}' already exists in organization '{}'", dto.getWardName(), dto.getOrgId());
            throw new GlobalMasterException(ErrorConstant.DUPLICATE_ENTRY, HttpStatus.CONFLICT);
        }
        ObjectMapperUtil.safeCopyObjectAndIgnore(dto, existing, List.of("id", "orgId", "defunct")); // copy the dto to the existing entity
        WardMaster updated = wardMasterRepository.save(existing); // save the entity
        logger.info("Ward updated successfully with ID: {}", updated.getId());
        return toDto(updated); // return the dto
    }

    // delete ward
    @Override
    @Transactional
    public void deleteWardById(String id) {
        ValidatorHelper.ValidateAllObject(id); // validate the request object
        WardMaster existing = wardMasterRepository.findById(id) // find ward by id
                .orElseThrow(() -> {
                    logger.warn("Attempt to delete non-existing ward with ID: {}", id);
                    return new GlobalMasterException(ErrorConstant.DATA_NOT_FOUND, HttpStatus.NOT_FOUND);
                });
        existing.setDefunct(true); // soft delete the ward
        wardMasterRepository.save(existing); // save the entity
        logger.info("Ward soft-deleted (marked as defunct) with ID: {}", id);
    }
}
