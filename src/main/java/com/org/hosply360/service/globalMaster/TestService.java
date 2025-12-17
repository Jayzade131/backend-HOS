package com.org.hosply360.service.globalMaster;


import com.org.hosply360.dto.globalMasterDTO.TestDTO;
import com.org.hosply360.dto.globalMasterDTO.TestReqDTO;

import java.util.List;

public interface TestService {

    TestDTO createTest(TestReqDTO testReqDTO);
    TestDTO updateTest(String id, TestReqDTO testReqDTO);
    TestDTO getTestById(String id);
    List<TestDTO> getAllTests(String organizationId);
    void deleteTest(String id);


}
