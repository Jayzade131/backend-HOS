package com.org.hosply360.dto.OPDDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PrescribedMedDTO {

    private String medicineId;

    private String dose;

    private String when;

    private String frequency;

    private String duration;

    private String notes;

}
