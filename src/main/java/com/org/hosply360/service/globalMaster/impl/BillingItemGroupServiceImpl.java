package com.org.hosply360.service.globalMaster.impl;

import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.globalMaster.BillingItemGroup;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dto.globalMasterDTO.BillingItemGroupDTO;
import com.org.hosply360.dto.globalMasterDTO.BillingItemGroupReqDTO;
import com.org.hosply360.dto.globalMasterDTO.OrganizationDTO;
import com.org.hosply360.exception.GlobalMasterException;
import com.org.hosply360.repository.globalMasterRepo.BillingItemGroupRepository;
import com.org.hosply360.repository.globalMasterRepo.OrganizationMasterRepository;
import com.org.hosply360.service.globalMaster.BillingItemGroupService;
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
public class BillingItemGroupServiceImpl implements BillingItemGroupService {
    private static final Logger logger = LoggerFactory.getLogger(BillingItemGroupServiceImpl.class);
    private final BillingItemGroupRepository billingItemGroupRepository;
    private final OrganizationMasterRepository organizationMasterRepository;

    // create billing item group
    @Override
    public BillingItemGroupDTO createBillingItemGroup(BillingItemGroupReqDTO billingItemGroupReqDTO) {
        ValidatorHelper.validateObject(billingItemGroupReqDTO); // validate the request object
        Organization organization = organizationMasterRepository.findByIdAndDefunct(billingItemGroupReqDTO.getOrganization(), false) // validate the organization
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        if (billingItemGroupRepository.findByItemGroupNameAndDefunct(billingItemGroupReqDTO.getItemGroupName(), false).isPresent())  // validate the item group name
        {
            throw new GlobalMasterException(ErrorConstant.ITEM_GROUP_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }
        BillingItemGroup billingItemGroup = ObjectMapperUtil.copyObject(billingItemGroupReqDTO, BillingItemGroup.class); // convert the request object to entity
        billingItemGroup.setOrganization(organization);
        billingItemGroup.setDefunct(false);
        BillingItemGroup saveBillingItemGroup = billingItemGroupRepository.save(billingItemGroup); // save the entity
        logger.info("Billing Item Group created successfully");
        BillingItemGroupDTO billingItemGroupDTO = ObjectMapperUtil.copyObject(saveBillingItemGroup, BillingItemGroupDTO.class); // convert the entity to dto
        billingItemGroupDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(saveBillingItemGroup.getOrganization(), OrganizationDTO.class)); // convert the organization to dto
        return billingItemGroupDTO; // return the dto
    }

    // update billing item group
    @Override
    public BillingItemGroupDTO updateBillingItemGroup(String id, BillingItemGroupReqDTO billingItemGroupDTO) {
        ValidatorHelper.ValidateAllObject(id, billingItemGroupDTO); // validate the request object
        BillingItemGroup existingBillingItemGroup = billingItemGroupRepository.findByIdAndDefunct(id, false) // validate the billing item group
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ITEM_GROUP_NOT_FOUND, HttpStatus.NOT_FOUND));
        existingBillingItemGroup.setDefunct(false);
        Organization organization = organizationMasterRepository.findByIdAndDefunct(billingItemGroupDTO.getOrganization(), false) // validate the organization
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        BillingItemGroup billingItemGroup = ObjectMapperUtil.copyObject(billingItemGroupDTO, BillingItemGroup.class); // convert the request object to entity
        billingItemGroup.setOrganization(organization);
        billingItemGroup.setDefunct(false);
        ObjectMapperUtil.safeCopyObjectAndIgnore(billingItemGroupDTO, existingBillingItemGroup, List.of("id", "defunct", "organizationId")); // update the billing item group
        BillingItemGroup updatedBillingItemGroup = billingItemGroupRepository.save(billingItemGroup); // save the entity
        logger.info("Billing Item Group updated successfully");
        BillingItemGroupDTO billingItemGroupDTO1 = ObjectMapperUtil.copyObject(updatedBillingItemGroup, BillingItemGroupDTO.class); // convert the entity to dto
        billingItemGroupDTO1.setOrganizationDTO(ObjectMapperUtil.copyObject(updatedBillingItemGroup.getOrganization(), OrganizationDTO.class));
        return billingItemGroupDTO1; // return the dto
    }

    // get billing item group by id
    @Override
    public BillingItemGroupDTO getBillingItemGroupById(String id) {
        ValidatorHelper.ValidateAllObject(id); // validate the request object
        BillingItemGroup billingItemGroup = billingItemGroupRepository.findByIdAndDefunct(id, false) // validate the billing item group
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ITEM_GROUP_NOT_FOUND, HttpStatus.NOT_FOUND));
        logger.info("Billing Item Group fetched successfully");
        BillingItemGroupDTO billingItemGroupDTO1 = ObjectMapperUtil.copyObject(billingItemGroup, BillingItemGroupDTO.class); // convert the entity to dto
        billingItemGroupDTO1.setOrganizationDTO(ObjectMapperUtil.copyObject(billingItemGroup.getOrganization(), OrganizationDTO.class));
        return billingItemGroupDTO1; // return the dto
    }

    // get all billing item groups
    @Override
    public List<BillingItemGroupDTO> getAllBillingItemGroups(String organizationId) {
        logger.info("Fetching all billing item groups in LIFO order for organization ID: {}", organizationId);
        Organization organization = organizationMasterRepository.findByIdAndDefunct(organizationId, false) // validate the organization
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        List<BillingItemGroup> billingItemGroups = billingItemGroupRepository.findByOrganizationId(organization.getId(), false); // validate the billing item groups
        return billingItemGroups.stream() // convert the list to stream
                .map(billingItemGroup -> { // convert the entity to dto
                    BillingItemGroupDTO dto = ObjectMapperUtil.copyObject(billingItemGroup, BillingItemGroupDTO.class); // convert the entity to dto
                    dto.setOrganizationDTO(ObjectMapperUtil.copyObject(billingItemGroup.getOrganization(), OrganizationDTO.class)); // convert the organization to dto
                    return dto; // return the dto
                }).toList(); // convert the stream to list
    }

    // delete billing item group
    @Override
    public void deleteBillingItemGroup(String id) {
        ValidatorHelper.ValidateAllObject(id); // validate the request object
        BillingItemGroup billingItemGroup = billingItemGroupRepository.findByIdAndDefunct(id, false) // validate the billing item group
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ITEM_GROUP_NOT_FOUND, HttpStatus.NOT_FOUND));
        billingItemGroup.setDefunct(true); // mark the billing item group as defunct
        billingItemGroupRepository.save(billingItemGroup); // save the entity
        logger.info("Billing Item Group deleted successfully with ID: {}", id);
    }
}
