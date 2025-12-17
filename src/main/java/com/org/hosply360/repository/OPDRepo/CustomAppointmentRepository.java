package com.org.hosply360.repository.OPDRepo;

import com.org.hosply360.dto.OPDDTO.AppointmentSummaryDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomAppointmentRepository {
    List<AppointmentSummaryDTO> findAppointmentSummary(
            String orgId, LocalDateTime fromDate, LocalDateTime toDate
    );
}
