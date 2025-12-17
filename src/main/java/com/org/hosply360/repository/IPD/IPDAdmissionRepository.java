package com.org.hosply360.repository.IPD;

import com.org.hosply360.dao.IPD.IPDAdmission;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IPDAdmissionRepository extends MongoRepository<IPDAdmission, String>, IPDBedCustomRepo {


    @Query("{'_id': ?0, 'defunct': ?1}")
    Optional<IPDAdmission> findByIdAndDefunct(String id, boolean defunct);



    @Query("{ '_id': ?0, 'defunct': ?1, 'orgId': ?2 }")
    Optional<IPDAdmission> findByIdAndDefunctAndOrg(ObjectId id, boolean defunct, String orgId);

    @Query("{ 'wardMaster._id': ?0, 'defunct': false }")
    List<IPDAdmission> findByWardMaster_IdAndDefunctFalseAndIpdStatus(
            String wardId
    );

    @Query("{ 'wardMaster._id': ?0, 'defunct': false, 'ipdStatus': ?1 }")
    List<IPDAdmission> findByWardMasterIdAndDefunctFalseAndIpdStatus(String wardId, String ipdStatus);


}
