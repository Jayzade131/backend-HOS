package com.org.hosply360.controller.globalMaster;

import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.dto.globalMasterDTO.TestDTO;
import com.org.hosply360.dto.globalMasterDTO.TestReqDTO;
import com.org.hosply360.service.globalMaster.TestService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(EndpointConstants.GLOBAL_MASTER_API)
@RequiredArgsConstructor
public class TestController {
    private final TestService testService;
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);


    @PostMapping(EndpointConstants.TEST)
    public ResponseEntity<AppResponseDTO> create(@RequestBody TestReqDTO requestDTO) {
        logger.info("Creating Test");
        TestDTO dto = testService.createTest(requestDTO);
        return ResponseEntity.ok(AppResponseDTO.ok(dto));
    }

    @PutMapping(EndpointConstants.TEST)
    public ResponseEntity<AppResponseDTO> update(@RequestBody TestReqDTO requestDTO) {
        logger.info("Updating Test with ID: {}", requestDTO.getId());
        TestDTO dto = testService.updateTest(requestDTO.getId(), requestDTO);
        return ResponseEntity.ok(AppResponseDTO.ok(dto));
    }

    @GetMapping(EndpointConstants.TEST_BY_ID)
    public ResponseEntity<AppResponseDTO> getById(@PathVariable String id) {
        logger.info("Fetching Test by ID");
        return ResponseEntity.ok(AppResponseDTO.ok(testService.getTestById(id)));
    }

    @GetMapping(EndpointConstants.GET_ALL_TESTS)
    public ResponseEntity<AppResponseDTO> getAllTests(@PathVariable String organizationId) {
        logger.info("Fetching all tests for organization ID: {}", organizationId);
        return ResponseEntity.ok(AppResponseDTO.ok(testService.getAllTests(organizationId)));
    }

    @DeleteMapping(EndpointConstants.TEST_BY_ID)
    public ResponseEntity<AppResponseDTO> delete(@PathVariable String id) {
        logger.info("Deleting Test with ID: {}", id);
        testService.deleteTest(id);
        return ResponseEntity.ok(AppResponseDTO.customStatus(ApplicationConstant.DELETED_SUCCESSFULLY, HttpStatus.OK));
    }


}
