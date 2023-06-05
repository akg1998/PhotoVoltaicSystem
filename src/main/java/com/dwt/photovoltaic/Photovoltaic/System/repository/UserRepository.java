package com.dwt.photovoltaic.Photovoltaic.System.repository;

import com.dwt.photovoltaic.Photovoltaic.System.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserRepository extends MongoRepository<User, String> {
    @Query("{ 'username' : ?0 }")
    public User findByUsername(String username);
}
