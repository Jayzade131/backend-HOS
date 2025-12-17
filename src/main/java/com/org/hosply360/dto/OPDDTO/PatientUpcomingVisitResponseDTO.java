package com.org.hosply360.dto.OPDDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatientUpcomingVisitResponseDTO {

    private String patientId;
    private String pid;
    private String firstName;
    private String lastName;
    private Integer age;
    private String doctorId;
    private String doctorName;
    private String doctorSpecialty;
    private String nextVisitDate;
    private String lastVisitDate;
    private String mobileNo;
    private String gender;
    private String email;
    private String dob;
}
