package com.org.hosply360.repository.globalMasterRepo;

import com.org.hosply360.constant.Enums.TemplateStatus;
import com.org.hosply360.dao.globalMaster.InsuranceProvider;
import com.org.hosply360.dao.globalMaster.Template;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TemplateRepository extends MongoRepository<Template,String> {

    @Query("{ 'id': ?0, 'defunct': ?1 }")
    Optional<Template> findByIdAndDefunct(String id, Boolean defunct);

    @Query("{'organization.id': ?0,'defunct': ?1}")
    List<Template> findAllByDefunct(String organizationId, boolean defunct);

    @Query(value = "{'organization.id': ?0, 'defunct': ?1, 'template_status': ?2}", sort = "{'createdDate': -1}")
    List<Template> findAllByDefunctAndTemplateStatus(String organizationId, boolean defunct, TemplateStatus templateStatus);
}
