package com.org.hosply360.repository.IPD;

import com.org.hosply360.dao.IPD.IPDFinancialSummary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IPDFinancialSummaryRepository extends MongoRepository<IPDFinancialSummary, String> {

    @Query("{ 'ipdAdmission.id': ?0 }")
    Optional<IPDFinancialSummary> findByIpdAdmissionId(String admissionId);

//    List<IPDFinancialSummary> findByIpdAdmissionId(String admissionId);


    @Query("{ 'ipdAdmission.id': ?0 }")
    Optional<IPDFinancialSummary> findByIpIndAdmissionId(String admissionId);


}
