package com.org.hosply360.repository.globalMasterRepo;

import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dto.globalMasterDTO.OrganizationDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;


public interface OrganizationMasterRepository extends MongoRepository<Organization, String> {

    @Query("{'id': ?0,'defunct': ?1}")
    Optional<Organization> findByIdAndDefunct(String id, boolean defunct);

    @Query("{'organizationCode': ?0,'defunct':?1}")
    Optional<Organization> findByOrganizationCodeAndDefunct(String organizationCode, boolean defunct);

    @Query("{'id': {'$in':?0}, 'defunct': ?1}")
    Optional<List<Organization>> findAllByIDAndDefunct(List<String> id, boolean defunct);

    @Query(value = "{'defunct': ?0}")
    List<Organization> findAllByDefuncts(Boolean defunct);

}
