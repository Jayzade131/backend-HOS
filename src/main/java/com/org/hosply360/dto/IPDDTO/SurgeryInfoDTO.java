package com.org.hosply360.dto.IPDDTO;

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
public class SurgeryInfoDTO {
    private String surgeryType;
    private String surgeonName;
    private String surgeryDate;
    private String surgeryTime;
}
