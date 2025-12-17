package com.org.hosply360.repository.globalMasterRepo;

import com.org.hosply360.dao.globalMaster.PatientCategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PatientCategoryRepository extends MongoRepository<PatientCategory,String> {
    @Query("{ 'categoryName': ?0, 'defunct': ?1 }")
    Optional<PatientCategory> findByCategoryNameAndDefunct(String categoryName, Boolean defunct);

    @Query("{ 'id': ?0, 'defunct': ?1 }")
    Optional<PatientCategory> findByIdAndDefunct(String id, Boolean defunct);

    @Query(value = "{ 'organization.id': ?0, 'defunct': ?1 }", sort = "{ 'createdDate': -1 }")
    List<PatientCategory> findByOrganizationId(String organizationId, boolean defunct);
}
