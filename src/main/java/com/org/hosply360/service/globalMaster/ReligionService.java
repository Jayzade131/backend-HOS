package com.org.hosply360.service.globalMaster;

import com.org.hosply360.dto.globalMasterDTO.ReligionDTO;
import com.org.hosply360.dto.globalMasterDTO.ReligionReqDTO;

import java.util.List;

public interface ReligionService {
    ReligionDTO createReligion(ReligionReqDTO religionDto);
    ReligionDTO updateReligion(String id,ReligionReqDTO religionDto);
    ReligionDTO getReligionById(String id);
    List<ReligionDTO> getAllReligions(String organizationId);
    void deleteReligionById(String id);

}
