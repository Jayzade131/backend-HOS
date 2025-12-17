package com.org.hosply360.repository.globalMasterRepo;

import com.org.hosply360.dao.globalMaster.Speciality;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SpecialityMasterRepository extends MongoRepository<Speciality, String> {

    @Query("{'id': ?0,'defunct':?1}")
    Optional<Speciality> findByIdAndDefunct(String id, boolean defunct);

    @Query(value = "{'organization.id': ?0,'defunct': ?1, 'masterType': ?2}", sort = "{ 'createdDate': -1 }")
    List<Speciality> findAllByDefunctAndMasterType(String organizationId, boolean defunct, String masterType);



}
