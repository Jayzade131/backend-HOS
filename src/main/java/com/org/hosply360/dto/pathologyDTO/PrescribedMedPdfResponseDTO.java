package com.org.hosply360.dto.pathologyDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrescribedMedPdfResponseDTO {
    private String medicine;
    private String dose;
    private String when;
    private String frequency;
    private String duration;
    private String notes;
}
