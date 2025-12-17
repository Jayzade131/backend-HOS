package com.org.hosply360.repository.globalMasterRepo;

import com.org.hosply360.dao.globalMaster.PackageE;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PackageERepository extends MongoRepository<PackageE, String> {
    @Query("{ 'packageName': ?0, 'defunct': ?1 }")
    Optional<PackageE> findByPackageNameAndDefunct(String packageName, boolean defunct);

    @Query("{ 'id': ?0, 'defunct': ?1 }")
    Optional<PackageE> findByIdAndDefunct(String id, Boolean defunct);

    @Query(value = "{ 'organization.id': ?0, 'defunct': ?1 }", sort = "{ 'createdDate': -1 }")
    List<PackageE> findByOrganizationIdAndDefunct(String organizationId, boolean defunct);
}
