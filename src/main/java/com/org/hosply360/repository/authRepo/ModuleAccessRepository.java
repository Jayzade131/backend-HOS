package com.org.hosply360.repository.authRepo;

import com.org.hosply360.dao.auth.ModuleAccessMapping;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ModuleAccessRepository extends MongoRepository<ModuleAccessMapping,String> {

  List<ModuleAccessMapping> findAllByModules_Id(String moduleId);
}
