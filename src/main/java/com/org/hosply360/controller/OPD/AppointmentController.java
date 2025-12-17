package com.org.hosply360.controller.OPD;

import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.constant.Enums.AppointmentStatus;
import com.org.hosply360.dto.OPDDTO.AppointmentDTO;
import com.org.hosply360.dto.OPDDTO.AppointmentReqDTO;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.service.OPD.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(EndpointConstants.FRONTDESK_API)
@RequiredArgsConstructor
public class AppointmentController {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);
    private final AppointmentService appointmentService;

    @PostMapping(EndpointConstants.CREATE_APPOINTMENT_API)
    public ResponseEntity<AppResponseDTO> createAppointment(@RequestBody AppointmentReqDTO dto) {

        String created = appointmentService.createAppointment(dto);
        return ResponseEntity.ok(AppResponseDTO.ok(created));
    }

    @PutMapping(EndpointConstants.UPDATE_APPOINTMENT_API)
    public ResponseEntity<AppResponseDTO> updateAppointment(@RequestBody AppointmentReqDTO dto) {
        logger.info("Updating appointment with ID: {}", dto.getId());
        String updated = appointmentService.updateAppointment(dto);
        return ResponseEntity.ok(AppResponseDTO.ok(updated));
    }

    @Deprecated
    @GetMapping(EndpointConstants.GET_APPOINTMENTS_BY_PATIENT_AND_ORG_EXCLUDING_COMPLETED_API)
    public ResponseEntity<AppResponseDTO> getAppointmentsByPatientAndOrgExcludingCompleted(@PathVariable String patientId, @PathVariable String orgId) {
        logger.info("Fetching appointments for patient ID: {} in org: {} excluding completed", patientId, orgId);
        return ResponseEntity.ok(AppResponseDTO.ok(
                appointmentService.getAppointmentsByPatientAndOrgExcludingCompleted(patientId, orgId)));
    }

    @GetMapping(EndpointConstants.GET_APPOINTMENTS)
    public ResponseEntity<AppResponseDTO> getAppointments(
            @PathVariable String orgId,
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appointmentDate) {

        logger.info("Fetching appointments with params - id: {}, doctorId: {}, orgId: {}, date: {}",
                id, doctorId, orgId, appointmentDate);

        return ResponseEntity.ok(AppResponseDTO.ok(
                appointmentService.getAppointments(id, doctorId, orgId, appointmentDate)));
    }

    @Deprecated
    @GetMapping(EndpointConstants.GET_APPOINTMENTS_BY_ORG_ID_AND_DATE_RANGE_API)
    public ResponseEntity<AppResponseDTO> getAppointmentsByDateRange(
            @PathVariable String orgId,
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        logger.info("Fetching appointments from {} to {} for org: {}", fromDate, toDate, orgId);
        List<AppointmentDTO> appointmentList = appointmentService.getAppointmentsByOrgIdAndDateRange(orgId, fromDate, toDate);
        return ResponseEntity.ok(AppResponseDTO.ok(appointmentList));
    }

    @DeleteMapping(EndpointConstants.DELETE_APPOINTMENT_API)
    public ResponseEntity<AppResponseDTO> deleteAppointment(@PathVariable("id") String id, @PathVariable("orgId") String orgId) {
        logger.info("Deleting appointment with ID: {}, OrgID: {}", id, orgId);
        appointmentService.deleteAppointment(id, orgId);
        return ResponseEntity.ok(AppResponseDTO.customStatus(ApplicationConstant.DELETED_SUCCESSFULLY, HttpStatus.OK));
    }

    @PutMapping(EndpointConstants.UPDATE_STATUS_API)
    public ResponseEntity<AppResponseDTO> updateStatus(@PathVariable String id, @PathVariable String orgId, @PathVariable String status) {
        logger.info("Updating status for appointment ID: {}, OrgID: {}, New Status: {}", id, orgId, status);
        appointmentService.updateStatus(id, orgId, AppointmentStatus.valueOf(status));
        return ResponseEntity.ok(AppResponseDTO.ok(ApplicationConstant.STATUS_UPDATED_SUCCESSFULLY));
    }

    @GetMapping(EndpointConstants.APPOINTMENTS_FILTERS)
    public ResponseEntity<AppResponseDTO> getAppointmentsFilters(
            @RequestParam String orgId,
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String pId,
            @RequestParam(required = false) Boolean isWalkIn,
            @RequestParam(required = false) String doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size)

    {

        logger.info("API call: Fetch appointments with invoices - id: {}, pId: {},isWalkIn: {}, doctorId: {}, orgId: {}, fromDate: {}, toDate: {}, page: {}, size: {}",
                id, pId,isWalkIn, doctorId, orgId, fromDate, toDate, page, size);

        return ResponseEntity.ok(
                appointmentService.getAppointmentFilters(id, pId, isWalkIn,doctorId, orgId, fromDate, toDate, page, size)
        );
    }



}
