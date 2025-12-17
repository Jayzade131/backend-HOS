package com.org.hosply360.dto.IPDDTO;

import com.org.hosply360.constant.Enums.ApprovalStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CorporateDetailsReqDTO {
    private String companyId;       // maps to CompanyMaster._id
    private ApprovalStatus approval;
}