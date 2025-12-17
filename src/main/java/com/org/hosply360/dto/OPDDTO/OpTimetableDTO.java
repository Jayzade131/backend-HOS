package com.org.hosply360.dto.OPDDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpTimetableDTO {

    private String id;
    private String doctorId;
    private String doctorName;
    private String specialtyId;
    private String specialtyName;
    private String organizationId;
    private Map<String, WeeklyScheduleDTO> weeklySchedule;
    private boolean defunct;


}
