package com.org.hosply360.dto.pathologyDTO;

import com.org.hosply360.dao.pathology.TestReportParameter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TestDataReqDTO {
    private String testId;
    private List<TestReportParameter> parametersValues;
}
