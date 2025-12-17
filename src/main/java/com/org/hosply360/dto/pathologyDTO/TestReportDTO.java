package com.org.hosply360.dto.pathologyDTO;

import com.org.hosply360.dao.globalMaster.Test;
import com.org.hosply360.dao.pathology.TestReportParameter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestReportDTO {
    private String id;
    private String organizationId;
    private TestManagerDTO testManagerDTO ;
    private String pId ;
    private String patId ;
    private Test test;
    private String doctorId;
    private LocalDateTime reportDate;
    private List<TestReportParameter> parameters;
    private Boolean defunct;
}
