package com.org.hosply360.repository.globalMasterRepo;

import com.org.hosply360.dao.globalMaster.Language;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LanguageMasterRepository extends MongoRepository<Language, String> {

    @Query("{'code': ?0,'defunct':?1}")
    Optional<Language> findByCodeAndDefunct(String code, boolean defunct);

    @Query("{'id': ?0,'defunct':?1}")
    Optional<Language> findByIdAndDefunct(String id, boolean defunct);

    @Query(value = "{ 'organization.id': ?0, 'defunct': ?1 }", sort = "{ 'createdDate': -1 }")
    List<Language> findAllByDefunct(String organizationId, boolean defunct);

    @Query("{'id': {'$in': ?0}, 'defunct': ?1}")
    Optional<List<Language>> findAllByIdAndDefunct(List<String> ids, boolean defunct);



}
