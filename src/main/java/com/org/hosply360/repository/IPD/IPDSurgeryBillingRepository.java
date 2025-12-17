package com.org.hosply360.repository.IPD;

import com.org.hosply360.dao.IPD.IPDSurgeryBilling;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IPDSurgeryBillingRepository extends MongoRepository<IPDSurgeryBilling, String> {
   @Query("{ 'surgeryId': ?0, }")
    Optional<IPDSurgeryBilling> findBySurgeryId(String surgeryId);
 @Query("{ 'ipdAdmissionId': ?0, 'hasCancelled': ?1 }")
 List<IPDSurgeryBilling> findByAdmissionIdAndCanceledStatus(String admissionId, Boolean hasCancelled);

}