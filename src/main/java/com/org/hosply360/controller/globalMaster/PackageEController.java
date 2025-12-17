package com.org.hosply360.controller.globalMaster;

import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.dto.globalMasterDTO.PackageEReqDTO;
import com.org.hosply360.repository.globalMasterRepo.PackageERepository;
import com.org.hosply360.service.globalMaster.PackageEService;
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
public class PackageEController {
    private final PackageEService packageEService;
    private final PackageERepository packageERepository;
    private static final Logger logger = LoggerFactory.getLogger(PackageEController.class);


    @PostMapping(EndpointConstants.PACKAGE_API)
    public ResponseEntity<AppResponseDTO> createPackage(@RequestBody PackageEReqDTO packageReqDTO) {
        logger.info("Creating package with name: {}", packageReqDTO.getPackageName());
        return ResponseEntity.ok(AppResponseDTO.ok(packageEService.createPackage(packageReqDTO)));
    }
    @PutMapping(EndpointConstants.PACKAGE_API)
    public ResponseEntity<AppResponseDTO> updatePackage(@RequestBody PackageEReqDTO packageReqDTO) {
        logger.info("Updating package with name: {}", packageReqDTO.getPackageName());
        return ResponseEntity.ok(AppResponseDTO.ok(packageEService.updatePackage(packageReqDTO.getId(), packageReqDTO)));
    }
    @GetMapping(EndpointConstants.PACKAGE_API_BY_ID)
    public ResponseEntity<AppResponseDTO> getPackageById(@PathVariable String id) {
        logger.info("Fetching package with ID: {}", id);
        return ResponseEntity.ok(AppResponseDTO.ok(packageEService.getPackageById(id)));
    }

    @GetMapping(EndpointConstants.GET_ALL_PACKAGE_API)
    public ResponseEntity<AppResponseDTO> getAllPackages(@PathVariable String organizationId) {
        logger.info("Fetching all billing item groups for organization ID: {}", organizationId);
        return ResponseEntity.ok(AppResponseDTO.ok(packageEService.getAllPackage(organizationId)));

   }

    @DeleteMapping(EndpointConstants.PACKAGE_API_BY_ID)
    public ResponseEntity<AppResponseDTO> deletePackage(@PathVariable String id) {
        logger.info("Deleting package with ID: {}", id);
        packageEService.deletePackage(id);
        return ResponseEntity.ok(AppResponseDTO.customStatus(ApplicationConstant.DELETED_SUCCESSFULLY, HttpStatus.OK));

    }


}
