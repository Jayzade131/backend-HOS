package com.org.hosply360.service.globalMaster.impl;

import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.globalMaster.Address;
import com.org.hosply360.dao.globalMaster.DocumentInfo;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dto.globalMasterDTO.AddressDTO;
import com.org.hosply360.dto.globalMasterDTO.DocumentInfoDTO;
import com.org.hosply360.dto.globalMasterDTO.OrganizationDTO;
import com.org.hosply360.dto.globalMasterDTO.OrganizationReqDTO;
import com.org.hosply360.exception.GlobalMasterException;
import com.org.hosply360.repository.globalMasterRepo.AddressMasterRepository;
import com.org.hosply360.repository.globalMasterRepo.OrganizationMasterRepository;
import com.org.hosply360.service.globalMaster.OrganizationMasterService;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import com.org.hosply360.util.validator.ValidatorHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizationMasterServiceImpl implements OrganizationMasterService {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationMasterServiceImpl.class);


    private final OrganizationMasterRepository organizationRepository;
    private final AddressMasterRepository addressRepository;

    public OrganizationDTO getOrganizationDTO(Organization organization) {
        if (organization == null) {
            return null;
        }

        OrganizationDTO dto = ObjectMapperUtil.copyObject(organization, OrganizationDTO.class);

        // Map Address safely
        if (organization.getAddress() != null) {
            dto.setAddress(ObjectMapperUtil.copyObject(organization.getAddress(), AddressDTO.class));
        }

        // Map Parent Organization (avoid deep recursion)
        if (organization.getParentOrganization() != null) {
            Organization parent = organization.getParentOrganization();
            OrganizationDTO parentDTO = new OrganizationDTO();
            parentDTO.setId(parent.getId());
            parentDTO.setOrganizationCode(parent.getOrganizationCode());
            parentDTO.setOrganizationName(parent.getOrganizationName());
            parentDTO.setOrganizationQuote(parent.getOrganizationQuote());

            if (parent.getAddress() != null) {
                parentDTO.setAddress(ObjectMapperUtil.copyObject(parent.getAddress(), AddressDTO.class));
            }

            dto.setParentOrganization(parentDTO);
        }

        // Map Organization Logo
        if (organization.getOrgLogo() != null) {
            DocumentInfoDTO documentInfoDTO = new DocumentInfoDTO();
            documentInfoDTO.setDocFile(Base64.getEncoder().encodeToString(organization.getOrgLogo().getDocFile()));
            documentInfoDTO.setDocName(organization.getOrgLogo().getDocName());
            dto.setOrgLogo(documentInfoDTO);
        }

        return dto;
    }

    @Transactional
    public String saveOrUpdateOrganization(OrganizationReqDTO organizationReqDTO) {

        ValidatorHelper.validateObject(organizationReqDTO);

        boolean isUpdate = organizationReqDTO.getId() != null && !organizationReqDTO.getId().isBlank();
        Organization organization;

        if (isUpdate) {
            // ----- UPDATE FLOW -----
            organization = organizationRepository.findByIdAndDefunct(organizationReqDTO.getId(), false)
                    .orElseThrow(() -> new GlobalMasterException(
                            ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));

            // Prevent immutable field overwrite
            ObjectMapperUtil.safeCopyObjectAndIgnore(
                    organizationReqDTO,
                    organization,
                    List.of("id", "organizationCode", "defunct")
            );

        } else {
            // ----- CREATE FLOW -----
            organizationRepository.findByOrganizationCodeAndDefunct(
                    organizationReqDTO.getOrganizationCode(), false
            ).ifPresent(existing -> {
                logger.warn("Organization with code {} already exists", organizationReqDTO.getOrganizationCode());
                throw new GlobalMasterException(
                        ErrorConstant.ORGANIZATION_CODE_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
            });

            organization = ObjectMapperUtil.copyObject(organizationReqDTO, Organization.class);
            organization.setDefunct(false);
        }

        // ----- ADDRESS HANDLING -----
        if (organizationReqDTO.getAddress() != null) {
            Address address;
            if (organizationReqDTO.getAddress().getId() != null) {
                address = addressRepository.findById(organizationReqDTO.getAddress().getId()).orElse(new Address());
            } else {
                address = new Address();
            }
            ObjectMapperUtil.safeCopyObjectAndIgnore(organizationReqDTO.getAddress(), address, List.of("id"));
            Address savedAddress = addressRepository.save(address);
            organization.setAddress(savedAddress);
        }

        // ----- PARENT ORGANIZATION -----
        if (organizationReqDTO.getParentOrgId() != null && !organizationReqDTO.getParentOrgId().isBlank()) {
            if (organizationReqDTO.getId() != null &&
                    organizationReqDTO.getParentOrgId().equals(organizationReqDTO.getId())) {
                throw new GlobalMasterException(
                        ErrorConstant.ORGANIZATION_CANNOT_BE_ITS_OWN_PARENT, HttpStatus.BAD_REQUEST);
            }

            organizationRepository.findByIdAndDefunct(organizationReqDTO.getParentOrgId(), false)
                    .ifPresentOrElse(
                            organization::setParentOrganization,
                            () -> logger.warn("Parent organization not found for ID: {}", organizationReqDTO.getParentOrgId())
                    );
        } else {
            organization.setParentOrganization(null);
        }

        // ----- LOGO HANDLING -----
        if (organizationReqDTO.getDocumentInfoDTO() != null &&
                organizationReqDTO.getDocumentInfoDTO().getDocFile() != null &&
                !organizationReqDTO.getDocumentInfoDTO().getDocFile().isBlank()) {
            try {

                DocumentInfo orgLogo = new DocumentInfo();
                orgLogo.setDocFile(Base64.getDecoder().decode(organizationReqDTO.getDocumentInfoDTO().getDocFile()));
                orgLogo.setDocName(organizationReqDTO.getDocumentInfoDTO().getDocName());
                organization.setOrgLogo(orgLogo);
                logger.debug("Organization logo processed successfully: {}", orgLogo.getDocName());
            } catch (IllegalArgumentException e) {
                logger.error("Invalid Base64 logo file for organization", e);
                throw new GlobalMasterException(ErrorConstant.INVALID_LOGO_FILE_FORMAT, HttpStatus.BAD_REQUEST);
            }
        }

        // ----- SAVE OR UPDATE -----
        Organization savedOrganization = organizationRepository.save(organization);

        if (isUpdate) {
            logger.info("Organization updated successfully with ID {}", savedOrganization.getId());
        } else {
            logger.info("Organization created successfully with ID {}", savedOrganization.getId());
        }

        return savedOrganization.getId();
    }


    public OrganizationDTO getOrganization(String id) {
        Organization organization = organizationRepository.findByIdAndDefunct(id, false)
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        return getOrganizationDTO(organization);

    }

    @Override
    public List<OrganizationDTO> fetchAllOrganization() {
        logger.info("Fetch All Organization");
        return organizationRepository.findAllByDefuncts(false).stream().map(this::getOrganizationDTO).collect(Collectors.toList());
    }

}
