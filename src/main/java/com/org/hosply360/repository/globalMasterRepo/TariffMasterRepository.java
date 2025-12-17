package com.org.hosply360.repository.globalMasterRepo;

import com.org.hosply360.dao.globalMaster.Tariff;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TariffMasterRepository extends MongoRepository<Tariff, String> {

   @Query("{ 'name': ?0, 'defunct': ?1 }")
   Optional<Tariff> existsByName(String name, boolean defunct);

   @Query("{ 'id': ?0, 'defunct': ?1 }")
   Optional<Tariff> findByIdAndDefunct(String id, boolean defunct);

   @Query(value = "{ 'organization.id': ?0, 'defunct': ?1 }", sort = "{ 'createdDate': -1 }")
   List<Tariff> findByOrganizationIdAndDefunct(String organizationId, boolean defunct);
}
