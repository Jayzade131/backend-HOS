package com.org.hosply360.repository.OPDRepo;

import com.org.hosply360.dao.OPD.OPDPaymentHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OPDPaymentHistoryRepository extends MongoRepository<OPDPaymentHistory, String> {

    List<OPDPaymentHistory> findByInvoiceIdAndDefunctFalse(String invoiceId);
}