package com.org.hosply360.dto.globalMasterDTO;

import com.org.hosply360.constant.Enums.TemplateStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class TemplateReqDto {
    private String id;
    private String organizationId;
    private String templateName;
    private String design;
    private TemplateStatus templateStatus;
    private boolean defunct;
}
