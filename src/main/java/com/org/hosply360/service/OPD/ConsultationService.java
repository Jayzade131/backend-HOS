package com.org.hosply360.service.OPD;

import com.org.hosply360.dto.OPDDTO.AppointmentSummaryDTO;
import com.org.hosply360.dto.OPDDTO.ConsultationAppointmentDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsultationService {
    ConsultationAppointmentDTO getConsultationDetails(String appointmentId, String orgId);

    List<AppointmentSummaryDTO> getAllConsultationDetailsForOrg(String orgId, LocalDateTime fromDate, LocalDateTime toDate);
}
