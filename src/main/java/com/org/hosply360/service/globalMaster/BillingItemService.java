package com.org.hosply360.service.globalMaster;

import com.org.hosply360.dto.globalMasterDTO.BillingItemDTO;
import com.org.hosply360.dto.globalMasterDTO.BillingItemReqDTO;

import java.util.List;

public interface BillingItemService {
    BillingItemDTO createBillingItem(BillingItemReqDTO billingItemRequestDTO);
    BillingItemDTO updateBillingItem(String id, BillingItemReqDTO billingItemRequestDTO);
    BillingItemDTO getBillingItemById(String id);
    List<BillingItemDTO> getAllBillingItems(String organizationId);
    void deleteBillingItem(String id);
    List<BillingItemDTO> getAllBillingItemsByItemGrp(String organizationId, String itemGrpId);
}
