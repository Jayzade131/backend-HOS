package com.org.hosply360.service.OPD;

import com.org.hosply360.dto.OPDDTO.PatientUpcomingVisitResponseDTO;
import com.org.hosply360.dto.OPDDTO.PrescriptionDTO;
import com.org.hosply360.dto.OPDDTO.PrescriptionPDFResponseDTO;
import com.org.hosply360.dto.OPDDTO.PrescriptionResponseDTO;

import java.util.List;

public interface PrescriptionService {

    PrescriptionResponseDTO createUpdatePrescription(PrescriptionDTO prescriptionDTO);

    List<PrescriptionResponseDTO> getPrescriptionByPatientId(String patientId, String orgId, int size);

    PrescriptionPDFResponseDTO generatePrescriptionPdf(String prescriptionId, String organizationId);

    List<PatientUpcomingVisitResponseDTO> getPatientsByNextVisitRange(String fromDate, String toDate, String doctorId);


}
