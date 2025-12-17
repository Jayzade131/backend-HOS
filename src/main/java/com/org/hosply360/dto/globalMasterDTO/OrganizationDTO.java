package com.org.hosply360.dto.globalMasterDTO;


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
public class OrganizationDTO {

    private String id;

    private OrganizationDTO parentOrganization;

    private String organizationCode;

    private String organizationName;

    private String organizationQuote;

    private String organizationDesc;

    private AddressDTO address;

    private String gstNo;

    private String panNo;

    private String tanNo;

    private boolean defunct;

    private DocumentInfoDTO orgLogo;

    private String email;

    private String phoneNumber;

    private String website;

    private String registrationNumber;
}
