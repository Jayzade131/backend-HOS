package com.org.hosply360.controller.IPD;

import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.IPDDTO.IPDFinancialSummaryReqDTO;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.service.IPD.IPDFinancialSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(EndpointConstants.IPD_API)
@RequiredArgsConstructor
public class IPDFinancialSummaryController {

    private final IPDFinancialSummaryService ipdFinancialSummaryService;

    @PostMapping(EndpointConstants.IPD_FINANCIAL_SUMMARY)
    public ResponseEntity<AppResponseDTO> createFinancialSummary (@RequestBody IPDFinancialSummaryReqDTO reqDTO) {
        return ResponseEntity.ok(AppResponseDTO.ok(ipdFinancialSummaryService.createFinancialSummary(reqDTO)));
    }

    @GetMapping(EndpointConstants.IPD_FINANCIAL_SUMMARIES)
    public ResponseEntity<AppResponseDTO> getFinancialSummary(
            @RequestParam(required = false) String organizationId,
            @RequestParam(required = false) String ipdAdmissionId,
            @RequestParam(required = false) String id) {
        return ResponseEntity.ok(AppResponseDTO.ok(ipdFinancialSummaryService.getFinancialSummary(organizationId, ipdAdmissionId, id)));
    }

    @PutMapping(EndpointConstants.IPD_FINANCIAL_SUMMARY_REFUND)
    public ResponseEntity<AppResponseDTO> refundFinancialSummary(@RequestBody IPDFinancialSummaryReqDTO reqDTO) {
        return ResponseEntity.ok(AppResponseDTO.ok(ipdFinancialSummaryService.processRefund(reqDTO)));
    }
}
