package com.org.hosply360.dto.frontDeskDTO;

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
public class PatientAllergyDTO {

    private String allergen;

    private String reaction;

    private String severity;
}

