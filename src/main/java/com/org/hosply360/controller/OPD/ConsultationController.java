package com.org.hosply360.controller.OPD;


import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.OPDDTO.GetConstDTO;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.dto.OPDDTO.ConsultationAppointmentDTO;
import com.org.hosply360.service.OPD.impl.ConsultationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping(EndpointConstants.FRONTDESK_API)
@RequiredArgsConstructor
public class ConsultationController {

    private static final Logger logger = LoggerFactory.getLogger(ConsultationController.class);

    private final ConsultationServiceImpl consultationService;

    @GetMapping(EndpointConstants.GET_CONSULTATION_DETAILS_API)
    public ResponseEntity<AppResponseDTO> getConsultationDetails(
            @PathVariable String appointmentId,
            @PathVariable String orgId) {

        logger.info("Fetching consultation details for appointmentId: {}, orgId: {}", appointmentId, orgId);

        ConsultationAppointmentDTO details = consultationService.getConsultationDetails(appointmentId, orgId);

        return ResponseEntity.ok(AppResponseDTO.ok(details));
    }
    @GetMapping(EndpointConstants.GET_ALL_CONSULTATION_DETAILS_API)
    public ResponseEntity<AppResponseDTO> getAllConsultationDetails(@RequestParam String orgId, @RequestParam LocalDateTime fromDate, @RequestParam LocalDateTime toDate) {

        return ResponseEntity.ok(
                AppResponseDTO.ok(consultationService.getAllConsultationDetailsForOrg(orgId,fromDate,toDate))
        );
    }

}
