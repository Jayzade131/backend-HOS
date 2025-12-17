package com.org.hosply360.service.globalMaster;

import com.org.hosply360.dto.globalMasterDTO.LanguageDTO;
import com.org.hosply360.dto.globalMasterDTO.LanguageReqDTO;

import java.util.List;

public interface LanguageMasterService {

    LanguageDTO createLanguage(LanguageReqDTO dto);


    List<LanguageDTO> getAllLanguages(String organizationId);

    LanguageDTO getLanguageById(String id);

    LanguageDTO updateLanguage(String id,LanguageReqDTO dto);

    void deleteLanguageById(String id);



}
