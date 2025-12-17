package com.org.hosply360.repository.IPD;


import com.org.hosply360.dao.IPD.IPDBilling;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPDBillingRepository extends MongoRepository<IPDBilling, String> {

    @Query("{ 'admission.id': ?0 }")
    List<IPDBilling> findByAdmissionId(String admissionId);
    @Query("{ 'admission_id.$id': ObjectId(?0), 'canceled': ?1 }")
    List<IPDBilling> findByAdmissionIdAndCanceledStatus(String admissionId, Boolean canceled);


}
