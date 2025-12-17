package com.org.hosply360.service.globalMaster;

import com.org.hosply360.dto.globalMasterDTO.InsuranceProviderDTO;
import com.org.hosply360.dto.globalMasterDTO.InsuranceProviderReqDTO;

import java.util.List;

public interface InsuranceProviderService {
    InsuranceProviderDTO createInsuranceProvider(InsuranceProviderReqDTO insuranceProviderDto);

    List<InsuranceProviderDTO> getAllInsuranceProviders(String organizationId);

    InsuranceProviderDTO getInsuranceProviderById(String id);

    InsuranceProviderDTO updateInsuranceProvider(String id,InsuranceProviderReqDTO insuranceProviderDto);


    void deleteInsuranceProviderById(String id);



}
