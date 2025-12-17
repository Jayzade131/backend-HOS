package com.org.hosply360.repository.IPD;


import com.org.hosply360.dao.IPD.IPDDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IPDDocumentRepository extends MongoRepository<IPDDocument, String> {

    @Query("{ 'ipdAdmission._id': ?0, 'defunct': ?1 }")
    List<IPDDocument> findByIpdAdmissionIdAndDefunct(String ipdAdmissionId, boolean defunct);

    @Query("{id: ?0, defunct: ?1}")
    Optional<IPDDocument> findByIdAndDefunct(String id, boolean defunct);
}


