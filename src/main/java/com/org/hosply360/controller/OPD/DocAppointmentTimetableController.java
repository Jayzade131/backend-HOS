package com.org.hosply360.controller.OPD;

import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.EndpointConstants;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.dto.OPDDTO.OpTimetableDTO;
import com.org.hosply360.service.OPD.DocAppointmentTimetableService;
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
@RequestMapping(EndpointConstants.FRONTDESK_API)
@RequiredArgsConstructor
public class DocAppointmentTimetableController {

    private static final Logger logger = LoggerFactory.getLogger(DocAppointmentTimetableController.class);

    private final DocAppointmentTimetableService timetableService;

        @PostMapping(EndpointConstants.DOC_SCHEDULE_API)
        public ResponseEntity<AppResponseDTO> createTimetable(@RequestBody OpTimetableDTO dto) {
            logger.info("createTimetable");
            OpTimetableDTO created = timetableService.createOpTimetable(dto);
            return ResponseEntity.ok(AppResponseDTO.ok(created));
        }

        @PutMapping(EndpointConstants.DOC_SCHEDULE_API)
        public ResponseEntity<AppResponseDTO> updateTimetable(@RequestBody OpTimetableDTO dto) {
            logger.info("updateTimetable");
            OpTimetableDTO created = timetableService.updateOpTimetable(dto);
            return ResponseEntity.ok(AppResponseDTO.ok(created));
        }

        @GetMapping(EndpointConstants.GET_DOC_SCHEDULES_API)
        public ResponseEntity<AppResponseDTO> getAllTimetables(@RequestParam(required = true) String organizationId) {
            logger.info("getAllTimetables");
            return ResponseEntity.ok(AppResponseDTO.ok(timetableService.getAllOpTimetables(organizationId)));
        }

    @DeleteMapping(EndpointConstants.DELETE_DOC_SCHEDULE_API)
        public ResponseEntity<AppResponseDTO>deleteSchedule(@PathVariable String id){
            logger.info("deleteSchedule");
            timetableService.deleteOpTimetable(id);
            logger.info("Schedule with ID {} deleted successfully", id);
        return ResponseEntity.ok(AppResponseDTO.customStatus(ApplicationConstant.DELETED_SUCCESSFULLY, HttpStatus.OK));
        }

    @GetMapping(EndpointConstants.GET_DOC_SCHEDULE_BY_DOCTOR_ID_API)
    public ResponseEntity<AppResponseDTO> getTimetableByDoctorId(
            @PathVariable String doctorId,
            @PathVariable String orgId) {
        OpTimetableDTO dto = timetableService.getOpTimetableByDoctorId(doctorId, orgId);
        return ResponseEntity.ok(AppResponseDTO.ok(dto));
    }
}
