package com.org.hosply360.service.globalMaster;

import com.org.hosply360.dto.globalMasterDTO.OrganizationDTO;
import com.org.hosply360.dto.globalMasterDTO.OrganizationReqDTO;

import java.util.List;

public interface OrganizationMasterService {
    String saveOrUpdateOrganization(OrganizationReqDTO organizationReqDTO);
    OrganizationDTO getOrganization(String id);
    List<OrganizationDTO> fetchAllOrganization();
}

