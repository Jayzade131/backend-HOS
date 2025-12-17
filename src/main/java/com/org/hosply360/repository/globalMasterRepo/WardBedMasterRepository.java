package com.org.hosply360.repository.globalMasterRepo;

import com.org.hosply360.constant.Enums.AdmitStatus;
import com.org.hosply360.dao.globalMaster.WardBedMaster;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WardBedMasterRepository extends MongoRepository<WardBedMaster, String> {
    @Query(value = "{ 'orgId': ?0, 'ward._id': ?1, 'defunct': false }")
    List<WardBedMaster> findAllByOrgIdAndWardIdAndDefunctFalse(String orgId, String wardId);

    @Query(value = "{ 'orgId': ?0, 'ward._id': ?1, 'bedNo': ?2 }", exists = true)
    boolean existsByOrgIdAndWardIdAndBedNo(String orgId, String wardId, String bedNo);

    @Query("{ '_id': ?0, 'defunct': false }")
    Optional<WardBedMaster> findByIdAndDefunctFalse(String id);

    @Query(value = "{ 'orgId': ?0, 'ward._id': ?1, 'status': ?2, 'defunct': false }")
   List<WardBedMaster> findAllByOrgIdAndWardIdAndStatusAndDefunctFalse(String orgId, String wardId, AdmitStatus status);
}
