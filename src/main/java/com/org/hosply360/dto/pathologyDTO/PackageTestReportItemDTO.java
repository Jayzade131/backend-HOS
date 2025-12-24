package com.org.hosply360.dto.pathologyDTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PackageTestReportItemDTO {
    private String testName;
    private String testId;
    private LocalDateTime reportDate;
    private List<TestReportParameterDTO> parameters;
}
