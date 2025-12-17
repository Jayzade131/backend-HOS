package com.org.hosply360.dto.pathologyDTO;

import com.org.hosply360.constant.Enums.PackageTestStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class GetResPacTestReportDTO {
    private String packageTestReportId;
    private String orgId;
    private String pId;
    private String patId;
    private String testManagerId;
    private String firstName;
    private String lastName;
    private String phoneNo;
    private PackageTestStatus status;
    private LocalDateTime testReportDateTime;
    private List<String> testNames;

}
