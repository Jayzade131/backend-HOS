package com.org.hosply360.repository.PathologyRepo;

import com.org.hosply360.dao.pathology.TestReport;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface TestReportRepository extends MongoRepository<TestReport, String>,CustomTestReportRepository {

    @Query("{id: ?0, defunct: ?1}")
    Optional<TestReport> findByIdAndDefunct(String id, boolean defunct);

    Optional<TestReport> findByTestManagerIdAndTestIdAndDefunctFalse(
            String testManagerId,
            String testId
    );

}
