package com.org.hosply360.repository.IPD;

import com.org.hosply360.dao.IPD.IPDDiet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IPDDietRepository extends MongoRepository<IPDDiet, String> {

    @Query("{'id': ?0, 'defunct': ?1}")
    Optional<IPDDiet> findByIdAndDefunct(String id, boolean defunct);

    @Query("{ 'ipdAdmissionId.id': ?0, 'defunct': ?1 }")
    List<IPDDiet> findByIpdAdmissionIdAndDefunct(String ipdAdmissionId, boolean defunct);

}
