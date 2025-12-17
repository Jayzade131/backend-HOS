package com.org.hosply360.service.pathology;

import com.org.hosply360.constant.Enums.TestStatus;
import com.org.hosply360.dto.OPDDTO.PdfResponseDTO;
import com.org.hosply360.dto.pathologyDTO.GetReqTestMangerDTO;
import com.org.hosply360.dto.pathologyDTO.GetResTestManagerDTO;
import com.org.hosply360.dto.pathologyDTO.PagedResultForTest;
import com.org.hosply360.dto.pathologyDTO.TestManagerReqDTO;

public interface TestManagerService {

    String createTestManager(TestManagerReqDTO testManagerReqDTO);

    String updateTestManager(String id,TestStatus status);

    PagedResultForTest<GetResTestManagerDTO> getTestManagers(GetReqTestMangerDTO dto);

    String testManagerPayment(String TestManagerId, Double amount);

    PdfResponseDTO generateTestManagerPdf(String testManagerId);


}
