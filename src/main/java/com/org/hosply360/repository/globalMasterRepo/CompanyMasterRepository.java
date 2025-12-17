package com.org.hosply360.repository.globalMasterRepo;

import com.org.hosply360.dao.globalMaster.CompanyMaster;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CompanyMasterRepository extends MongoRepository<CompanyMaster,String> {
    @Query("{ 'companyName': ?0, 'defunct': ?1 }")
    Optional<CompanyMaster> findByCompanyNameAndDefunct(String companyName, Boolean defunct);

    @Query("{ 'id': ?0, 'defunct': ?1 }")
    Optional<CompanyMaster> findByIdAndDefunct(String id, Boolean defunct);

    @Query(value = "{ 'organization.id': ?0, 'defunct': ?1 }", sort = "{ 'createdDate': -1 }")
    List<CompanyMaster> findByOrganizationId(String organizationId, boolean defunct);
}
