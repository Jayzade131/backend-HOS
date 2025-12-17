package com.org.hosply360.dao.OPD;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklySchedule {


    @Field("morning_session_from")
    private String morningSessionFrom;

    @Field("morning_session_to")
    private String morningSessionTo;

    @Field("afternoon_session_from")
    private String afternoonSessionFrom;

    @Field("afternoon_session_to")
    private String afternoonSessionTo;

    @Field("evening_session_from")
    private String eveningSessionFrom;

    @Field("evening_session_to")
    private String eveningSessionTo;

    @Field("new_duration")
    private int newDuration;

    @Field("follow_up_duration")
    private int followUpDuration;

    @Field("extra_session")
    private List<String> extraSession;
}



