package com.org.hosply360.repository.IPD;

import com.org.hosply360.dao.IPD.IPDDischargeForm;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface IPDDischargeFormRepository extends MongoRepository<IPDDischargeForm, String> {

    @Query("{'id': ?0, 'defunct': ?1}")
    Optional<IPDDischargeForm> findByIdAndDefunct(String id, boolean defunct);
}
