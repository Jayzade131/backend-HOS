package com.org.hosply360.dto.IPDDTO;

import com.org.hosply360.dto.frontDeskDTO.PatientInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PatientInfoDetailDTO extends PatientInfoDTO {

    private String alternateNo;
    private String age;
    private String gender;
    private String dateOfBirth;
    private String bloodGroup;
    private String maritalStatus;
    private String email;
    private String address;

}
