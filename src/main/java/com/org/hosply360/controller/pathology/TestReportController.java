package com.org.hosply360.controller.pathology;

import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.OPDDTO.PdfResponseDTO;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.dto.pathologyDTO.PackageTestReportReqDTO;
import com.org.hosply360.dto.pathologyDTO.PagedResultForTest;
import com.org.hosply360.dto.pathologyDTO.TestReportReqDTO;
import com.org.hosply360.service.pathology.TestReportService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping(EndpointConstants.TEST_PATHOLOGY_API)
@RequiredArgsConstructor
public class TestReportController {
    private final TestReportService testReportService;
    private static final Logger logger = LoggerFactory.getLogger(TestReportController.class);

    @PostMapping(EndpointConstants.TEST_REPORT)
    public ResponseEntity<AppResponseDTO> createTestReport(@RequestBody TestReportReqDTO testReportReqDTO) {
        return ResponseEntity.ok(AppResponseDTO.ok(testReportService.createTestReport(testReportReqDTO)));
    }
    @PutMapping(EndpointConstants.TEST_REPORT)
    public ResponseEntity<AppResponseDTO> updateTestReport(@RequestBody TestReportReqDTO testReportReqDTO) {
        return ResponseEntity.ok(AppResponseDTO.ok( testReportService.updateTestReport(testReportReqDTO)));
    }
    @PostMapping(EndpointConstants.PACKAGE_TEST_REPORT)
    public ResponseEntity<AppResponseDTO> createPackageTestReport(@RequestBody PackageTestReportReqDTO testReportReqPackageDTO) {
        return ResponseEntity.ok(AppResponseDTO.ok(testReportService.createPackageTestReport(testReportReqPackageDTO)));
    }
    @PutMapping(EndpointConstants.PACKAGE_TEST_REPORT)
    public ResponseEntity<AppResponseDTO> updatePackageTestReport(@RequestBody PackageTestReportReqDTO testReportReqPackageDTO) {
        return ResponseEntity.ok(AppResponseDTO.ok(testReportService.updatePackageTestReport(testReportReqPackageDTO)));
    }
    @GetMapping(EndpointConstants.TEST_REPORT_BY_ID)
    public ResponseEntity<AppResponseDTO> getTestReportById(@PathVariable String testReportId) {
        logger.info("Fetching Test Report by ID");
        return ResponseEntity.ok(AppResponseDTO.ok(testReportService.getTestReportById(testReportId)));
    }
    @GetMapping(EndpointConstants.PACKAGE_TEST_REPORT_ID)
    public ResponseEntity<AppResponseDTO> getPackageTestReportById(@PathVariable String packageTestReportId) {
        logger.info("Fetching Package Test Report by ID");
        return ResponseEntity.ok(AppResponseDTO.ok(testReportService.getPackageTestReportById(packageTestReportId)));
    }
    @GetMapping(EndpointConstants.GET_TEST_REPORT)
    public ResponseEntity<AppResponseDTO> getTestReports(
            @RequestParam String orgId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String pId,
            @RequestParam(required = false) String mobileNo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResultForTest<?> result = testReportService.getTestReports(orgId,fromDate,toDate,pId,mobileNo,page,size);
        long totalPages = (long) Math.ceil((double) result.getTotal() / result.getSize());

        return ResponseEntity.ok(
                AppResponseDTO.getOk(
                        result.getData(),
                        result.getTotal(),
                        totalPages,
                        result.getPage()
                )
        );
    }

    @GetMapping(EndpointConstants.GET_PACKAGE_TEST_REPORT)
    public ResponseEntity<AppResponseDTO> getPackageTestReports(
            @RequestParam String orgId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String pId,
            @RequestParam(required = false) String mobileNo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResultForTest<?> result = testReportService.getPackageTestReports(orgId,fromDate,toDate,pId,mobileNo,page,size);
        long totalPages = (long) Math.ceil((double) result.getTotal() / result.getSize());

        return ResponseEntity.ok(
                AppResponseDTO.getOk(
                        result.getData(),
                        result.getTotal(),
                        totalPages,
                        result.getPage()
                )
        );    }

    @GetMapping(EndpointConstants.DOWNLOAD_TEST_REPORT)
    public ResponseEntity<AppResponseDTO> downloadTestReportPDF(@RequestParam String testReportId) {
        PdfResponseDTO pdfResponseDTO = testReportService.generateTestReportPdf(testReportId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(AppResponseDTO.ok(pdfResponseDTO));
    }
    @GetMapping(EndpointConstants.DOWNLOAD_PACKAGE_TEST_REPORT)
    public ResponseEntity<AppResponseDTO> downloadPackageTestReportPDF(
            @RequestParam String packageTestReportId) {
        PdfResponseDTO pdfResponseDTO = testReportService.generatePackageTestReportPdf(packageTestReportId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(AppResponseDTO.ok(pdfResponseDTO));
    }
}




