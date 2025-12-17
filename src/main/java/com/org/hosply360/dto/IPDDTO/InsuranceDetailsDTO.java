package com.org.hosply360.dto.IPDDTO;

import com.org.hosply360.constant.Enums.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsuranceDetailsDTO {

    private String insuranceProviderId;
    private String insuranceName;
    private String insuranceNo;
    private ApprovalStatus approval;
}
