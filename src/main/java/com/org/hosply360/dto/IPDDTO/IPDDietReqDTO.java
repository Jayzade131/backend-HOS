package com.org.hosply360.dto.IPDDTO;

import com.org.hosply360.constant.Enums.Diet;
import com.org.hosply360.constant.Enums.DietTime;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class IPDDietReqDTO {
    private String id;
    private String organizationId;
    private String ipdAdmissionId;
    private LocalDateTime dateTime;
    private DietTime dietTime;
    private String time;
    private Diet diet;
    private String remark;
    private Boolean defunct;
}
