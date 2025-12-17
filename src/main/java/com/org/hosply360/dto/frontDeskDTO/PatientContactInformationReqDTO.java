package com.org.hosply360.dto.frontDeskDTO;

import com.org.hosply360.dto.globalMasterDTO.AddressDTO;
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
public class PatientContactInformationReqDTO {

    private String primaryPhone;

    private String secondaryPhone;

    private String email;

    private AddressDTO address;

    private String preferredContactMethod;
}
