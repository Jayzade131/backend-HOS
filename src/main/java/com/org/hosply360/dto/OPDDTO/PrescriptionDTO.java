package com.org.hosply360.dto.OPDDTO;

import com.org.hosply360.constant.Enums.TestWhen;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionDTO {

    private String id;

    private String organizationId;

    private LocalDate prescriptionDate;

    private String patientId;

    private String doctorId;

    private String appointmentId;

    private String complaints;

    private String patientRecord;

    private String generalExamination;

    private String history;

    private ObstetricDTO obstetric;

    private VitalsDTO vitals;

    private List<String> testId;

    private TestWhen testWhen;

    private String diagnosis;

    private List<PrescribedMedDTO> prescribedMeds;

    private String advice;

    private String nextVisit;

    private String mobileNumber;

    private String email;
}
