package com.org.hosply360.repository.PathologyRepo;

import com.org.hosply360.constant.Enums.TestStatus;
import com.org.hosply360.dao.pathology.TestManager;
import com.org.hosply360.dto.pathologyDTO.GetResTestManagerDTO;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TestManagerRepository extends MongoRepository<TestManager, String>,CustomTestManagerRepository {

    @Query("{id: ?0, defunct: ?1}")
    Optional<TestManager> findByIdAndDefunct(String id, boolean defunct);

}


