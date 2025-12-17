package com.org.hosply360.dto.OPDDTO;

import com.org.hosply360.dao.OPD.Appointment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultationAggregationDTO {
    private Appointment appointment;
    private String lastVisit;
    private long totalVisits;
}
