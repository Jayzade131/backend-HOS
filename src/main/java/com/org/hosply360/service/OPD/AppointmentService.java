package com.org.hosply360.service.OPD;

import com.org.hosply360.constant.Enums.AppointmentStatus;
import com.org.hosply360.dto.OPDDTO.AppointmentDTO;
import com.org.hosply360.dto.OPDDTO.AppointmentReqDTO;
import com.org.hosply360.dto.authDTO.AppResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {


    String createAppointment(AppointmentReqDTO request);

    String updateAppointment(AppointmentReqDTO request);

    List<AppointmentDTO> getAppointmentsByPatientAndOrgExcludingCompleted(String patientId, String orgId);

   List<AppointmentDTO> getAppointments(String id, String doctorId, String orgId, LocalDate appointmentDate);

    void deleteAppointment(String id, String orgId);

    void updateStatus(String id, String orgId, AppointmentStatus status);

    List<AppointmentDTO> getAppointmentsByOrgIdAndDateRange(String orgId, LocalDate fromDate, LocalDate toDate);

    AppResponseDTO getAppointmentFilters(
            String id, String pId,Boolean isWalkIn, String doctorId, String orgId,
            LocalDate fromDate, LocalDate toDate,
            int page, int size);
}