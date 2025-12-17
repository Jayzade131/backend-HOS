package com.org.hosply360.dto.frontDeskDTO;

import com.org.hosply360.dto.globalMasterDTO.OccupationDTO;
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
public class PatientPersonalInformationDTO {

    private String title;

    private String firstName;

    private String middleName;

    private String lastName;

    private String dateOfBirth;

    private String gender;

    private String preferredName;

    private String maritalStatus;

    private String bloodType;

    private OccupationDTO occupation;



}
