package com.org.hosply360.service.globalMaster;

import com.org.hosply360.dto.globalMasterDTO.ConfigurationDTO;
import com.org.hosply360.dto.globalMasterDTO.ConfigurationReqDTO;

import java.util.List;


public interface ConfigurationService {
    ConfigurationDTO createConfiguration(ConfigurationReqDTO dto);
    ConfigurationDTO updateConfiguration(String id, ConfigurationReqDTO dto);
    ConfigurationDTO getConfiguration(String id);
    List<ConfigurationDTO> getAllConfiguration(String organizationId);

    void deleteConfiguration(String id);
}