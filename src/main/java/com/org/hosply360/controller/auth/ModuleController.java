package com.org.hosply360.controller.auth;

import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.dto.authDTO.ModuleDTO;
import com.org.hosply360.service.auth.ModuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.util.List;

@Slf4j
@RestController
@RequestMapping(EndpointConstants.GLOBAL_MASTER_API)
@RequiredArgsConstructor
public class ModuleController {

    private static final Logger logger = LoggerFactory.getLogger(ModuleController.class);
    private final ModuleService moduleService;

    @PostMapping(EndpointConstants.MODULE)
    public ResponseEntity<AppResponseDTO> createModule(@RequestBody ModuleDTO dto) {
        logger.info("Creating module  for module: {}", dto.getModuleName());
        ModuleDTO created = moduleService.createModule(dto);
        return ResponseEntity.ok(AppResponseDTO.ok(created));
    }

    @PutMapping(EndpointConstants.MODULE)
    public ResponseEntity<AppResponseDTO> updateModule(@RequestBody ModuleDTO dto) {
        ModuleDTO updated = moduleService.updateModule(dto);
        return ResponseEntity.ok(AppResponseDTO.ok(updated));
    }

    @GetMapping(EndpointConstants.MODULE_BY_ID)
    public ResponseEntity<AppResponseDTO> getModule(@PathVariable String id) {
        logger.info("Fetching module  with ID: {}", id);
        ModuleDTO result = moduleService.getModule(id);
        return ResponseEntity.ok(AppResponseDTO.ok(result));
    }

    @GetMapping(EndpointConstants.GET_ALL_MODULE)
    public ResponseEntity<AppResponseDTO> getAllModule(
            @RequestParam(defaultValue = ApplicationConstant.DEFAULT_VALUE_PAGE) int pageNumber,
            @RequestParam(defaultValue = ApplicationConstant.DEFAULT_VALUE_SIZE) int pageSize) {
        logger.info("Fetching all module ");
        List<ModuleDTO> list = moduleService.getAllModule(pageNumber, pageSize);
        return ResponseEntity.ok(AppResponseDTO.ok(list));
    }

    @DeleteMapping(EndpointConstants.MODULE_BY_ID)
    public ResponseEntity<AppResponseDTO> deleteModule(@PathVariable String id) {
        moduleService.deleteModule(id);
        return ResponseEntity.ok(AppResponseDTO.ok(ApplicationConstant.DELETED_SUCCESSFULLY));
    }
}
