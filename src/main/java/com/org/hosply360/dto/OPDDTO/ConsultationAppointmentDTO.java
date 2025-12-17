package com.org.hosply360.dto.OPDDTO;

import com.org.hosply360.constant.Enums.AppointmentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultationAppointmentDTO {

    private String id;
    private String aId;
    private String doctorId;
    private String pId;
    private String patientId;

    private String patientName;
    private String ageGender;
    private String mobile;

    private String consultant;
    private String lastVisit;
    private int totalVisit;

    private LocalDateTime appointmentDate;
    private AppointmentStatus status;

    private String appointmentType;
}
