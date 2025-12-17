package com.org.hosply360.service.globalMaster.impl;

import com.org.hosply360.constant.Enums.AdmitStatus;
import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.globalMaster.WardBedMaster;
import com.org.hosply360.dao.globalMaster.WardMaster;
import com.org.hosply360.dto.globalMasterDTO.WardBedMasterDto;
import com.org.hosply360.exception.GlobalMasterException;
import com.org.hosply360.repository.globalMasterRepo.WardBedMasterRepository;
import com.org.hosply360.repository.globalMasterRepo.WardMasterRepository;
import com.org.hosply360.service.globalMaster.WardBedMasterService;
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
public class WardBedMasterServiceImpl implements WardBedMasterService {

    private static final Logger logger = LoggerFactory.getLogger(WardBedMasterServiceImpl.class);
    private final WardBedMasterRepository bedRepository;
    private final WardMasterRepository wardRepository;

    // validate ward and organization
    private void validateWardAndOrganization(String orgId, String wardId) {
        wardRepository.findByIdAndOrgIdAndDefunct(wardId, orgId, false) // find the ward by id and organization id and defunct
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.DATA_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    // convert entity to dto
    private WardBedMasterDto toDto(WardBedMaster bed) {
        WardBedMasterDto dto = ObjectMapperUtil.copyObject(bed, WardBedMasterDto.class); // map entity to dto
        dto.setWardId(bed.getWard().getId());
        dto.setWardName(bed.getWard().getWardName());
        return dto;
    }

    // create bed
    @Override
    @Transactional
    public WardBedMasterDto create(WardBedMasterDto dto) {
        ValidatorHelper.validateObject(dto); // validate the request object
        validateWardAndOrganization(dto.getOrgId(), dto.getWardId()); // validate the ward and organization
        if (bedRepository.existsByOrgIdAndWardIdAndBedNo(dto.getOrgId(), dto.getWardId(), dto.getBedNo())) { // check if the bed already exists
            logger.warn("Bed {} already exists for ward {} in org {}", dto.getBedNo(), dto.getWardId(), dto.getOrgId());
            throw new GlobalMasterException(ErrorConstant.DUPLICATE_ENTRY, HttpStatus.CONFLICT);
        }
        WardMaster ward = wardRepository.findById(dto.getWardId()).orElseThrow(); // find the ward by id
        WardBedMaster bed = ObjectMapperUtil.copyObject(dto, WardBedMaster.class); // map dto to entity
        bed.setWard(ward);
        bed.setStatus(AdmitStatus.AVAILABLE);
        bed.setDefunct(false);
        WardBedMaster saved = bedRepository.save(bed); // save the entity
        logger.info("Bed created with ID: {}", saved.getId());
        return toDto(saved); // return the dto
    }

    // get all beds by ward
    @Override
    public List<WardBedMasterDto> getAllByWard(String orgId, String wardId) {
        logger.info("Fetching beds for Ward ID: {} and Org ID: {}", wardId, orgId);
        validateWardAndOrganization(orgId, wardId); // validate the ward and organization
        return bedRepository.findAllByOrgIdAndWardIdAndDefunctFalse(orgId, wardId).stream() // find all beds by ward and organization
                .map(this::toDto)
                .toList();
    }

    // get bed by id
    @Override
    public WardBedMasterDto getById(String id) {
        WardBedMaster bed = bedRepository.findByIdAndDefunctFalse(id) // find the bed by id and defunct
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.DATA_NOT_FOUND, HttpStatus.NOT_FOUND));
        return toDto(bed); // return the dto
    }

    // update bed
    @Override
    @Transactional
    public WardBedMasterDto update(WardBedMasterDto dto) {
        ValidatorHelper.validateObject(dto); // validate the request object
        validateWardAndOrganization(dto.getOrgId(), dto.getWardId()); // validate the ward and organization
        WardBedMaster existing = bedRepository.findById(dto.getId()) // find the bed by id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.DATA_NOT_FOUND, HttpStatus.NOT_FOUND));
        boolean isDuplicate = bedRepository.existsByOrgIdAndWardIdAndBedNo(dto.getOrgId(), dto.getWardId(), dto.getBedNo()) // check if the bed already exists
                && !existing.getBedNo().equals(dto.getBedNo()); // check if the bed number is different
        if (isDuplicate) {
            logger.warn("Duplicate bed number '{}' in ward '{}' for org '{}'", dto.getBedNo(), dto.getWardId(), dto.getOrgId());
            throw new GlobalMasterException(ErrorConstant.DUPLICATE_ENTRY, HttpStatus.CONFLICT);
        }
        ObjectMapperUtil.safeCopyObjectAndIgnore(dto, existing, List.of("id", "orgId", "ward", "defunct")); // copy the dto to the existing entity
        existing.setWard(wardRepository.findById(dto.getWardId()).orElseThrow()); // set the ward
        WardBedMaster updated = bedRepository.save(existing); // save the entity
        logger.info("Bed updated with ID: {}", updated.getId());
        return toDto(updated); // return the dto
    }

    // delete bed
    @Override
    @Transactional
    public void delete(String id) {
        ValidatorHelper.ValidateAllObject(id);
        bedRepository.findById(id).orElseThrow(() -> new GlobalMasterException(ErrorConstant.DATA_NOT_FOUND, HttpStatus.NOT_FOUND)); // find the bed by id
        bedRepository.deleteById(id); // delete the bed
        logger.info("Bed Deleted with ID: {}", id);
    }

    // get all available beds by ward
    @Override
    public List<WardBedMasterDto> getAllAvilableBedsByWard(String orgId, String wardId) {
        logger.info("Fetching available beds for Ward ID: {} and Org ID: {}", wardId, orgId);
        validateWardAndOrganization(orgId, wardId); // validate the ward and organization
        return bedRepository.findAllByOrgIdAndWardIdAndStatusAndDefunctFalse(orgId, wardId, AdmitStatus.AVAILABLE).stream() // find all available beds by ward and organization
                .map(this::toDto)
                .toList();
    }
}
