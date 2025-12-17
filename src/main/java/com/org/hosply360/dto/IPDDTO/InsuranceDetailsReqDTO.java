package com.org.hosply360.dto.IPDDTO;
import com.org.hosply360.constant.Enums.ApprovalStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsuranceDetailsReqDTO {
    private String insuranceProviderId;
    private String insuranceNo;
    private ApprovalStatus approval;
}