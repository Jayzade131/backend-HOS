package com.org.hosply360.service.globalMaster;

import com.org.hosply360.dto.globalMasterDTO.WardMasterDto;

import java.util.List;

public interface WardMasterService {

    WardMasterDto createWard(WardMasterDto dto);

    List<WardMasterDto> getAllWards(String orgId);

    WardMasterDto getWardById(String orgId, String id);

    WardMasterDto updateWard(WardMasterDto dto);

    void deleteWardById(String id);
}
