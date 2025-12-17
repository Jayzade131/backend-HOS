package com.org.hosply360.repository.globalMasterRepo;

import com.org.hosply360.dao.globalMaster.Religion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReligionRepository extends MongoRepository<Religion, String> {

    @Query(value = "{ 'organization.id': ?0, 'defunct': ?1 }", sort = "{ 'createdDate': -1 }")
    List<Religion> findAllByDefunct(String organizationId, boolean defunct);

    @Query("{id: ?0, defunct: ?1}")
    Optional<Religion> findByIdAndDefunct(String id, boolean defunct);



}
