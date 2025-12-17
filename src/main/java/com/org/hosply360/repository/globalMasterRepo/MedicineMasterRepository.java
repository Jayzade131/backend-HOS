package com.org.hosply360.repository.globalMasterRepo;

import com.org.hosply360.dao.globalMaster.MedicineMaster;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface MedicineMasterRepository extends MongoRepository<MedicineMaster, String> {

    boolean existsByName(String name);

    @Query("{ 'isDefunct': ?0, 'id': ?1 }")
    Optional<MedicineMaster> findByIdAndDefunct(boolean defunct, String id );
}
