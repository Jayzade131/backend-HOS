package com.org.hosply360.dto.pathologyDTO;

import com.org.hosply360.constant.Enums.TestSource;
import com.org.hosply360.constant.Enums.TestStatus;
import com.org.hosply360.dto.frontDeskDTO.PatientDTO;
import com.org.hosply360.dto.globalMasterDTO.OrganizationDTO;
import com.org.hosply360.dto.globalMasterDTO.PackageEDTO;
import com.org.hosply360.dto.globalMasterDTO.TestDTO;
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
public class TestManagerDTO {
    private String id;
    private OrganizationDTO organizationDTO;
    private PatientDTO patientDTO;
    private TestStatus status;
    private LocalDateTime TestDateTime;
    private List<TestDTO> testDTO ;
    private PackageEDTO packageEDTO;
    private TestSource source;
    private Boolean defunct;
}
