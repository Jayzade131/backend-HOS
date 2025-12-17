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
public class OrganizationReqDTO {

    private String id;

    private String parentOrgId;

    private String organizationCode;

    private String organizationName;

    private String organizationQuote;

    private String organizationDesc;

    private AddressDTO address;

    private String email;

    private String registrationNumber;

    private DocumentInfoDTO documentInfoDTO;

    private String phoneNumber;


    private String website;

    private String gstNo;

    private String panNo;

    private String tanNo;


}
