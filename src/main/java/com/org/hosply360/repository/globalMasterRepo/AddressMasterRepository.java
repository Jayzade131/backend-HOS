package com.org.hosply360.repository.globalMasterRepo;

import com.org.hosply360.dao.globalMaster.Address;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressMasterRepository extends MongoRepository<Address, String> {


}
