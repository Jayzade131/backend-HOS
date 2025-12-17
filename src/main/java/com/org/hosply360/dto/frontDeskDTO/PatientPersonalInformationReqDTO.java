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
public class PatientPersonalInformationReqDTO {
    private String title;

    private String firstName;

    private String middleName;

    private String lastName;

    private String dateOfBirth;

    private String gender;

    private String preferredName;

    private String maritalStatus;

    private String bloodType;

    private String occupationId;
}
