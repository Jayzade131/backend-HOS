package com.org.hosply360.repository.OPDRepo;

import com.org.hosply360.dao.OPD.InvoicePaymentHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InvoicePaymentHistoryRepository extends MongoRepository<InvoicePaymentHistory, String> {
}
