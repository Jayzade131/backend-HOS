package com.org.hosply360.repository.PathologyRepo;

import com.org.hosply360.dao.pathology.PackageTestReport;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface PackageTestReportRepository extends MongoRepository<PackageTestReport, String>,CustomPacTestReportRepository {

    @Query("{id: ?0, defunct: ?1}")
    Optional<PackageTestReport> findByIdAndDefunct(String id, boolean defunct);
}
