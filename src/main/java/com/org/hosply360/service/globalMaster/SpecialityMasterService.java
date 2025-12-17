package com.org.hosply360.service.globalMaster;

import com.org.hosply360.dto.globalMasterDTO.SpecialityDTO;
import com.org.hosply360.dto.globalMasterDTO.SpecialityReqDTO;

import java.util.List;

public interface SpecialityMasterService {

    SpecialityDTO createSpeciality(SpecialityReqDTO dto);

    List<SpecialityDTO> getAllSpeciality(String organizationId, String masterType);

    SpecialityDTO getSpecialityById(String id);

    SpecialityDTO updateSpeciality(String id,SpecialityReqDTO dto);

    void deleteSpeciality(String id);


}
