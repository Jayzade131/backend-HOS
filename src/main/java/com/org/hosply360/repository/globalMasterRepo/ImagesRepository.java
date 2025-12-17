package com.org.hosply360.repository.globalMasterRepo;

import com.org.hosply360.dao.globalMaster.Images;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ImagesRepository extends MongoRepository<Images, String> {
}
