package com.org.hosply360.controller.globalMaster;

import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.dto.globalMasterDTO.BillingItemGroupReqDTO;
import com.org.hosply360.service.globalMaster.BillingItemGroupService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping(EndpointConstants.GLOBAL_MASTER_API)
@RequiredArgsConstructor
public class BillingItemGroupController {

    private final BillingItemGroupService billingItemGroupService;
    private static final Logger logger = LoggerFactory.getLogger(BillingItemGroupController.class);

    @PostMapping(EndpointConstants.BILLING_ITEM_GROUP)
    public ResponseEntity<AppResponseDTO> createBillingItemGroup(@RequestBody BillingItemGroupReqDTO billingItemGroupDTO) {
        logger.info("Creating billing item group with name: {}", billingItemGroupDTO.getItemGroupName());
        return ResponseEntity.ok(AppResponseDTO.ok(billingItemGroupService.createBillingItemGroup(billingItemGroupDTO)));
    }

    @PutMapping (EndpointConstants.BILLING_ITEM_GROUP)
    public ResponseEntity<AppResponseDTO> updateBillingItemGroup(@RequestBody BillingItemGroupReqDTO billingItemGroupDTO) {
        logger.info("Updating billing item group with name: {}", billingItemGroupDTO.getItemGroupName());
        return ResponseEntity.ok(AppResponseDTO.ok(billingItemGroupService.updateBillingItemGroup(billingItemGroupDTO.getId(), billingItemGroupDTO)));
    }
    @GetMapping(EndpointConstants.BILLING_ITEM_GROUP_BY_ID)
    public ResponseEntity<AppResponseDTO> getBillingItemGroupById(@PathVariable String id) {
        logger.info("Fetching billing item group with ID: {}", id);
        return ResponseEntity.ok(AppResponseDTO.ok(billingItemGroupService.getBillingItemGroupById(id)));
    }

    @GetMapping(EndpointConstants.GET_ALL_BILLING_ITEM_GROUPS)
    public ResponseEntity<AppResponseDTO> getAllBillingItemGroups(@PathVariable String organizationId) {
        logger.info("Fetching all billing item groups for organization ID: {}", organizationId);
        return ResponseEntity.ok(AppResponseDTO.ok(billingItemGroupService.getAllBillingItemGroups(organizationId)));
    }


    @DeleteMapping(EndpointConstants.BILLING_ITEM_GROUP_BY_ID)
    public ResponseEntity<AppResponseDTO> deleteBillingItemGroup(@PathVariable String id) {
        logger.info("Deleting billing item group with ID: {}", id);
        billingItemGroupService.deleteBillingItemGroup(id);
        return ResponseEntity.ok(AppResponseDTO.customStatus(ApplicationConstant.DELETED_SUCCESSFULLY, HttpStatus.OK));
    }
}