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
public class PatientInfoDTO {

    private String id;
    private String firstname;
    private String lastname;
    private String pid;
    private String patientNumber;


}
