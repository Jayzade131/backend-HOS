package com.org.hosply360.dto.globalMasterDTO;

import com.org.hosply360.constant.Enums.TemplateStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemplateDto {

    private String id;
    private String organizationId;
    private String templateName;
    private String design;
    private TemplateStatus templateStatus;
    private boolean defunct;
}
