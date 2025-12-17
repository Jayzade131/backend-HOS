package com.org.hosply360.repository.globalMasterRepo;

import com.org.hosply360.dao.globalMaster.Test;
import org.apache.catalina.LifecycleState;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TestRepository extends MongoRepository<Test, String> {

    @Query("{ 'id': ?0, 'defunct': ?1 }")
    Optional<Test> findByIdAndDefunct(String id, boolean defunct);

    @Query("{ 'defunct': ?0, 'id': { $in: ?1 } }")
    List<Test> findAllByIdInTestAndDefunct(boolean defunct, List<String> id);

    @Query("{ 'defunct': ?0, 'id': { $in: ?1 } }")
    List<Test> findAllByDefunctAndIdIn(boolean defunct, List<String> ids);

    @Query("{ 'testName': ?0, 'defunct': false }")
   Optional<Test> findByTestNameAndDefunct(String testName, boolean defunct);

    @Query(value = "{ 'organization.id': ?0, 'defunct': ?1 }", sort = "{ 'createdDate': -1 }")
    List<Test> findByAllDefunct(String organizationId, boolean defunct);



}
