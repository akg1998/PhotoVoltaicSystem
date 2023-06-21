package com.dwt.photovoltaic.Photovoltaic.System.repository;

import com.dwt.photovoltaic.Photovoltaic.System.model.Product;
import com.dwt.photovoltaic.Photovoltaic.System.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
    @Query("{ 'username' : ?0 }")
    User findByUsername(String username);

    @Query("{ 'status' : ?0 }")
    List<User> showUsersbyStatus(String status);

    @Query(value = "{ 'projects.products.productName' : ?0 }", exists = true)
    boolean existsByProjectsProductName(String productName);
}
