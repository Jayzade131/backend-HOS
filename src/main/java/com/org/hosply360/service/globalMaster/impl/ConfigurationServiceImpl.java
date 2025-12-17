package com.org.hosply360.service.globalMaster.impl;

import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.globalMaster.BillingItem;
import com.org.hosply360.dao.globalMaster.Configuration;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dto.globalMasterDTO.BillingItemDTO;
import com.org.hosply360.dto.globalMasterDTO.BillingItemGroupDTO;
import com.org.hosply360.dto.globalMasterDTO.ConfigurationDTO;
import com.org.hosply360.dto.globalMasterDTO.ConfigurationReqDTO;
import com.org.hosply360.dto.globalMasterDTO.OrganizationDTO;
import com.org.hosply360.dto.globalMasterDTO.SpecialityDTO;
import com.org.hosply360.exception.GlobalMasterException;
import com.org.hosply360.repository.globalMasterRepo.BillingItemRepository;
import com.org.hosply360.repository.globalMasterRepo.ConfigurationRepository;
import com.org.hosply360.repository.globalMasterRepo.OrganizationMasterRepository;
import com.org.hosply360.service.globalMaster.ConfigurationService;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import com.org.hosply360.util.validator.ValidatorHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConfigurationServiceImpl implements ConfigurationService {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationServiceImpl.class);
    private final ConfigurationRepository configurationRepository;
    private final OrganizationMasterRepository organizationMasterRepository;
    private final BillingItemRepository billingItemRepository;

    // create configuration
    @Override
    public ConfigurationDTO createConfiguration(ConfigurationReqDTO dto) {
        ValidatorHelper.validateObject(dto); // validate the request object
        Organization organization = organizationMasterRepository.findByIdAndDefunct(dto.getOrganizationId(), false) // validate the organization id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        BillingItem billingItem = billingItemRepository.findByIdAndDefunct(dto.getBillingItemId(), false) // validate the billing item id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.BILLING_ITEM_NOT_FOUND, HttpStatus.NOT_FOUND));
        Configuration configuration = ObjectMapperUtil.copyObject(dto, Configuration.class); // copy the request object to the configuration object
        configuration.setOrganization(organization);
        configuration.setBillingItem(billingItem);
        configuration.setDefunct(false);
        configuration.setOpdVital(dto.getOpdVital());
        Configuration saved = configurationRepository.save(configuration); // save the configuration object
        ConfigurationDTO configurationDTO = ObjectMapperUtil.copyObject(saved, ConfigurationDTO.class); // copy the saved object to the configuration dto
        configurationDTO.setOrganizationDto(ObjectMapperUtil.copyObject(saved.getOrganization(), OrganizationDTO.class));
        BillingItemDTO billingItemDTO = ObjectMapperUtil.copyObject(saved.getBillingItem(), BillingItemDTO.class); // copy the billing item object to the billing item dto
        billingItemDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(saved.getBillingItem().getOrganization(), OrganizationDTO.class));
        BillingItemGroupDTO billingItemGroupDTO = ObjectMapperUtil.copyObject(saved.getBillingItem().getBillingItemGroup(), BillingItemGroupDTO.class); // copy the billing item group object to the billing item group dto
        billingItemGroupDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(saved.getBillingItem().getBillingItemGroup().getOrganization(), OrganizationDTO.class));
        SpecialityDTO specialityDTO = ObjectMapperUtil.copyObject(saved.getBillingItem().getSpeciality(), SpecialityDTO.class); // copy the speciality object to the speciality dto
        specialityDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(saved.getBillingItem().getSpeciality().getOrganization(), OrganizationDTO.class));
        billingItemDTO.setBillingItemGroupDTO(billingItemGroupDTO);
        billingItemDTO.setSpecialityDTO(specialityDTO);
        configurationDTO.setBillingItemDto(billingItemDTO);
        configurationDTO.setOpdVital(configurationDTO.getOpdVital());
        return configurationDTO; // return the configuration dto
    }


    @Override
    public ConfigurationDTO getConfiguration(String id) {
        ValidatorHelper.ValidateAllObject(id); // validate the id
        Configuration config = configurationRepository.findByIdAndDefunct(id, false) // validate the configuration id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.CONFIG_NOT_FOUND, HttpStatus.NOT_FOUND));
        logger.info("Configuration fetched successfully with ID: {}", id);
        ConfigurationDTO configurationDTO = ObjectMapperUtil.copyObject(config, ConfigurationDTO.class); // copy the configuration object to the configuration dto
        configurationDTO.setOrganizationDto(ObjectMapperUtil.copyObject(config.getOrganization(), OrganizationDTO.class));
        BillingItemDTO billingItemDTO = ObjectMapperUtil.copyObject(config.getBillingItem(), BillingItemDTO.class); // copy the billing item object to the billing item dto
        billingItemDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(config.getBillingItem().getOrganization(), OrganizationDTO.class));
        BillingItemGroupDTO billingItemGroupDTO = ObjectMapperUtil.copyObject(config.getBillingItem().getBillingItemGroup(), BillingItemGroupDTO.class); // copy the billing item group object to the billing item group dto
        billingItemGroupDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(config.getBillingItem().getBillingItemGroup().getOrganization(), OrganizationDTO.class));
        SpecialityDTO specialityDTO = ObjectMapperUtil.copyObject(config.getBillingItem().getSpeciality(), SpecialityDTO.class); // copy the speciality object to the speciality dto
        specialityDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(config.getBillingItem().getSpeciality().getOrganization(), OrganizationDTO.class));
        billingItemDTO.setBillingItemGroupDTO(billingItemGroupDTO);
        billingItemDTO.setSpecialityDTO(specialityDTO);
        configurationDTO.setBillingItemDto(billingItemDTO);
        configurationDTO.setOpdVital(configurationDTO.getOpdVital());
        return configurationDTO; // return the configuration dto
    }

    // get all configurations
    @Override
    public List<ConfigurationDTO> getAllConfiguration(String organizationId) {
        logger.info("Fetching all configurations for organization ID: {}", organizationId);
        organizationMasterRepository.findByIdAndDefunct(organizationId, false) // validate the organization id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        List<Configuration> configs = configurationRepository.findByOrganizationIdAndDefunct(organizationId, false); // get all configurations
        return configs.stream() // stream the configurations
                .map(conf -> { // map the configurations to configuration dtos
                    ConfigurationDTO configurationDTO = ObjectMapperUtil.copyObject(conf, ConfigurationDTO.class); // copy the configuration object to the configuration dto
                    configurationDTO.setOrganizationDto(ObjectMapperUtil.copyObject(conf.getOrganization(), OrganizationDTO.class));
                    BillingItemDTO billingItemDTO = ObjectMapperUtil.copyObject(conf.getBillingItem(), BillingItemDTO.class); // copy the billing item object to the billing item dto
                    billingItemDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(conf.getBillingItem().getOrganization(), OrganizationDTO.class));
                    BillingItemGroupDTO billingItemGroupDTO = ObjectMapperUtil.copyObject(conf.getBillingItem().getBillingItemGroup(), BillingItemGroupDTO.class); // copy the billing item group object to the billing item group dto
                    billingItemGroupDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(conf.getBillingItem().getBillingItemGroup().getOrganization(), OrganizationDTO.class));
                    SpecialityDTO specialityDTO = ObjectMapperUtil.copyObject(conf.getBillingItem().getSpeciality(), SpecialityDTO.class); // copy the speciality object to the speciality dto
                    specialityDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(conf.getBillingItem().getSpeciality().getOrganization(), OrganizationDTO.class));
                    billingItemDTO.setBillingItemGroupDTO(billingItemGroupDTO);
                    billingItemDTO.setSpecialityDTO(specialityDTO);
                    configurationDTO.setBillingItemDto(billingItemDTO);
                    configurationDTO.setOpdVital(configurationDTO.getOpdVital());
                    return configurationDTO; // return the configuration dto
                })
                .collect(Collectors.toList()); // collect the configurations to a list
    }

    // update configuration
    @Override
    public ConfigurationDTO updateConfiguration(String id, ConfigurationReqDTO dto) {
        ValidatorHelper.ValidateAllObject(id, dto); // validate the id and dto
        Configuration config = configurationRepository.findByIdAndDefunct(id, false) // validate the configuration id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.CONFIG_NOT_FOUND, HttpStatus.NOT_FOUND));
        Organization organization = organizationMasterRepository.findByIdAndDefunct(dto.getOrganizationId(), false) // validate the organization id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        BillingItem billingItem = billingItemRepository.findByIdAndDefunct(dto.getBillingItemId(), false) // validate the billing item id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.BILLING_ITEM_NOT_FOUND, HttpStatus.NOT_FOUND));
        Configuration configuration = ObjectMapperUtil.copyObject(dto, Configuration.class); // copy the dto to the configuration object
        configuration.setOrganization(organization);
        configuration.setBillingItem(billingItem);
        configuration.setDefunct(false);
        configuration.setOpdVital(dto.getOpdVital());
        ObjectMapperUtil.safeCopyObjectAndIgnore(dto, config, List.of("id", "defunct", "organizationId", "billingItemId")); // copy the dto to the configuration object
        Configuration updated = configurationRepository.save(configuration); // save the configuration
        ConfigurationDTO configurationDTO = ObjectMapperUtil.copyObject(updated, ConfigurationDTO.class); // copy the updated configuration to the configuration dto
        configurationDTO.setOrganizationDto(ObjectMapperUtil.copyObject(updated.getOrganization(), OrganizationDTO.class));
        BillingItemDTO billingItemDTO = ObjectMapperUtil.copyObject(updated.getBillingItem(), BillingItemDTO.class); // copy the billing item to the billing item dto
        billingItemDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(updated.getBillingItem().getOrganization(), OrganizationDTO.class));
        BillingItemGroupDTO billingItemGroupDTO = ObjectMapperUtil.copyObject(updated.getBillingItem().getBillingItemGroup(), BillingItemGroupDTO.class); // copy the billing item group to the billing item group dto
        billingItemGroupDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(updated.getBillingItem().getBillingItemGroup().getOrganization(), OrganizationDTO.class));
        SpecialityDTO specialityDTO = ObjectMapperUtil.copyObject(updated.getBillingItem().getSpeciality(), SpecialityDTO.class); // copy the speciality to the speciality dto
        specialityDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(updated.getBillingItem().getSpeciality().getOrganization(), OrganizationDTO.class));
        billingItemDTO.setBillingItemGroupDTO(billingItemGroupDTO);
        billingItemDTO.setSpecialityDTO(specialityDTO);
        configurationDTO.setBillingItemDto(billingItemDTO);
        configurationDTO.setOpdVital(configurationDTO.getOpdVital());
        return configurationDTO; // return the configuration dto
    }

    // delete configuration
    @Override
    public void deleteConfiguration(String id) {
        ValidatorHelper.ValidateAllObject(id); // validate the id
        Configuration config = configurationRepository.findByIdAndDefunct(id, false) // validate the configuration id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.CONFIG_NOT_FOUND, HttpStatus.NOT_FOUND));
        config.setDefunct(true); // mark the configuration as defunct
        configurationRepository.save(config); // save the configuration
        logger.info("Configuration marked as defunct with ID: {}", id);
    }
}
