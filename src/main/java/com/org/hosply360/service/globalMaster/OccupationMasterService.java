package com.org.hosply360.service.globalMaster;

import com.org.hosply360.dto.globalMasterDTO.OccupationDTO;
import com.org.hosply360.dto.globalMasterDTO.OccupationReqDTO;

import java.util.List;

public interface OccupationMasterService {

    OccupationDTO createOccupation(OccupationReqDTO dto);

    List<OccupationDTO> getAllOccupations(String organizationId);

    OccupationDTO getOccupationById(String id);

    OccupationDTO updateOccupation(String id,OccupationReqDTO dto);

    void deleteOccupation(String id);



}
