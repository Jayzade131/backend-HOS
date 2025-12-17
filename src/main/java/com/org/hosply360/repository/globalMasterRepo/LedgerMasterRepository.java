package com.org.hosply360.repository.globalMasterRepo;


import com.org.hosply360.dao.globalMaster.Ledger;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface LedgerMasterRepository extends MongoRepository<Ledger, String> {

    @Query(value = "{ 'organization.id': ?0, 'defunct': ?1 }", sort = "{ 'createdDate': -1 }")
    List<Ledger> findByOrganizationIdAndDefunct(String organizationId, boolean defunct);
}
