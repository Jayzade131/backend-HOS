package com.org.hosply360.dto.pathologyDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class GetResTestReportDTO {
    private String testReportId;
    private String orgId;
    private String pId;
    private String patId;
    private String testManagerId;
    private String firstName;
    private String lastName;
    private String phoneNo;
    private LocalDateTime testReportDateTime;
    private String testName;
}
