package com.org.hosply360.dto.OPDDTO;

import com.org.hosply360.constant.Enums.TestWhen;
import com.org.hosply360.dto.globalMasterDTO.TestDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PrescriptionResponseDTO {

    private String id;

    private String organizationId;

    private LocalDate prescriptionDate;

    private String patientId;

    private String patientFirstName;

    private String patientLastName;

    private String patientMidName;

    private String doctorId;

    private String doctorName;

    private AppointmentDTO appointment;

    private String complaints;

    private String patientRecord;

    private String generalExamination;

    private String history;

    private ObstetricDTO obstetric;

    private VitalsDTO vitals;

    private List<TestDTO> test;

    private TestWhen testWhen;

    private String diagnosis;

    private List<PrescribedMedResponseDTO> prescribedMeds;

    private String advice;

    private String nextVisit;

    private String mobileNumber;

    private String email;

}
