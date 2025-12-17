package com.org.hosply360.dto.globalMasterDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceProviderReqDTO {
    private String id;
    private String organization;
    private String name;
    private String code;
    private String providerType;
    private String registrationNumber;
    private String contactPersonName;
    private String contactNumber;
    private String email;
    private String address;
    private String website;
    private Boolean defunct;
}
