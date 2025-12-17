package com.org.hosply360.dto.IPDDTO;

import com.org.hosply360.constant.Enums.type;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
@Setter
@Getter
public class IPDDischargeFormReqDTO {
    private String id;
    private String organizationId;
    private String ipdAdmissionId;
    private String secondaryConsultant;
    private String thirdConsultant;
    private String templateId;
    private String remarks;
    private LocalDateTime dateTime;
    private type type;
    private String dischargeSummary;
    private Boolean defunct;
}
