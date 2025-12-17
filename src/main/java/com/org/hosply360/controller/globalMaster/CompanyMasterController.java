package com.org.hosply360.controller.globalMaster;

import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.dto.globalMasterDTO.CompanyMasterDTO;
import com.org.hosply360.dto.globalMasterDTO.CompanyMasterReqDTO;
import com.org.hosply360.service.globalMaster.CompanyMasterService;
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
public class CompanyMasterController {
    private static final Logger logger = LoggerFactory.getLogger(CompanyMasterController.class);

    private final CompanyMasterService companyMasterService;

    @PostMapping(EndpointConstants.COMPANY)
    public ResponseEntity<AppResponseDTO> create(@RequestBody CompanyMasterReqDTO requestDTO) {
        logger.info("Creating Company");
        CompanyMasterDTO dto = companyMasterService.createCompany(requestDTO);
        return ResponseEntity.ok(AppResponseDTO.ok(dto));
    }

    @PutMapping(EndpointConstants.COMPANY)
    public ResponseEntity<AppResponseDTO> update(@RequestBody CompanyMasterReqDTO requestDTO) {
        logger.info("Updating Company with ID: {}", requestDTO.getId());
        CompanyMasterDTO dto = companyMasterService.updateCompany(requestDTO.getId(), requestDTO);
        return ResponseEntity.ok(AppResponseDTO.ok(dto));
    }

    @GetMapping(EndpointConstants.COMPANY_BY_ID)
    public ResponseEntity<AppResponseDTO> getById(@PathVariable String id) {
        logger.info("Fetching Company by ID: {}", id);
        CompanyMasterDTO dto = companyMasterService.getCompanyById(id);
        return ResponseEntity.ok(AppResponseDTO.ok(dto));
    }

    @GetMapping(EndpointConstants.GET_ALL_COMPANY)
    public ResponseEntity<AppResponseDTO> getAll(@PathVariable String organizationId) {
        logger.info("Fetching all Companies for Organization ID: {}", organizationId);
        return ResponseEntity.ok(AppResponseDTO.ok(companyMasterService.getAllCompanies(organizationId)));
    }

    @DeleteMapping(EndpointConstants.COMPANY_BY_ID)
    public ResponseEntity<AppResponseDTO> delete(@PathVariable String id) {
        logger.info("Deleting Companies with ID: {}", id);
        companyMasterService.deleteCompany(id);
        return ResponseEntity.ok(AppResponseDTO.customStatus(ApplicationConstant.DELETED_SUCCESSFULLY, HttpStatus.OK));
    }

}
