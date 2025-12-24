package com.org.hosply360.dto.OPDDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentDocInfoDTO implements Serializable {
    private String doc_id;
    private String firstName;
    private String specialtyName;
    private String specialtyId;
    private Double firstRate;
    private Double secondRate;
    private Double tariffFirstRate;
    private Double tariffSecondRate;
}
