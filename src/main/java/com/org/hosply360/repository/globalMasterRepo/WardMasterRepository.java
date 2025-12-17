package com.org.hosply360.repository.globalMasterRepo;

import com.org.hosply360.dao.globalMaster.WardMaster;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WardMasterRepository extends MongoRepository<WardMaster, String> {

    boolean existsByWardNameAndOrgId(String wardName, String orgId);

    Optional<WardMaster> findByWardNameAndOrgId(String wardName, String orgId);

    @Query(value = "{ 'orgId': ?0, 'defunct': false }", sort = "{ 'createdDate': -1 }")
    List<WardMaster> findAllByOrgIdAndDefunctFalse(String orgId);

    @Query("{ 'id': ?0,'orgId': ?1, 'defunct': false }")
    Optional<WardMaster> findByIdAndOrgIdAndDefunct(String id, String orgId, boolean defunct);

    @Query("{ 'id': ?0, 'defunct': false }")
    Optional<WardMaster> findByIdAndDefunct(String id, boolean defunct);




}