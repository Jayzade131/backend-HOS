package com.org.hosply360.dao.IPD;

import com.org.hosply360.constant.Enums.ApprovalStatus;
import com.org.hosply360.dao.globalMaster.InsuranceProvider;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Getter
@Setter
@Builder
public class InsuranceDetails {

    @DBRef
    private InsuranceProvider insuranceName;
    private String insuranceNo;
    private ApprovalStatus approval;
}