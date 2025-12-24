package com.org.hosply360.repository.PathologyRepo;

import com.org.hosply360.dao.pathology.PathologyReceipt;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PathologyReceiptRepository extends MongoRepository<PathologyReceipt, String> {
}
