package com.org.hosply360.service.globalMaster;

import com.org.hosply360.dto.globalMasterDTO.WardBedMasterDto;

import java.util.List;

public interface WardBedMasterService {

    WardBedMasterDto create(WardBedMasterDto dto);
    WardBedMasterDto update(WardBedMasterDto dto);
    List<WardBedMasterDto> getAllByWard(String orgId, String wardId);
    WardBedMasterDto getById(String id);
    void delete(String id);

    List<WardBedMasterDto> getAllAvilableBedsByWard(String orgId, String wardId);
}
