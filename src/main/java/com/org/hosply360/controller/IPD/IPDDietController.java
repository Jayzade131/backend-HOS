package com.org.hosply360.controller.IPD;

import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.IPDDTO.DietPlanPdfResponseDTO;
import com.org.hosply360.dto.IPDDTO.IPDDietReqDTO;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.service.IPD.IPDDietService;
import lombok.RequiredArgsConstructor;
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
@RequestMapping(EndpointConstants.IPD_API)
@RequiredArgsConstructor
public class IPDDietController {
    private final IPDDietService ipdDietService;

    @PostMapping(EndpointConstants.IPD_DIET)
    public ResponseEntity<AppResponseDTO> createDiet(@RequestBody IPDDietReqDTO requestDTO) {
        return ResponseEntity.ok(AppResponseDTO.ok(ipdDietService.createDiet(requestDTO)));
    }

    @PutMapping(EndpointConstants.IPD_DIET)
    public ResponseEntity<AppResponseDTO> updateDiet(@RequestBody IPDDietReqDTO requestDTO) {
        return ResponseEntity.ok(AppResponseDTO.ok(ipdDietService.updateDiet(requestDTO)));
    }

    @GetMapping(EndpointConstants.IPD_DIET_BY_ID)
    public ResponseEntity<AppResponseDTO> getDietById(@PathVariable String id) {
        return ResponseEntity.ok(AppResponseDTO.ok(ipdDietService.getDietById(id)));
    }

    @GetMapping(EndpointConstants.IPD_DIET_BY_IPD_ADMISSION)
    public ResponseEntity<AppResponseDTO> getDietByIpdAdmission(@PathVariable String ipdAdmissionId) {
        return ResponseEntity.ok(AppResponseDTO.ok(ipdDietService.getAllDiet(ipdAdmissionId)));
    }

    @DeleteMapping(EndpointConstants.IPD_DIET_BY_ID)
    public ResponseEntity<AppResponseDTO> deleteDiet(@PathVariable String id) {
        ipdDietService.deleteDiet(id);
        return ResponseEntity.ok(AppResponseDTO.customStatus(ApplicationConstant.DELETED_SUCCESSFULLY, HttpStatus.OK));
    }

    @GetMapping(EndpointConstants.IPD_DIET_PLAN)
    public ResponseEntity<AppResponseDTO> getDietPlan(@RequestParam String ipdAdmissionId) {
        DietPlanPdfResponseDTO response = ipdDietService.getDietPlanPdf(ipdAdmissionId);
        return ResponseEntity.ok(AppResponseDTO.ok(response));
    }
}