package com.org.hosply360.repository.OPDRepo;

import com.org.hosply360.dto.OPDDTO.PatientUpcomingVisitResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface PrescriptionCustomRepository {
    List<PatientUpcomingVisitResponseDTO> findPatientsByNextVisitRange(LocalDate fromDate, LocalDate toDate, String doctorId);
}

