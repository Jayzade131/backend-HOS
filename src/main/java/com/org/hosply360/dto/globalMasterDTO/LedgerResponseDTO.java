package com.org.hosply360.dto.globalMasterDTO;

import com.org.hosply360.constant.Enums.Group;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LedgerResponseDTO {

    private String id;

    private String ledgerName;

    private AddressDTO address;

    private Group group;

    private String mobile;

    private String email;

    private String aadhaar;

    private String pan;

    private String contractPerson;

    private String bankAccountName;

    private String branch;

    private String bankName;

    private String bankAccountNumber;

    private String ifscCode;

    private Boolean registeredWithGst;

    private String gstNumber;

    private OrganizationDTO organization;

}
