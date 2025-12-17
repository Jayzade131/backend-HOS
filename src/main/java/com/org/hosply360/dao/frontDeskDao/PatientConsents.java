package com.org.hosply360.dao.frontDeskDao;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientConsents {

    @Field("privacy_policy_accepted")
    private boolean privacyPolicyAccepted;

    @Field("terms_accepted")
    private boolean termsAndConditionsAccepted;

    @Field("data_sharing_consent")
    private boolean dataSharingConsent;

    @Field("marketing_consent")
    private boolean marketingConsent;

    @CreatedDate
    @Field("consent_date")
    private LocalDate consentDate;
}
