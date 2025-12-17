package com.org.hosply360.dto.OPDDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VitalsDTO {

    private String bp;
    private String pulse;
    private String spo2;
    private String covidVaccination;
    private String pallor;
    private String chest;

}
