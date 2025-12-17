package com.org.hosply360.repository.PathologyRepo;

import com.org.hosply360.constant.Enums.TestStatus;
import com.org.hosply360.dto.pathologyDTO.GetResTestManagerDTO;
import com.org.hosply360.dto.pathologyDTO.PagedResultForTest;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomTestManagerRepository {
    PagedResultForTest<GetResTestManagerDTO> findCustomTestManagersDynamic(
            String orgId,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            String pId,
            String encryptedMobile,
            List<TestStatus> statuses,
            int page,
            int size
    );
}
