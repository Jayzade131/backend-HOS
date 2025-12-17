package com.org.hosply360.controller.globalMaster;

import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.dto.globalMasterDTO.LanguageDTO;
import com.org.hosply360.dto.globalMasterDTO.LanguageReqDTO;
import com.org.hosply360.service.globalMaster.LanguageMasterService;
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
public class LanguageMasterController {

    private static final Logger logger = LoggerFactory.getLogger(LanguageMasterController.class);
    private final LanguageMasterService languageMasterService;

    @PostMapping(EndpointConstants.LANGUAGE_API)
    public ResponseEntity<AppResponseDTO> createLanguage(@RequestBody LanguageReqDTO languageDTO) {
        logger.info("Creating language with code: {}", languageDTO.getCode());
        LanguageDTO created = languageMasterService.createLanguage(languageDTO);
        return ResponseEntity.ok(AppResponseDTO.ok(created));
    }

    @GetMapping(EndpointConstants.GET_LANGUAGES_API)
    public ResponseEntity<AppResponseDTO> getAllLanguages(@PathVariable String organizationId) {
        logger.info("Fetching all billing item groups for organization ID: {}", organizationId);
        return ResponseEntity.ok(AppResponseDTO.ok(languageMasterService.getAllLanguages(organizationId)));

    }

    @GetMapping(EndpointConstants.LANGUAGE_API_BY_ID)
    public ResponseEntity<AppResponseDTO> getLanguageById(@PathVariable String id) {
        logger.info("Fetching language with ID: {}", id);
        return ResponseEntity.ok(AppResponseDTO.ok(languageMasterService.getLanguageById(id)));
    }

    @PutMapping(EndpointConstants.LANGUAGE_API)
    public ResponseEntity<AppResponseDTO> updateLanguage(@RequestBody LanguageReqDTO languageDTO) {
        LanguageDTO updated = languageMasterService.updateLanguage(languageDTO.getId(),languageDTO);
        return ResponseEntity.ok(AppResponseDTO.ok(updated));
    }

    @DeleteMapping(EndpointConstants.LANGUAGE_API_BY_ID)
    public ResponseEntity<AppResponseDTO> deleteLanguageById(@PathVariable String id) {
        languageMasterService.deleteLanguageById(id);
        return ResponseEntity.ok(AppResponseDTO.customStatus(ApplicationConstant.DELETED_SUCCESSFULLY, HttpStatus.OK));

    }
}
