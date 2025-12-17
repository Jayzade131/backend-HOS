package com.org.hosply360.service.globalMaster.impl;

import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.globalMaster.InsuranceProvider;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dto.globalMasterDTO.InsuranceProviderDTO;
import com.org.hosply360.dto.globalMasterDTO.InsuranceProviderReqDTO;
import com.org.hosply360.dto.globalMasterDTO.OrganizationDTO;
import com.org.hosply360.exception.GlobalMasterException;
import com.org.hosply360.repository.globalMasterRepo.InsuranceProviderRepository;
import com.org.hosply360.repository.globalMasterRepo.OrganizationMasterRepository;
import com.org.hosply360.service.globalMaster.InsuranceProviderService;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import com.org.hosply360.util.validator.ValidatorHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class InsuranceProviderServiceImpl implements InsuranceProviderService {

    private static final Logger logger = LoggerFactory.getLogger(InsuranceProviderServiceImpl.class);
    private final InsuranceProviderRepository insuranceProviderRepository;
    private final OrganizationMasterRepository organizationMasterRepository;

    // create insurance provider
    @Override
    @Transactional
    public InsuranceProviderDTO createInsuranceProvider(InsuranceProviderReqDTO dto) {
        ValidatorHelper.validateObject(dto); // validate the request object
        Organization organization = organizationMasterRepository.findByIdAndDefunct(dto.getOrganization(), false) // validate the organization id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        if (insuranceProviderRepository.findByCodeAndDefunct(dto.getCode(), false).isPresent()) { // validate the insurance provider code
            logger.info("Insurance Provider code {} already exists", dto.getCode());
            throw new GlobalMasterException(ErrorConstant.INSURANCE_PROVIDER_CODE_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        }
        InsuranceProvider insuranceProvider = ObjectMapperUtil.copyObject(dto, InsuranceProvider.class); // copy the request object to the insurance provider object
        insuranceProvider.setOrganization(organization);
        insuranceProvider.setDefunct(false);
        InsuranceProvider saved = insuranceProviderRepository.save(insuranceProvider); // save the insurance provider object
        logger.info("Insurance provider created successfully");
        InsuranceProviderDTO insuranceProviderDTO = ObjectMapperUtil.copyObject(saved, InsuranceProviderDTO.class); // copy the insurance provider object to the dto
        insuranceProviderDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(saved.getOrganization(), OrganizationDTO.class));
        return insuranceProviderDTO; // return the dto
    }

    // update insurance provider
    @Override
    @Transactional
    public InsuranceProviderDTO updateInsuranceProvider(String id, InsuranceProviderReqDTO dto) {
        ValidatorHelper.ValidateAllObject(id, dto); // validate the request object
        InsuranceProvider existing = insuranceProviderRepository.findByIdAndDefunct(dto.getId(), false) // validate the insurance provider id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.INSURANCE_PROVIDER_NOT_FOUND, HttpStatus.NOT_FOUND));
        Organization organization = organizationMasterRepository.findByIdAndDefunct(dto.getOrganization(), false) // validate the organization id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        InsuranceProvider insuranceProvider = ObjectMapperUtil.copyObject(dto, InsuranceProvider.class); // copy the request object to the insurance provider object
        insuranceProvider.setOrganization(organization);
        insuranceProvider.setDefunct(false);
        ObjectMapperUtil.safeCopyObjectAndIgnore(dto, existing, List.of("id", "defunct", "organizationId")); // update the insurance provider object
        InsuranceProvider saveupdate = insuranceProviderRepository.save(insuranceProvider); // save the insurance provider object
        logger.info("Insurance provider updated successfully");
        InsuranceProviderDTO insuranceProviderDTO = ObjectMapperUtil.copyObject(saveupdate, InsuranceProviderDTO.class); // convert the insurance provider object to dto
        insuranceProviderDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(saveupdate.getOrganization(), OrganizationDTO.class));
        return insuranceProviderDTO; // return the dto
    }

    // get insurance provider by id
    @Override
    public InsuranceProviderDTO getInsuranceProviderById(String id) {
        InsuranceProvider provider = insuranceProviderRepository.findByIdAndDefunct(id, false) // validate the insurance provider id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.INSURANCE_PROVIDER_NOT_FOUND, HttpStatus.NOT_FOUND));
        InsuranceProviderDTO insuranceProviderDTO = ObjectMapperUtil.copyObject(provider, InsuranceProviderDTO.class); // convert the insurance provider object to dto
        insuranceProviderDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(provider.getOrganization(), OrganizationDTO.class));
        return insuranceProviderDTO; // return the dto
    }

    // get all insurance providers
    @Override
    public List<InsuranceProviderDTO> getAllInsuranceProviders(String organizationId) {
        logger.info("Fetching all insurance provider for organization ID: {}", organizationId);
        Organization organization = organizationMasterRepository.findByIdAndDefunct(organizationId, false) // validate the organization id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        List<InsuranceProvider> insuranceProviders = insuranceProviderRepository.findAllByDefunct(organization.getId(), false); // get all insurance providers
        return insuranceProviders.stream() // convert the insurance provider object to dto
                .map(insuranceProvider -> { // map each insurance provider object to dto
                    InsuranceProviderDTO dto = ObjectMapperUtil.copyObject(insuranceProvider, InsuranceProviderDTO.class); // convert the insurance provider object to dto
                    dto.setOrganizationDTO(ObjectMapperUtil.copyObject(insuranceProvider.getOrganization(), OrganizationDTO.class));
                    return dto; // return the dto
                })
                .collect(Collectors.toList()); // collect the dtos
    }

    // delete insurance provider by id
    @Override
    public void deleteInsuranceProviderById(String id) {
        ValidatorHelper.ValidateAllObject(id); // validate the request object
        InsuranceProvider provider = insuranceProviderRepository.findByIdAndDefunct(id, false) // validate the insurance provider id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.INSURANCE_PROVIDER_NOT_FOUND, HttpStatus.NOT_FOUND));
        provider.setDefunct(true); // set the defunct flag to true
        insuranceProviderRepository.save(provider); // save the insurance provider object
        logger.info("Deleted insurance provider with ID: {}", id);
    }
}
