package com.org.hosply360.repository.authRepo;

import com.org.hosply360.dao.auth.RoleModuleMapping;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RoleModuleRepository extends MongoRepository<RoleModuleMapping, String> {


    List<RoleModuleMapping> findAllByRoles_Id(String roleIds);

}


