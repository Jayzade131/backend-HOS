package com.org.hosply360.dto.IPDDTO;

import com.org.hosply360.constant.Enums.Diet;
import com.org.hosply360.constant.Enums.DietTime;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class IPDDeitDTO {
    private String id;
    private String organization;
    private String ipdAdmission;
    private LocalDateTime dateTime;
    private DietTime dietTime;
    private String time;
    private Diet diet;
    private String remark;
    private Boolean defunct;
}
