package com.org.hosply360.dto.OPDDTO;

import com.org.hosply360.constant.Enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentReqDTO {

    private String id;

    private String doctorId;

    private String specialtyId;

    private String orgId;

    private String patientId;

    private LocalDate  appointmentDate;

    private String session;

    private String startTime;

    private String endTime;

    private AppointmentStatus status;

    private String appointmentType;

    private boolean isWalkIn;


}
