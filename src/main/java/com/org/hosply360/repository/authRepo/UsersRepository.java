package com.org.hosply360.repository.authRepo;

import com.org.hosply360.dao.auth.Users;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends MongoRepository<Users, String> {

    @Query("{username: ?0}")
    Optional<Users> findByUsername(String username);

    @Query("{mobileNo: ?0}")
    Optional<Users> findByMobileNo(String mobileNo);

    @Query("{'organizations.id': ?0, defunct: ?1}")
    List<Users> findAllByOrganizations_IdAndDefunct(String organizationId, boolean defunct);


    @Query("{ 'username': { $regex: ?0, $options: 'i' } }")
    Optional<Users> findByUsernameIgnoreCase(String username);

    @Query("{ 'email': { $regex: ?0, $options: 'i' } }")
    Optional<Users> findByEmailIgnoreCase(String email);


    @Query("{id: ?0, defunct: ?1}")
    Optional<Users> findByIdAndDefunct(String id, boolean defunct);


}

