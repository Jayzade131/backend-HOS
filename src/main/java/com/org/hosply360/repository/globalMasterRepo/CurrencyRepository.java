package com.org.hosply360.repository.globalMasterRepo;

import com.org.hosply360.dao.globalMaster.Currency;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CurrencyRepository extends MongoRepository<Currency, String>
{
    Optional<Currency> findByCode(String code);
    @Query("{code: ?0, defunct: ?1}")
    Optional<Currency> findByCodeandDefunct(String code, boolean defunct);
    @Query("{id: ?0, defunct: ?1}")
    Optional<Currency> findByIdandDefunct(String id, boolean defunct);
    @Query(value = "{ 'organization.id': ?0, 'defunct': ?1 }", sort = "{ 'createdDate': -1 }")
     List<Currency> findAllByDefunct(String organizationId, boolean defunct);

}