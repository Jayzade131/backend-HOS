package com.org.hosply360.repository.PathologyRepo;

import com.org.hosply360.dto.pathologyDTO.GetResPacTestReportDTO;
import com.org.hosply360.dto.pathologyDTO.PagedResultForTest;

import java.time.LocalDateTime;

public interface CustomPacTestReportRepository {
    PagedResultForTest<GetResPacTestReportDTO> findCustomPackageTestReports(
            String orgId,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            String pId,
            String encryptedMobile,
            int page,
            int size
    );
}
