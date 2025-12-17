package com.org.hosply360.dto.globalMasterDTO;


import com.org.hosply360.dao.globalMaster.TestParameterMaster;
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
public class TestDTO {
    private String id;
    private OrganizationDTO organizationDTO;
    private String name;
    private Long amount;
     private List<TestParameterMaster> testParameterMasters;
    private Boolean defunct;

}
