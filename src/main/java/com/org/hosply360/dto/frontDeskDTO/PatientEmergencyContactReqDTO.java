package com.org.hosply360.dto.frontDeskDTO;

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
public class PatientEmergencyContactReqDTO {

    private String name;

    private String relationship;

    private String phone;

    private boolean isPrimary;
}
