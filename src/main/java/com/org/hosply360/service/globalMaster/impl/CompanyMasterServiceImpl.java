package com.org.hosply360.service.globalMaster.impl;

import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.globalMaster.CompanyMaster;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dao.globalMaster.Tariff;
import com.org.hosply360.dto.globalMasterDTO.CompanyMasterDTO;
import com.org.hosply360.dto.globalMasterDTO.CompanyMasterReqDTO;
import com.org.hosply360.dto.globalMasterDTO.OrganizationDTO;
import com.org.hosply360.dto.globalMasterDTO.TariffDTO;
import com.org.hosply360.exception.GlobalMasterException;
import com.org.hosply360.repository.globalMasterRepo.CompanyMasterRepository;
import com.org.hosply360.repository.globalMasterRepo.OrganizationMasterRepository;
import com.org.hosply360.repository.globalMasterRepo.TariffMasterRepository;
import com.org.hosply360.service.globalMaster.CompanyMasterService;
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
public class CompanyMasterServiceImpl implements CompanyMasterService {

    private static final Logger logger = LoggerFactory.getLogger(CompanyMasterServiceImpl.class);
    private final CompanyMasterRepository companyMasterRepository;
    private final OrganizationMasterRepository organizationRepository;
    private final TariffMasterRepository tariffRepository;

