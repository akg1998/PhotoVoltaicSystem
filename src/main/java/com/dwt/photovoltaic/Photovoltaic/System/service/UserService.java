package com.dwt.photovoltaic.Photovoltaic.System.service;

import com.dwt.photovoltaic.Photovoltaic.System.controller.UserController;
import com.dwt.photovoltaic.Photovoltaic.System.model.User;
import com.dwt.photovoltaic.Photovoltaic.System.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    public boolean authenticate(String username, String password) {
        if (username != null && password != null) {
            User user = userRepository.findByUsername(username);
            if (user != null) {
                return username.equals(user.getUsername()) && password.equals(user.getPassword());
            } else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    public User registerUser(User user){
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        User userObj = userRepository.save(user);
        return userObj;
    }


    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
}
