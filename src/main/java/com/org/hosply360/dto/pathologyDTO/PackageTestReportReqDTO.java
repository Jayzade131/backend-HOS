package com.org.hosply360.dto.pathologyDTO;

import com.org.hosply360.constant.Enums.PackageTestStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
@Setter
@Getter
public class PackageTestReportReqDTO {
    private String packageTestReportId;
    private String organizationId;
    private String testManagerId ;
    private String patientId ;
    private String doctorId;
    private LocalDateTime reportDate;
    private List<TestDataReqDTO> testDataReqDTOS;
    private PackageTestStatus status;
}
