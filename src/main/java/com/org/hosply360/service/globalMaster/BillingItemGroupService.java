package com.org.hosply360.service.globalMaster;

import com.org.hosply360.dto.globalMasterDTO.BillingItemGroupDTO;
import com.org.hosply360.dto.globalMasterDTO.BillingItemGroupReqDTO;

import java.util.List;

public interface BillingItemGroupService {
    BillingItemGroupDTO createBillingItemGroup(BillingItemGroupReqDTO billingItemGroupDTO);

    BillingItemGroupDTO updateBillingItemGroup(String id, BillingItemGroupReqDTO billingItemGroupDTO);
    BillingItemGroupDTO getBillingItemGroupById(String id);

    List<BillingItemGroupDTO> getAllBillingItemGroups(String organizationId);
    void deleteBillingItemGroup(String id);


}
