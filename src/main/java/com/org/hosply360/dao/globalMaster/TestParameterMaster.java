package com.org.hosply360.dao.globalMaster;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestParameterMaster{
    private String name;
    private String unit;
    private String referenceRange;
}
