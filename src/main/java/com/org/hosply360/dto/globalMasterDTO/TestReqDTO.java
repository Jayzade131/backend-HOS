package com.org.hosply360.dto.globalMasterDTO;

import com.org.hosply360.dao.globalMaster.TestParameterMaster;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TestReqDTO {
    private String id;
    private String organizationId;
    private String name;
    private Long amount;
    private List<TestParameterMaster> testParameterMasters;
    private Boolean defunct;
}
