package com.org.hosply360.dto.pathologyDTO;

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
public class TestReportReqDTO {
    private String testReportId;
    private String organizationId;
    private String testManagerId ;
    private String patientId ;
    private String doctorId;
    private TestDataReqDTO testDataReqDTOS;
    private Boolean defunct;
}
