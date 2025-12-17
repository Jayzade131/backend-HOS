package com.org.hosply360.dto.OPDDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentSummaryDTO {

    private String appointmentId;
    private String patientId;
    private LocalDateTime appointmentDate;
    private String startTime;
    private String endTime;
    private String name;
    private String age;
    private String dob;
    private String gender;
    private String status;
    private String consultantName;
    private LocalDateTime lastVisit;
    private Integer totalVisit;
    private String visitType;
    private String doc_id;
}
