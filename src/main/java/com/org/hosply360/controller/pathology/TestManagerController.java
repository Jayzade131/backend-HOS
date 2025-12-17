package com.org.hosply360.controller.pathology;

import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.constant.Enums.TestStatus;
import com.org.hosply360.dto.OPDDTO.PdfResponseDTO;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.dto.pathologyDTO.GetReqTestMangerDTO;
import com.org.hosply360.dto.pathologyDTO.GetResTestManagerDTO;
import com.org.hosply360.dto.pathologyDTO.PagedResultForTest;
import com.org.hosply360.dto.pathologyDTO.TestManagerReqDTO;
import com.org.hosply360.service.pathology.TestManagerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(EndpointConstants.TEST_PATHOLOGY_API)
@RequiredArgsConstructor
public class TestManagerController {
    private static final Logger logger = LoggerFactory.getLogger(TestManagerController.class);
    private final TestManagerService testManagerService;

    @PostMapping(EndpointConstants.TEST_MANAGER)
    public ResponseEntity<AppResponseDTO> createTestManager(@RequestBody TestManagerReqDTO testManagerReqDTO) {
        return ResponseEntity.ok(AppResponseDTO.ok(testManagerService.createTestManager(testManagerReqDTO)));
    }

    @PutMapping(EndpointConstants.TEST_MANAGER)
    public ResponseEntity<AppResponseDTO> updateTestManager(
            @RequestParam String id,
            @RequestParam TestStatus status
    ) {
        logger.info("Updating test manager with ID: {}", id);
        return ResponseEntity.ok(AppResponseDTO.ok(testManagerService.updateTestManager(id , status)));
    }

    @PostMapping(EndpointConstants.GET_TEST_MANAGER)
    public ResponseEntity<AppResponseDTO> getTestManagersByDateRangeAndStatus(
            @RequestBody GetReqTestMangerDTO getReqTestMangerDTO) {

        PagedResultForTest<?> result = testManagerService.getTestManagers(getReqTestMangerDTO);

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


    @PostMapping(EndpointConstants.TEST_MANAGER_PAYMENT)
    public ResponseEntity<AppResponseDTO> testManagerPayment(
            @RequestParam String testManagerId,
            @RequestParam Double amount) {
        logger.info("Processing payment for test manager with ID: {}", testManagerId);
        return ResponseEntity.ok(AppResponseDTO.ok(testManagerService.testManagerPayment(testManagerId, amount)));
    }

    @GetMapping(EndpointConstants.DOWNLOAD_TEST_MANAGER_BILL)
    public ResponseEntity<AppResponseDTO> downloadTestReportPDF(@RequestParam String testManagerId) {
        PdfResponseDTO pdfResponseDTO = testManagerService.generateTestManagerPdf(testManagerId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(AppResponseDTO.ok(pdfResponseDTO));
    }
}
