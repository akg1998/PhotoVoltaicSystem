package com.dwt.photovoltaic.Photovoltaic.System.repository;

import com.dwt.photovoltaic.Photovoltaic.System.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}
