package com.org.hosply360.repository.globalMasterRepo;

import com.org.hosply360.dao.globalMaster.Occupation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OccupationMasterRepository extends MongoRepository<Occupation, String> {


    @Query("{'occupationCode': ?0,'defunct':?1}")
    Optional<Occupation> findByOccupationCodeAndDefunct(String code,boolean defunct);


    @Query("{'id': ?0,'defunct':?1}")
    Optional<Occupation> findByIdAndDefunct(String id, boolean defunct);

    @Query(value = "{ 'organization.id': ?0, 'defunct': ?1 }", sort = "{ 'createdDate': -1 }")
    List<Occupation> findAllByDefunct(String organizationId, boolean defunct);





}
