package com.org.hosply360.dto.pathologyDTO;

import com.org.hosply360.constant.Enums.TestStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
public class GetReqTestMangerDTO {
    private String orgId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String pId;
    private String mobileNo;
    private List<TestStatus> testStatuses;
    private int page = 0;
    private int size = 10;

}
