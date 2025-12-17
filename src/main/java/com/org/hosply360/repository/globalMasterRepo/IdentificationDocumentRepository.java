package com.org.hosply360.repository.globalMasterRepo;

import com.org.hosply360.dao.globalMaster.IdentificationDocument;
import com.org.hosply360.dto.globalMasterDTO.IdentificationResDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IdentificationDocumentRepository  extends MongoRepository<IdentificationDocument, String>
{

    Optional<IdentificationDocument> findByCode(String code);
    @Query("{id: ?0, defunct: ?1}")
    Optional<IdentificationDocument> findByIdandDefunct(String id, boolean defunct);
    @Query(value = "{ 'organization.id': ?0, 'defunct': ?1 }", sort = "{ 'createdDate': -1 }")
    List<IdentificationDocument> findAllByDefunct(String organizationId, boolean defunct);

    @Query("{'_id': {$in: ?0}, 'defunct': ?1}")
    List<IdentificationResDTO> findByIdAndDefunct(List<String> ids, boolean defunct);




}
