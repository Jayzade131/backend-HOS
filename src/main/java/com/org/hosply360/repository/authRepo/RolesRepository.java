package com.org.hosply360.repository.authRepo;

import com.org.hosply360.dao.auth.Roles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RolesRepository extends MongoRepository<Roles, String> {

   Optional<Roles> findByName(String name);
    @Query("{id: ?0, defunct: ?1}")
    Optional<Roles> findByIdAndDefunct(String id, boolean defunct);

    @Query("{'defunct': ?0}")
    Page<Roles> findAllByDefunct(boolean defunct, Pageable pageable);

    @Query("{'id': {'$in': ?0}, 'defunct': ?1}")
Optional<List<Roles>> findAllByIDAndDefunct( List<String> id, boolean defunct);
}
