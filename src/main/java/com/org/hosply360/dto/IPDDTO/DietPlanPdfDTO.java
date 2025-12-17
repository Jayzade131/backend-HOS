package com.org.hosply360.dto.IPDDTO;

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
public class DietPlanPdfDTO {
    private String mrdNo;
    private String ipdNo;
    private String admDate;
    private String consultant;
    private String referredBy;
    private String patientName;
    private String ageGender;
    private String mobileNo;
    private String address;
    private String remark;
    private String date;
    private String dietTime;
    private String diet;
    private String dietRemark;
}
