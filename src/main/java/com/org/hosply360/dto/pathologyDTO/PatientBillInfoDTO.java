package com.org.hosply360.dto.pathologyDTO;

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
public class PatientBillInfoDTO {
    private String fullName;
    private String pId;
    private String dob;
    private String gender;
    private String age;
    private String phone;
    private String address;
}

