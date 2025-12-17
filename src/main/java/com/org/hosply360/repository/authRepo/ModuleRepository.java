package com.org.hosply360.repository.authRepo;

import com.org.hosply360.dao.auth.Modules;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ModuleRepository extends MongoRepository<Modules, String> {
    Optional<Modules> findByModuleName(String moduleName);

    @Query("{id: ?0, defunct: ?1}")
    Optional<Modules> findByIdAndDefunct(String id, boolean defunct);

    @Query("{'defunct': ?0}")
    Page<Modules> findAllByDefunct(boolean defunct, Pageable pageable);

    @Query("{'id': {'$in': ?0},'defunct':?1}")
    Optional<List<Modules>> findAllByIdAndDefunct(List<String> id, boolean defunct);



}
