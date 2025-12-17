package com.org.hosply360.dto.pathologyDTO;

import com.org.hosply360.constant.Enums.TestSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestManagerReqDTO {
    private String organizationId;
    private String patientId;
    private LocalDateTime TestDateTime;
    private List<String> testId ;
    private String packageId;
    private TestSource source;
}
