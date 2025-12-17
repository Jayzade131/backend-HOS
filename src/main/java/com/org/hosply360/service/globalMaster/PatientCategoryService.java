package com.org.hosply360.service.globalMaster;

import com.org.hosply360.dto.globalMasterDTO.PatientCategoryDTO;
import com.org.hosply360.dto.globalMasterDTO.PatientCategoryReqDTO;

import java.util.List;

public interface PatientCategoryService {
    PatientCategoryDTO createPatientCategory(PatientCategoryReqDTO reqDTO);
    PatientCategoryDTO updatePatientCategory(String id, PatientCategoryReqDTO reqDTO);

    PatientCategoryDTO getPatientCategoryById(String id);

    List<PatientCategoryDTO> getAllPatientCategories(String organizationId);

    void deletePatientCategory(String id);
}
