package com.org.hosply360.service.globalMaster;

import com.org.hosply360.dto.globalMasterDTO.TariffDTO;
import com.org.hosply360.dto.globalMasterDTO.TariffReqDTO;

import java.util.List;

public interface TariffMasterService {
    TariffDTO createTariff(TariffReqDTO tariffDTO);
    List<TariffDTO> getAllTariffs(String orgId);
    TariffDTO getTariffById(String id);
    TariffDTO updateTariff(String id,TariffReqDTO dto);
    void deleteTariffById(String id);
}
