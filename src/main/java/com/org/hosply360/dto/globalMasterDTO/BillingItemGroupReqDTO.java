package com.org.hosply360.dto.globalMasterDTO;

import com.org.hosply360.constant.Enums.IPDBill;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingItemGroupReqDTO {
    private String id;
    private String organization;
    private String itemGroupName;
    private IPDBill includeInIPDBill;
    private Boolean defunct;
}
