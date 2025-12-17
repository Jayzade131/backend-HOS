package com.org.hosply360.repository.PathologyRepo;

import com.org.hosply360.dto.pathologyDTO.GetResTestReportDTO;
import com.org.hosply360.dto.pathologyDTO.PagedResultForTest;

import java.time.LocalDateTime;

public interface CustomTestReportRepository {
    PagedResultForTest<GetResTestReportDTO> findCustomTestReports(
            String orgId,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            String pId,
            String encryptedMobile,
            int page,
            int size
    );
}
