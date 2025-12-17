package com.org.hosply360.repository.globalMasterRepo;

import com.org.hosply360.dao.globalMaster.BillingItemGroup;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BillingItemGroupRepository extends MongoRepository<BillingItemGroup,String> {
   @Query("{ 'itemGroupName': ?0, 'defunct': ?1 }")
   Optional<BillingItemGroup> findByItemGroupNameAndDefunct(String itemGroupName, Boolean defunct);
    @Query("{ 'id': ?0, 'defunct': ?1 }")
    Optional<BillingItemGroup> findByIdAndDefunct(String id, Boolean defunct);
    @Query(value = "{ 'organization.id': ?0, 'defunct': ?1 }", sort = "{ 'createdDate': -1 }")
    List<BillingItemGroup> findByOrganizationId(String organizationId, boolean defunct);



}
