package com.org.hosply360.controller.globalMaster;

import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.dto.globalMasterDTO.CurrencyDTO;
import com.org.hosply360.dto.globalMasterDTO.CurrencyReqDTO;
import com.org.hosply360.service.globalMaster.CurrencyService;
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
public class CurrencyController {

    private final CurrencyService currencyService;
    private static final Logger logger = LoggerFactory.getLogger(CurrencyController.class);

    @PostMapping(EndpointConstants.CURRENCY)
    public ResponseEntity<AppResponseDTO> createCurrency(@RequestBody CurrencyReqDTO currencyDto) {
        logger.info("Creating currency with code: {}", currencyDto.getCode());
        CurrencyDTO created = currencyService.createCurrency(currencyDto);
        return ResponseEntity.ok(AppResponseDTO.ok(created));
    }

    @PutMapping(EndpointConstants.CURRENCY)
    public ResponseEntity<AppResponseDTO> updateCurrency(@RequestBody CurrencyReqDTO currencyDto) {
        CurrencyDTO updated = currencyService.updateCurrency(currencyDto.getId(), currencyDto);
        return ResponseEntity.ok(AppResponseDTO.ok(updated));
    }

    @GetMapping(EndpointConstants.CURRENCY_BY_ID)
    public ResponseEntity<AppResponseDTO> getCurrencyById(@PathVariable String id) {
        logger.info("Fetching currency with ID: {}", id);
        return ResponseEntity.ok(AppResponseDTO.ok(currencyService.getCurrencyById(id)));
    }

    @GetMapping(EndpointConstants.GET_ALL_CURRENCIES)
    public ResponseEntity<AppResponseDTO> getAllCurrencies(@PathVariable String organizationId) {
        logger.info("Fetching all billing item groups for organization ID: {}", organizationId);
        return ResponseEntity.ok(AppResponseDTO.ok(currencyService.getAllCurrencies(organizationId)));

    }

    @DeleteMapping(EndpointConstants.CURRENCY_BY_ID)
    public ResponseEntity<AppResponseDTO> deleteCurrencyById(@PathVariable String id) {
        currencyService.deleteCurrencyById(id);
        return ResponseEntity.ok(AppResponseDTO.customStatus(ApplicationConstant.DELETED_SUCCESSFULLY, HttpStatus.OK));
    }
}
