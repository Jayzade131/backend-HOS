package com.org.hosply360.repository.globalMasterRepo;

import com.org.hosply360.dao.globalMaster.Configuration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ConfigurationRepository extends MongoRepository<Configuration,String> {
    @Query("{ '_id': ?0, 'defunct': ?1 }")
    Optional<Configuration> findByIdAndDefunct(String id, boolean defunct);

    @Query(value = "{ 'organization.id': ?0, 'defunct': ?1 }", sort = "{ 'createdDate': -1 }")
    List<Configuration> findByOrganizationIdAndDefunct(String organizationId, boolean defunct);
}
