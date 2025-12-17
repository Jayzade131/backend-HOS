package com.org.hosply360.service.OPD;

import com.org.hosply360.dto.OPDDTO.ConsultationAppointmentDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomAppointmentRepository {
    List<ConsultationAppointmentDTO> fetchConsultationDetailsWithAggregation(String orgId, LocalDateTime fromDate, LocalDateTime toDate);

}
