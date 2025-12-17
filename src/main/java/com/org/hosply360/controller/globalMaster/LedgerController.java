package com.org.hosply360.controller.globalMaster;


import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.dto.globalMasterDTO.LedgerDTO;
import com.org.hosply360.service.globalMaster.LedgerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequestMapping(EndpointConstants.GLOBAL_MASTER_API)
@RequiredArgsConstructor
public class LedgerController {

    private static final Logger logger = LoggerFactory.getLogger(LedgerController.class);
    private final LedgerService ledgerService;

    @PostMapping(EndpointConstants.LEDGER_API)
    public ResponseEntity<AppResponseDTO> createLedger(@RequestBody LedgerDTO ledgerDTO) {
        logger.info("Creating ledger with name: {}", ledgerDTO.getLedgerName());
        return ResponseEntity.ok(AppResponseDTO.ok(ledgerService.createLedger(ledgerDTO)));
    }

    @PutMapping(EndpointConstants.LEDGER_API)
    public ResponseEntity<AppResponseDTO> updateLedger(@RequestBody LedgerDTO ledgerDTO) {
        logger.info("Updating ledger with ID: {}", ledgerDTO.getId());
        return ResponseEntity.ok(AppResponseDTO.ok(ledgerService.updateLedger(ledgerDTO)));
    }

    @GetMapping(EndpointConstants.LEDGER_BY_ID)
    public ResponseEntity<AppResponseDTO> getLedgerById(@PathVariable String id) {
        return ResponseEntity.ok(AppResponseDTO.ok(ledgerService.getLedgerById(id)));
    }

    @GetMapping(EndpointConstants.GET_ALL_LEDGERS)
    public ResponseEntity<AppResponseDTO> getAllLedgers(@PathVariable String organizationId) {
        logger.info("Fetching all ledgers for organization ID: {}", organizationId);
        return ResponseEntity.ok(AppResponseDTO.ok(ledgerService.getAllLedger(organizationId)));
    }

        @DeleteMapping(EndpointConstants.LEDGER_BY_ID)
    public ResponseEntity<AppResponseDTO> deleteLedger(@PathVariable String id) {
        logger.info("Deleting (soft) ledger with ID: {}", id);
        ledgerService.deleteLedger(id);
            return ResponseEntity.ok(AppResponseDTO.customStatus(ApplicationConstant.DELETED_SUCCESSFULLY, HttpStatus.OK));
    }
}

