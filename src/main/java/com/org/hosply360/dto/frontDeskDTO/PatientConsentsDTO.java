package com.org.hosply360.dto.frontDeskDTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientConsentsDTO {

    private boolean privacyPolicyAccepted;

    private boolean termsAndConditionsAccepted;

    private boolean dataSharingConsent;

    private boolean marketingConsent;

    private LocalDate consentDate;
}
