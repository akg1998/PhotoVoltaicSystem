package com.dwt.photovoltaic.Photovoltaic.System.repository;

import com.dwt.photovoltaic.Photovoltaic.System.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
    @Query("{ 'username' : ?0 }")
    User findByUsername(String username);

    @Query("{ 'status' : ?0 }")
    List<User> showUsersbyStatus(String status);

    @Query(value = "{ 'projects': { $elemMatch: { 'projectName': ?0, 'products.productName': ?1 } } }", exists = true)
    boolean existsByProjectNameAndProductName(String projectName, String productName);
}
