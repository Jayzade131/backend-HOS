package com.org.hosply360.dto.pathologyDTO;

import com.org.hosply360.constant.Enums.PackageTestStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
@Setter
@Getter
public class PackageTestReportDTO {
    private String id;
    private String organizationId;
    private TestManagerDTO testManagerDTO;
    private String doctorId;
    private String pId;
    private String patId;
    private LocalDateTime reportDate;
    private List<PackageTestReportItem> packageTestReportItem;
    private Boolean defunct;
    private PackageTestStatus status;
}
