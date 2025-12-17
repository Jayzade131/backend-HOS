package com.org.hosply360.controller.globalMaster;

import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.dto.globalMasterDTO.BillingItemDTO;
import com.org.hosply360.dto.globalMasterDTO.BillingItemReqDTO;
import com.org.hosply360.service.globalMaster.BillingItemService;
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
@RequestMapping(EndpointConstants.BILLING_ITEM_API)
@RequiredArgsConstructor
public class BillingItemController {
    private final BillingItemService billingItemService;
    private static final Logger logger = LoggerFactory.getLogger(BillingItemController.class);

    @PostMapping(EndpointConstants.BILLING_ITEM)
    public ResponseEntity<AppResponseDTO> create(@RequestBody BillingItemReqDTO requestDTO) {
        logger.info("Creating Billing Item");
        BillingItemDTO dto = billingItemService.createBillingItem(requestDTO);
        return ResponseEntity.ok(AppResponseDTO.ok(dto));
    }

    @PutMapping(EndpointConstants.BILLING_ITEM)
    public ResponseEntity<AppResponseDTO> update(@RequestBody BillingItemReqDTO requestDTO) {
        logger.info("Updating Billing Item with ID: {}", requestDTO.getId());
        BillingItemDTO dto = billingItemService.updateBillingItem(requestDTO.getId(), requestDTO);
        return ResponseEntity.ok(AppResponseDTO.ok(dto));
    }

    @GetMapping(EndpointConstants.BILLING_ITEM_BY_ID)
    public ResponseEntity<AppResponseDTO> getById(@PathVariable String id) {
        logger.info("Fetching Billing Item by ID");
        return ResponseEntity.ok(AppResponseDTO.ok(billingItemService.getBillingItemById(id)));
    }

    @GetMapping(EndpointConstants.GET_ALL_BILLING_ITEMS)
    public ResponseEntity<AppResponseDTO> getAllBillingItem(@PathVariable String organizationId) {
        logger.info("Fetching all billing item groups for organization ID: {}", organizationId);
        return ResponseEntity.ok(AppResponseDTO.ok(billingItemService.getAllBillingItems(organizationId)));

    }

    @DeleteMapping(EndpointConstants.BILLING_ITEM_BY_ID)
    public ResponseEntity<AppResponseDTO> delete(@PathVariable String id) {
        logger.info("Deleting Billing Item with ID: {}"+ id);
        billingItemService.deleteBillingItem(id);
        return ResponseEntity.ok(AppResponseDTO.customStatus(ApplicationConstant.DELETED_SUCCESSFULLY, HttpStatus.OK));
    }

    @GetMapping(EndpointConstants.GET_ALL_BILLING_ITEMS_BY_ITEM_GRP)
    public ResponseEntity<AppResponseDTO> getAllBillingItemByItemGrp(@PathVariable String organizationId, @PathVariable String itemGrpId) {
        logger.info("Fetching all billing item groups for organization ID: {}", organizationId);
        return ResponseEntity.ok(AppResponseDTO.ok(billingItemService.getAllBillingItemsByItemGrp(organizationId, itemGrpId)));
    }
}


