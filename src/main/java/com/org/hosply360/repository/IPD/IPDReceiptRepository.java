package com.org.hosply360.repository.IPD;

import com.org.hosply360.dao.IPD.IPDReceipt;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IPDReceiptRepository extends MongoRepository<IPDReceipt, String> {
}
