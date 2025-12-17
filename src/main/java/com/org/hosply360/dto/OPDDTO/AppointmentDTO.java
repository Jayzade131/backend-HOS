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
public class AppointmentDTO {

    private String id;

    private String aId;

    private String doctorId;

    private String specialtyId;

    private String orgId;

    private String patientId;

    private String pId;

    private LocalDateTime appointmentDate;

    private String session;

    private String startTime;

    private String endTime;

    private String status;

    private String appointmentType;

    private boolean defunct;

    private String sessionStartTime;

    private String sessionEndTime;

    private String patientName;

    private String patientNumber;

    private boolean isWalkIn;

    private AppointmentDocInfoDTO docInfo;


}
