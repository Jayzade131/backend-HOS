package com.org.hosply360.service.globalMaster;

import com.org.hosply360.constant.Enums.TemplateStatus;
import com.org.hosply360.dto.globalMasterDTO.TemplateDto;
import com.org.hosply360.dto.globalMasterDTO.TemplateReqDto;

import java.util.List;

public interface TemplateService {

    TemplateDto createTemplate(TemplateReqDto templateReqDto);
    TemplateDto updateTemplate(TemplateReqDto templateReqDto);
    TemplateDto getTemplaterById(String id);
    List<TemplateDto> getAllTemplate(String organizationId, TemplateStatus templateStatus);
    void deleteTemplateById(String id);
}
