package com.org.hosply360.repository.authRepo;

import com.org.hosply360.dao.auth.Access;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;


public interface AccessRepository extends MongoRepository<Access, String> {

    @Query("{'accessName': ?0}")
    Optional<Access> findByAccessName(String accessName);

    @Query("{id: ?0, defunct: ?1}")
    Access findByIdInAndDefunct(String id, boolean defunct);

    @Query("{'defunct': ?0}")
    Page<Access> findAllByDefunct(boolean defunct, Pageable pageable);
}
