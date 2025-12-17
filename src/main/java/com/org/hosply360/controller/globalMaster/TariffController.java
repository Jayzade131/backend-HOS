package com.org.hosply360.controller.globalMaster;

import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.dto.globalMasterDTO.TariffDTO;
import com.org.hosply360.dto.globalMasterDTO.TariffReqDTO;
import com.org.hosply360.service.globalMaster.TariffMasterService;
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

import java.util.List;

@RestController
@RequestMapping(EndpointConstants.GLOBAL_MASTER_API)
@RequiredArgsConstructor
public class TariffController {

    private static final Logger logger = LoggerFactory.getLogger(TariffController.class);
    private final TariffMasterService tariffMasterService;

    @PostMapping(EndpointConstants.TARIFF_API)
    public ResponseEntity<AppResponseDTO> createTariff(@RequestBody TariffReqDTO tariffDTO) {
        logger.info("Creating tariff with name: {}", tariffDTO.getName());
        TariffDTO created = tariffMasterService.createTariff(tariffDTO);
        return ResponseEntity.ok(AppResponseDTO.ok(created));
    }

    @GetMapping(EndpointConstants.GET_TARIFF_API)
    public ResponseEntity<AppResponseDTO> getAllTariffs(@PathVariable String orgId) {
        logger.info("Fetching all tariffs for organization ID: {}", orgId);
        List<TariffDTO> tariffs = tariffMasterService.getAllTariffs(orgId);
        return ResponseEntity.ok(AppResponseDTO.ok(tariffs));
    }

    @GetMapping(EndpointConstants.TARIFFS_API_BY_ID)
    public ResponseEntity<AppResponseDTO> getTariffById(@PathVariable String id) {
        logger.info("Fetching tariff with ID: {}", id);
        return ResponseEntity.ok(AppResponseDTO.ok(tariffMasterService.getTariffById(id)));
    }

    @PutMapping(EndpointConstants.TARIFF_API)
    public ResponseEntity<AppResponseDTO> updateTariff(@RequestBody TariffReqDTO tariffDTO) {
        logger.info("Updating tariff with ID: {}", tariffDTO.getId());
        TariffDTO updated = tariffMasterService.updateTariff(tariffDTO.getId(),tariffDTO);
        return ResponseEntity.ok(AppResponseDTO.ok(updated));
    }

    @DeleteMapping(EndpointConstants.TARIFFS_API_BY_ID)
    public ResponseEntity<AppResponseDTO> deleteTariffById(@PathVariable String id) {
        logger.info("Deleting tariff with ID: {}", id);
        tariffMasterService.deleteTariffById(id);
        return ResponseEntity.ok(AppResponseDTO.customStatus(ApplicationConstant.DELETED_SUCCESSFULLY, HttpStatus.OK));
    }
}
