package com.org.hosply360.dto.globalMasterDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageEReqDTO {

    private String id;
    private String organization;
    private String packageName;
    private String billingGrpId;
    private List<String> testId;
    private Boolean defunct;


}
