package com.org.hosply360.controller.globalMaster;

import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.constant.Enums.TemplateStatus;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.dto.globalMasterDTO.TemplateReqDto;
import com.org.hosply360.service.globalMaster.TemplateService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(EndpointConstants.GLOBAL_MASTER_API)
public class TemplateController {
    private static final Logger logger = LoggerFactory.getLogger(TemplateController.class);
    private final TemplateService templateService;

    @PostMapping(EndpointConstants.TEMPLATE)
    public ResponseEntity<AppResponseDTO> createTemplate(@RequestBody TemplateReqDto dto) {
        return ResponseEntity.ok(AppResponseDTO.ok(templateService.createTemplate(dto)));
    }

    @PutMapping(EndpointConstants.TEMPLATE)
    public ResponseEntity<AppResponseDTO> updateTemplate(@RequestBody TemplateReqDto dto) {
        return ResponseEntity.ok(AppResponseDTO.ok(templateService.updateTemplate(dto)));
    }

    @GetMapping(EndpointConstants.TEMPLATE_BY_ID)
    public ResponseEntity<AppResponseDTO> getTemplaterById(@PathVariable String id) {
        return ResponseEntity.ok(AppResponseDTO.ok(templateService.getTemplaterById(id)));
    }

    @GetMapping(EndpointConstants.GET_ALL_TEMPLATE)
    public ResponseEntity<AppResponseDTO> getAllTemplate(@PathVariable String organizationId, @RequestParam(required = false) TemplateStatus templateStatus) {
        return ResponseEntity.ok(AppResponseDTO.ok(templateService.getAllTemplate(organizationId, templateStatus)));
    }


    @DeleteMapping(EndpointConstants.TEMPLATE_BY_ID)
    public ResponseEntity<AppResponseDTO> deleteTemplateById(@PathVariable String id) {
        templateService.deleteTemplateById(id);
        return ResponseEntity.ok(AppResponseDTO.customStatus(ApplicationConstant.DELETED_SUCCESSFULLY, HttpStatus.OK));
    }

}
