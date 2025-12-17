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
public class PatientInsuranceDetailsReqDTO {

    private String providerId;

    private String policyNumber;

    private String groupNumber;

    private LocalDate validFrom;

    private LocalDate validTo;

    private boolean isPrimary;
}
