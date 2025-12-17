package com.org.hosply360.service.globalMaster.impl;

import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.globalMaster.BillingItem;
import com.org.hosply360.dao.globalMaster.BillingItemGroup;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dao.globalMaster.Speciality;
import com.org.hosply360.dto.globalMasterDTO.BillingItemDTO;
import com.org.hosply360.dto.globalMasterDTO.BillingItemGroupDTO;
import com.org.hosply360.dto.globalMasterDTO.BillingItemReqDTO;
import com.org.hosply360.dto.globalMasterDTO.OrganizationDTO;
import com.org.hosply360.dto.globalMasterDTO.SpecialityDTO;
import com.org.hosply360.exception.GlobalMasterException;
import com.org.hosply360.repository.globalMasterRepo.BillingItemGroupRepository;
import com.org.hosply360.repository.globalMasterRepo.BillingItemRepository;
import com.org.hosply360.repository.globalMasterRepo.OrganizationMasterRepository;
import com.org.hosply360.repository.globalMasterRepo.SpecialityMasterRepository;
import com.org.hosply360.service.globalMaster.BillingItemService;
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
public class BillingItemServiceImpl implements BillingItemService {
    private static final Logger logger = LoggerFactory.getLogger(BillingItemServiceImpl.class);
    private final BillingItemRepository billingItemRepository;
    private final BillingItemGroupRepository billingItemGroupRepository;
    private final SpecialityMasterRepository specialityRepository;
    private final OrganizationMasterRepository organizationMasterRepository;

