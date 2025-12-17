package com.org.hosply360.dto.pathologyDTO;

import com.org.hosply360.constant.Enums.TestStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Builder
public class GetResTestManagerDTO {

    private String id;
    private String orgId;
    private String firstName;
    private String lastName;
    private String packageName;
    private String source;
    private String pId;
    private String patId;
    private String phoneNo;
    private TestStatus status;
    private LocalDateTime testDateTime;
    private List<TestResDTO> testDto;
    private Double totalAmount;
    private Boolean hasPaid;
    private Double paidAmount;
    private Double balanceAmount;


}
