package com.org.hosply360.dto.OPDDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyScheduleDTO {

    private String morningSessionFrom;
    private String morningSessionTo;
    private String afternoonSessionFrom;
    private String afternoonSessionTo;
    private String eveningSessionFrom;
    private String eveningSessionTo;
    private int newDuration;
    private int followUpDuration;
    private List<String> extraSession;
}
