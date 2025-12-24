package com.org.hosply360.service.pathology;

import com.org.hosply360.dto.pathologyDTO.GetResPacTestReportDTO;
import com.org.hosply360.dto.pathologyDTO.GetResTestReportDTO;
import com.org.hosply360.dto.pathologyDTO.PackageTestReportDTO;
import com.org.hosply360.dto.pathologyDTO.PackageTestReportPdfResponseDTO;
import com.org.hosply360.dto.pathologyDTO.PackageTestReportReqDTO;
import com.org.hosply360.dto.pathologyDTO.PagedResultForTest;
import com.org.hosply360.dto.pathologyDTO.TestReportDTO;
import com.org.hosply360.dto.pathologyDTO.TestReportPdfResponseDTO;
import com.org.hosply360.dto.pathologyDTO.TestReportReqDTO;

import java.time.LocalDate;

public interface TestReportService {
    String createTestReport(TestReportReqDTO testReportReqDTO);

    String updateTestReport(TestReportReqDTO testReportReqDTO);

    String createPackageTestReport(PackageTestReportReqDTO dto);

    String updatePackageTestReport(PackageTestReportReqDTO dto);


    TestReportDTO getTestReportById(String reportId);

    PackageTestReportDTO getPackageTestReportById(String packageReportId);

    PagedResultForTest<GetResTestReportDTO> getTestReports(String orgId, LocalDate fromDate, LocalDate toDate, String pId, String mobileNo, int page, int size);

    PagedResultForTest<GetResPacTestReportDTO> getPackageTestReports(String orgId, LocalDate fromDate, LocalDate toDate, String pId, String mobileNo, int page, int size);


    TestReportPdfResponseDTO getTestReportPdfResponseDTO(String testReportId);

    PackageTestReportPdfResponseDTO getPackageTestReportPdfResponseDTO(String packageTestReportId);
}