    // create company
    @Override
    public CompanyMasterDTO createCompany(CompanyMasterReqDTO reqDTO) {
        ValidatorHelper.validateObject(reqDTO); // validate the request object
        Organization organization = organizationRepository.findByIdAndDefunct(reqDTO.getOrganization(), false) // validate the organization
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        Tariff tariff = tariffRepository.findByIdAndDefunct(reqDTO.getTariff(), false)
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.TARIFF_NOT_FOUND, HttpStatus.NOT_FOUND)); // validate the tariff
        if (companyMasterRepository.findByCompanyNameAndDefunct(reqDTO.getCompanyName(), false).isPresent()) { // validate the company name
            throw new GlobalMasterException(ErrorConstant.COMPANY_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }
        CompanyMaster companyMaster = ObjectMapperUtil.copyObject(reqDTO, CompanyMaster.class); // convert the request object to company master object
        companyMaster.setOrganization(organization);
        companyMaster.setTariff(tariff);
        companyMaster.setDefunct(false);
        companyMaster.setCompanyMasterStatus(reqDTO.getCompanyMasterStatus());
        CompanyMaster saved = companyMasterRepository.save(companyMaster); // save the company master object
        logger.info("Company created successfully");
        CompanyMasterDTO dto = ObjectMapperUtil.copyObject(saved, CompanyMasterDTO.class); // convert the company master object to dto
        dto.setOrganizationDTO(ObjectMapperUtil.copyObject(saved.getOrganization(), OrganizationDTO.class));
        TariffDTO tariffDTO = ObjectMapperUtil.copyObject(saved.getTariff(), TariffDTO.class);
        tariffDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(tariff.getOrganization(), OrganizationDTO.class));
        dto.setTariffDTO(tariffDTO);
        dto.setCompanyMasterStatus(saved.getCompanyMasterStatus());
        return dto; // return the dto
    }

    // update company
    @Override
    public CompanyMasterDTO updateCompany(String id, CompanyMasterReqDTO reqDTO) {
        ValidatorHelper.ValidateAllObject(id, reqDTO); // validate the request object
        CompanyMaster existing = companyMasterRepository.findByIdAndDefunct(id, false) // validate the company id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.COMPANY_NOT_FOUND, HttpStatus.NOT_FOUND));
        Organization organization = organizationRepository.findByIdAndDefunct(reqDTO.getOrganization(), false) // validate the organization id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        Tariff tariff = tariffRepository.findByIdAndDefunct(reqDTO.getTariff(), false) // validate the tariff id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.TARIFF_NOT_FOUND, HttpStatus.NOT_FOUND));
        ObjectMapperUtil.safeCopyObjectAndIgnore(reqDTO, existing, List.of("id", "defunct", "organizationId")); // update the company master object
        existing.setOrganization(organization);
        existing.setTariff(tariff);
        existing.setDefunct(false);
        CompanyMaster updated = companyMasterRepository.save(existing); // save the company master object
        logger.info("Company updated successfully");
        CompanyMasterDTO dto = ObjectMapperUtil.copyObject(updated, CompanyMasterDTO.class); // convert the company master object to dto
        dto.setOrganizationDTO(ObjectMapperUtil.copyObject(updated.getOrganization(), OrganizationDTO.class));
        TariffDTO tariffDTO = ObjectMapperUtil.copyObject(updated.getTariff(), TariffDTO.class); // convert the tariff object to dto
        tariffDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(tariff.getOrganization(), OrganizationDTO.class));
        dto.setTariffDTO(tariffDTO);
        dto.setCompanyMasterStatus(updated.getCompanyMasterStatus());
        return dto; // return the dto
    }

    // get company by id
    @Override
    public CompanyMasterDTO getCompanyById(String id) {
        ValidatorHelper.validateObject(id); // validate the request object
        CompanyMaster entity = companyMasterRepository.findByIdAndDefunct(id, false) // validate the company id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.COMPANY_NOT_FOUND, HttpStatus.NOT_FOUND));
        logger.info("Company fetched successfully");
        CompanyMasterDTO dto = ObjectMapperUtil.copyObject(entity, CompanyMasterDTO.class); // convert the company master object to dto
        dto.setOrganizationDTO(ObjectMapperUtil.copyObject(entity.getOrganization(), OrganizationDTO.class));
        TariffDTO tariffDTO = ObjectMapperUtil.copyObject(entity.getTariff(), TariffDTO.class); // convert the tariff object to dto
        tariffDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(entity.getTariff().getOrganization(), OrganizationDTO.class));
        dto.setTariffDTO(tariffDTO);
        dto.setCompanyMasterStatus(entity.getCompanyMasterStatus());
        return dto; // return the dto
    }

    // get all companies
    @Override
    public List<CompanyMasterDTO> getAllCompanies(String organizationId) {
        logger.info("Fetching all companies for organization ID: {}", organizationId);
        Organization organization = organizationRepository.findByIdAndDefunct(organizationId, false)
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        List<CompanyMaster> companyMasters = companyMasterRepository.findByOrganizationId(organization.getId(), false); // get all companies
        return companyMasters.stream() // convert the company master objects to dtos
                .map(companyMaster -> { // map each company master object to dto
                    CompanyMasterDTO dto = ObjectMapperUtil.copyObject(companyMaster, CompanyMasterDTO.class); // convert the company master object to dto
                    dto.setOrganizationDTO(ObjectMapperUtil.copyObject(companyMaster.getOrganization(), OrganizationDTO.class));
                    TariffDTO tariffDTO = ObjectMapperUtil.copyObject(companyMaster.getTariff(), TariffDTO.class); // convert the tariff object to dto
                    tariffDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(companyMaster.getTariff().getOrganization(), OrganizationDTO.class));
                    dto.setTariffDTO(tariffDTO);
                    dto.setCompanyMasterStatus(companyMaster.getCompanyMasterStatus());
                    return dto; // return the dto
                })
                .collect(Collectors.toList()); // collect the dtos
    }

    // delete company
    @Override
    public void deleteCompany(String id) {
        ValidatorHelper.ValidateAllObject(id); // validate the request object
        CompanyMaster entity = companyMasterRepository.findByIdAndDefunct(id, false) // validate the company id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.COMPANY_NOT_FOUND, HttpStatus.NOT_FOUND));
        entity.setDefunct(true); // soft delete the company
        companyMasterRepository.save(entity); // save the company
        logger.info("Company soft-deleted with ID: {}", id);
    }
}
