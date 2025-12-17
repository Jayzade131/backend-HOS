package com.org.hosply360.service.globalMaster;

import com.org.hosply360.dto.globalMasterDTO.CompanyMasterDTO;
import com.org.hosply360.dto.globalMasterDTO.CompanyMasterReqDTO;


import java.util.List;

public interface CompanyMasterService {
    CompanyMasterDTO createCompany(CompanyMasterReqDTO reqDTO);
    CompanyMasterDTO updateCompany(String id, CompanyMasterReqDTO reqDTO);

    CompanyMasterDTO getCompanyById(String id);

    List<CompanyMasterDTO> getAllCompanies(String organizationId);

    void deleteCompany(String id);
}
