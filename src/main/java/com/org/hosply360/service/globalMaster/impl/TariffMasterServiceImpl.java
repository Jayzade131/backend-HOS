package com.org.hosply360.service.globalMaster.impl;

import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dao.globalMaster.Tariff;
import com.org.hosply360.dto.globalMasterDTO.OrganizationDTO;
import com.org.hosply360.dto.globalMasterDTO.TariffDTO;
import com.org.hosply360.dto.globalMasterDTO.TariffReqDTO;
import com.org.hosply360.exception.GlobalMasterException;
import com.org.hosply360.repository.globalMasterRepo.OrganizationMasterRepository;
import com.org.hosply360.repository.globalMasterRepo.TariffMasterRepository;
import com.org.hosply360.service.globalMaster.TariffMasterService;
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
public class TariffMasterServiceImpl implements TariffMasterService {
    private static final Logger logger = LoggerFactory.getLogger(TariffMasterServiceImpl.class);
    private final TariffMasterRepository tariffMasterRepository;
    private final OrganizationMasterRepository organizationRepository;

    // create tariff
    @Override
    public TariffDTO createTariff(TariffReqDTO tariffDTO) {
        ValidatorHelper.validateObject(tariffDTO); // validate the request object
        Organization organization = organizationRepository.findByIdAndDefunct(tariffDTO.getOrganization(), false) // find organization by id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        if (tariffMasterRepository.existsByName(tariffDTO.getName(), false).isPresent()) { // check if tariff name already exists
            logger.warn("Tariff name '{}' already exists", tariffDTO.getName());
            throw new GlobalMasterException(ErrorConstant.TARIFF_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }
        Tariff tariff = ObjectMapperUtil.copyObject(tariffDTO, Tariff.class); // map dto to entity
        tariff.setOrganization(organization);
        tariff.setDefunct(false);
        Tariff saved = tariffMasterRepository.save(tariff); // save the tariff
        logger.info("Tariff created successfully with ID: {}", saved.getId());
        TariffDTO dto = ObjectMapperUtil.copyObject(saved, TariffDTO.class); // map entity to dto
        dto.setOrganizationDTO(ObjectMapperUtil.copyObject(organization, OrganizationDTO.class));
        return dto;
    }

    // update tariff
    @Override
    public TariffDTO updateTariff(String id, TariffReqDTO dto) {
        ValidatorHelper.ValidateAllObject(id, dto); // validate the request object
        Tariff existing = tariffMasterRepository.findByIdAndDefunct(dto.getId(), false) // find tariff by id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.TARIFF_NOT_FOUND, HttpStatus.NOT_FOUND));
        if (!existing.getName().equalsIgnoreCase(dto.getName()) &&
                tariffMasterRepository.existsByName(dto.getName(), false).isPresent()) { // check if tariff name already exists
            throw new GlobalMasterException(ErrorConstant.TARIFF_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }
        Organization organization = organizationRepository.findByIdAndDefunct(dto.getOrganization(), false) // find organization by id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        Tariff tariff = ObjectMapperUtil.copyObject(dto, Tariff.class); // map dto to entity
        tariff.setOrganization(organization);
        tariff.setDefunct(false);
        ObjectMapperUtil.safeCopyObjectAndIgnore(dto, existing, List.of("id", "defunct", "organizationId")); // update the tariff object
        Tariff updated = tariffMasterRepository.save(tariff); // save the updated tariff
        logger.info("Tariff updated successfully with ID: {}", dto.getId());
        TariffDTO updatedDTO = ObjectMapperUtil.copyObject(updated, TariffDTO.class); // map entity to dto
        updatedDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(organization, OrganizationDTO.class));
        return updatedDTO; // return the updated dto
    }

    // get tariff by id
    @Override
    public TariffDTO getTariffById(String id) {
        ValidatorHelper.validateObject(id); // validate the request object
        Tariff tariff = tariffMasterRepository.findByIdAndDefunct(id, false) // find tariff by id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.TARIFF_NOT_FOUND, HttpStatus.NOT_FOUND));
        TariffDTO dto = ObjectMapperUtil.copyObject(tariff, TariffDTO.class); // map entity to dto
        dto.setOrganizationDTO(ObjectMapperUtil.copyObject(tariff.getOrganization(), OrganizationDTO.class));
        return dto; // return the dto
    }

    // get all tariffs
    @Override
    public List<TariffDTO> getAllTariffs(String orgId) {
        ValidatorHelper.validateObject(orgId); // validate the request object
        organizationRepository.findByIdAndDefunct(orgId, false) // find organization by id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        List<Tariff> tariffs = tariffMasterRepository.findByOrganizationIdAndDefunct(orgId, false); // find all tariffs by organization id
        return tariffs.stream().map(tariff -> { // map entity to dto
            TariffDTO dto = ObjectMapperUtil.copyObject(tariff, TariffDTO.class); // map entity to dto
            dto.setOrganizationDTO(ObjectMapperUtil.copyObject(tariff.getOrganization(), OrganizationDTO.class));
            return dto; // return the dto
        }).toList(); // return the list of dtos
    }

    // delete tariff by id
    @Override
    public void deleteTariffById(String id) {
        ValidatorHelper.ValidateAllObject(id); // validate the request object
        Tariff tariff = tariffMasterRepository.findByIdAndDefunct(id, false) // find tariff by id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.TARIFF_NOT_FOUND, HttpStatus.NOT_FOUND));
        tariff.setDefunct(true); // soft delete the tariff
        tariffMasterRepository.save(tariff); // save the tariff
        logger.info("Tariff soft-deleted successfully with ID: {}", id);
    }
}
