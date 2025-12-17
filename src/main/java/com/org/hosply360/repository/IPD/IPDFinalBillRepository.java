package com.org.hosply360.repository.IPD;

import com.org.hosply360.dao.IPD.IPDFinalBill;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPDFinalBillRepository extends MongoRepository<IPDFinalBill, String> {
    Optional<IPDFinalBill> findByAdmission_Id(String admissionId);
    Optional<IPDFinalBill> findByFinalBillNo(String finalBillNo);
    boolean existsByFinalBillNo(String finalBillNo);
    boolean existsByAdmission_Id(String admissionId);

}