    // create billing item
    @Override
    public BillingItemDTO createBillingItem(BillingItemReqDTO billingItemRequestDTO) {
        ValidatorHelper.validateObject(billingItemRequestDTO); // validate the request object
        Organization organization = organizationMasterRepository.findByIdAndDefunct(billingItemRequestDTO.getOrganization(), false) // validate the organization
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        BillingItemGroup billingItemGroup = billingItemGroupRepository.findByIdAndDefunct(billingItemRequestDTO.getItemGroupId(), false) // validate the billing item group
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ITEM_NOT_FOUND, HttpStatus.NOT_FOUND));
        Speciality speciality = specialityRepository.findByIdAndDefunct(billingItemRequestDTO.getDepartmentId(), false) // validate the speciality
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.DEPARTMENT_NOT_FOUND, HttpStatus.NOT_FOUND));
        if (billingItemRequestDTO.getItemGroupId() != null && // validate the billing item group id
                !billingItemGroupRepository.existsById(billingItemRequestDTO.getItemGroupId())) { // validate the billing item group id
            throw new GlobalMasterException(ErrorConstant.GRP_ID_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        BillingItem billingItem = ObjectMapperUtil.copyObject(billingItemRequestDTO, BillingItem.class); // convert the request object to billing item object
        billingItem.setOrganization(organization);
        billingItem.setBillingItemGroup(billingItemGroup);
        billingItem.setSpeciality(speciality);
        billingItem.setPercentage(billingItemRequestDTO.getPercentage());
        billingItem.setDefunct(false);
        BillingItem saved = billingItemRepository.save(billingItem); // save the billing item
        logger.info("Billing Item created successfully");

        BillingItemDTO dto = ObjectMapperUtil.copyObject(saved, BillingItemDTO.class); // convert the billing item object to dto
        dto.setOrganizationDTO(ObjectMapperUtil.copyObject(saved.getOrganization(), OrganizationDTO.class));
        BillingItemGroupDTO billingItemGroupDTO = ObjectMapperUtil.copyObject(saved.getBillingItemGroup(), BillingItemGroupDTO.class); // convert the billing item group object to dto
        billingItemGroupDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(billingItemGroup.getOrganization(), OrganizationDTO.class));
        dto.setBillingItemGroupDTO(billingItemGroupDTO);
        SpecialityDTO specialityDTO = ObjectMapperUtil.copyObject(saved.getSpeciality(), SpecialityDTO.class); // convert the speciality object to dto
        specialityDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(speciality.getOrganization(), OrganizationDTO.class));
        dto.setSpecialityDTO(specialityDTO);

        return dto; // return the dto
    }

    // update billing item
    @Override
    public BillingItemDTO updateBillingItem(String id, BillingItemReqDTO billingItemRequestDTO) {
        ValidatorHelper.ValidateAllObject(id, billingItemRequestDTO); // validate the request object
        Organization organization = organizationMasterRepository.findByIdAndDefunct(billingItemRequestDTO.getOrganization(), false) // validate the organization
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        BillingItemGroup billingItemGroup = billingItemGroupRepository.findByIdAndDefunct(billingItemRequestDTO.getItemGroupId(), false) // validate the billing item group
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ITEM_NOT_FOUND, HttpStatus.NOT_FOUND));
        Speciality speciality = specialityRepository.findByIdAndDefunct(billingItemRequestDTO.getDepartmentId(), false) // validate the speciality
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.DEPARTMENT_NOT_FOUND, HttpStatus.NOT_FOUND));
        BillingItem existingBillingItem = billingItemRepository.findByIdAndDefunct(id, false) // validate the billing item
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ITEM_NOT_FOUND, HttpStatus.NOT_FOUND));
        if (billingItemRequestDTO.getItemGroupId() != null && // validate the billing item group id
                !billingItemGroupRepository.existsById(billingItemRequestDTO.getItemGroupId())) { // validate the billing item group id
            throw new GlobalMasterException(ErrorConstant.GRP_ID_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        BillingItem billingItem = ObjectMapperUtil.copyObject(billingItemRequestDTO, BillingItem.class); // convert the request object to billing item object
        billingItem.setOrganization(organization);
        billingItem.setBillingItemGroup(billingItemGroup);
        billingItem.setSpeciality(speciality);
        billingItem.setDefunct(false);
        ObjectMapperUtil.safeCopyObjectAndIgnore(billingItemRequestDTO, existingBillingItem, List.of("id", "defunct", "organizationId")); // update the billing item
        BillingItem updatedBillingItem = billingItemRepository.save(billingItem); // save the billing item
        logger.info("Billing Item updated successfully");
        BillingItemDTO dto = ObjectMapperUtil.copyObject(updatedBillingItem, BillingItemDTO.class); // convert the billing item object to dto
        dto.setOrganizationDTO(ObjectMapperUtil.copyObject(updatedBillingItem.getOrganization(), OrganizationDTO.class));
        BillingItemGroupDTO billingItemGroupDTO = ObjectMapperUtil.copyObject(updatedBillingItem.getBillingItemGroup(), BillingItemGroupDTO.class); // convert the billing item group object to dto
        billingItemGroupDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(billingItemGroup.getOrganization(), OrganizationDTO.class));
        dto.setBillingItemGroupDTO(billingItemGroupDTO);
        SpecialityDTO specialityDTO = ObjectMapperUtil.copyObject(updatedBillingItem.getSpeciality(), SpecialityDTO.class); // convert the speciality object to dto
        specialityDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(speciality.getOrganization(), OrganizationDTO.class));
        dto.setSpecialityDTO(specialityDTO);
        dto.setPercentage(billingItemRequestDTO.getPercentage());
        return dto; // return the dto
    }

    // get billing item by id
    @Override
    public BillingItemDTO getBillingItemById(String id) {
        ValidatorHelper.validateObject(id); // validate the request object
        BillingItem item = billingItemRepository.findByIdAndDefunct(id, false) // validate the billing item
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ITEM_NOT_FOUND, HttpStatus.NOT_FOUND));
        logger.info("Billing Item fetched successfully");
        BillingItemDTO dto = ObjectMapperUtil.copyObject(item, BillingItemDTO.class); // convert the billing item object to dto
        dto.setOrganizationDTO(ObjectMapperUtil.copyObject(item.getOrganization(), OrganizationDTO.class));
        BillingItemGroupDTO billingItemGroupDTO = ObjectMapperUtil.copyObject(item.getBillingItemGroup(), BillingItemGroupDTO.class); // convert the billing item group object to dto
        billingItemGroupDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(item.getBillingItemGroup().getOrganization(), OrganizationDTO.class));
        dto.setBillingItemGroupDTO(billingItemGroupDTO);
        SpecialityDTO specialityDTO = ObjectMapperUtil.copyObject(item.getSpeciality(), SpecialityDTO.class); // convert the speciality object to dto
        specialityDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(item.getSpeciality().getOrganization(), OrganizationDTO.class));
        dto.setSpecialityDTO(specialityDTO);
        return dto; // return the dto
    }

    // get all billing items
    @Override
    public List<BillingItemDTO> getAllBillingItems(String organizationId) {
        logger.info("Fetching all billing items for organization ID: {}", organizationId);
        Organization organization = organizationMasterRepository.findByIdAndDefunct(organizationId, false) // validate the organization
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        List<BillingItem> billingItems = billingItemRepository.findByAllDefunct(organization.getId(), false); // get all billing items
        return billingItems.stream() // convert the billing item object to dto
                .map(billingItem -> { // convert the billing item object to dto
                    BillingItemDTO dto = ObjectMapperUtil.copyObject(billingItem, BillingItemDTO.class); // convert the billing item object to dto
                    if (billingItem.getOrganization() != null) { // validate the organization
                        dto.setOrganizationDTO(ObjectMapperUtil.copyObject(billingItem.getOrganization(), OrganizationDTO.class));
                    }
                    if (billingItem.getBillingItemGroup() != null) { // validate the billing item group
                        BillingItemGroupDTO billingItemGroupDTO = ObjectMapperUtil.copyObject(billingItem.getBillingItemGroup(), BillingItemGroupDTO.class); // convert the billing item group object to dto
                        if (billingItem.getBillingItemGroup().getOrganization() != null) { // validate the organization
                            billingItemGroupDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(billingItem.getBillingItemGroup().getOrganization(), OrganizationDTO.class)); // convert the organization object to dto
                        }
                        dto.setBillingItemGroupDTO(billingItemGroupDTO);
                    }
                    // speciality
                    if (billingItem.getSpeciality() != null) { // validate the speciality
                        SpecialityDTO specialityDTO = ObjectMapperUtil.copyObject(billingItem.getSpeciality(), SpecialityDTO.class); // convert the speciality object to dto
                        if (billingItem.getSpeciality().getOrganization() != null) { // validate the organization
                            specialityDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(billingItem.getSpeciality().getOrganization(), OrganizationDTO.class)); // convert the organization object to dto
                        }
                        dto.setSpecialityDTO(specialityDTO);
                    }
                    return dto; // return the dto
                })
                .toList();
    }

    // delete billing item
    @Override
    public void deleteBillingItem(String id) {
        ValidatorHelper.ValidateAllObject(id); // validate the request object
        BillingItem billingItem = billingItemRepository.findByIdAndDefunct(id, false) // validate the billing item
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ITEM_NOT_FOUND, HttpStatus.NOT_FOUND));
        billingItem.setDefunct(true); // set the defunct flag to true
        billingItemRepository.save(billingItem); // save the billing item
        logger.info("Billing Item deleted successfully with ID: {}", id);
    }

    @Override
    public List<BillingItemDTO> getAllBillingItemsByItemGrp(String organizationId, String itemGrpId) {
        logger.info("Fetching all billing items for organization ID: {}", organizationId);
        Organization organization = organizationMasterRepository.findByIdAndDefunct(organizationId, false) // validate the organization
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        BillingItemGroup billingItemGroup = billingItemGroupRepository.findByIdAndDefunct(itemGrpId, false) // validate the billing item group
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ITEM_GROUP_NOT_FOUND, HttpStatus.NOT_FOUND));
        List<BillingItem> billingItems = billingItemRepository.findByItemGrpAllDefunct(organization.getId(), billingItemGroup.getId(), false); // get all billing items
        return billingItems.stream() // convert the billing item object to dto
                .map(billingItem -> { // convert the billing item object to dto
                    BillingItemDTO dto = ObjectMapperUtil.copyObject(billingItem, BillingItemDTO.class); // convert the billing item object to dto
                    if (billingItem.getOrganization() != null) { // validate the organization
                        dto.setOrganizationDTO(ObjectMapperUtil.copyObject(billingItem.getOrganization(), OrganizationDTO.class));
                    }
                    if (billingItem.getBillingItemGroup() != null) { // validate the billing item group
                        BillingItemGroupDTO billingItemGroupDTO = ObjectMapperUtil.copyObject(billingItem.getBillingItemGroup(), BillingItemGroupDTO.class); // convert the billing item group object to dto
                        if (billingItem.getBillingItemGroup().getOrganization() != null) { // validate the organization
                            billingItemGroupDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(billingItem.getBillingItemGroup().getOrganization(), OrganizationDTO.class)); // convert the organization object to dto
                        }
                        dto.setBillingItemGroupDTO(billingItemGroupDTO);
                    }
                    // speciality
                    if (billingItem.getSpeciality() != null) { // validate the speciality
                        SpecialityDTO specialityDTO = ObjectMapperUtil.copyObject(billingItem.getSpeciality(), SpecialityDTO.class); // convert the speciality object to dto
                        if (billingItem.getSpeciality().getOrganization() != null) { // validate the organization
                            specialityDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(billingItem.getSpeciality().getOrganization(), OrganizationDTO.class)); // convert the organization object to dto
                        }
                        dto.setSpecialityDTO(specialityDTO);
                    }
                    return dto; // return the dto
                })
                .toList();
    }
}
