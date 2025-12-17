package com.org.hosply360.repository.globalMasterRepo;

import com.org.hosply360.dao.globalMaster.BillingItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BillingItemRepository extends MongoRepository<BillingItem,String> {
    @Query("{ 'itemName': ?0, 'defunct': false }")
    Optional<BillingItem> findByItemNameAndDefunct(String itemName);

    @Query("{ 'id': ?0, 'defunct': ?1 }")
    Optional<BillingItem> findByIdAndDefunct(String id,boolean defunct);

    @Query(value = "{ 'organization.id': ?0, 'defunct': ?1 }", sort = "{ 'createdDate': -1 }")
    List<BillingItem> findByAllDefunct(String organizationId, boolean defunct);

    @Query(value = "{ 'organization.id': ?0, 'billingItemGroup.id': ?1, 'defunct': ?2 }", sort = "{ 'createdDate': -1 }")
    List<BillingItem> findByItemGrpAllDefunct(String organizationId, String billingItemGroupId, boolean defunct);

    @Query("{ 'serviceCode': ?0 }")
    Optional<BillingItem> findByServiceCode(String serviceCode);

}
