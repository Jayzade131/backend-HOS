package com.org.hosply360.dao.pathology;

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
public class TestReportParameter {
    private String name;
    private String value;
    private String unit;
    private String referenceRange;
}
