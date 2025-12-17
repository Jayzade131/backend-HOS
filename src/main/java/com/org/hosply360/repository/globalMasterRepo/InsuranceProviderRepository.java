package com.org.hosply360.repository.globalMasterRepo;

import com.org.hosply360.dao.globalMaster.InsuranceProvider;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InsuranceProviderRepository extends MongoRepository<InsuranceProvider, String> {
    @Query("{id: ?0, defunct: ?1}")
    Optional<InsuranceProvider> findByIdAndDefunct(String id, boolean defunct);

    @Query(value = "{ 'organization.id': ?0, 'defunct': ?1 }", sort = "{ 'createdDate': -1 }")
    List<InsuranceProvider> findAllByDefunct(String organizationId, boolean defunct);

    @Query(value = "{ 'organization.id': ?0, 'defunct': ?1 }", sort = "{ 'createdDate': -1 }")
    Optional<InsuranceProvider> findByCodeAndDefunct(String code, boolean defunct);


}
