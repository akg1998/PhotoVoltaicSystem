package com.dwt.photovoltaic.Photovoltaic.System.service;

import com.dwt.photovoltaic.Photovoltaic.System.controller.UserController;
import com.dwt.photovoltaic.Photovoltaic.System.model.User;
import com.dwt.photovoltaic.Photovoltaic.System.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    public boolean authenticate(String username, String password) {
        if (username != null && password != null) {
            User user = userRepository.findByUsername(username);
            if (user != null) {
                if (username.equals(user.getUsername()) && password.equals(user.getPassword())) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        else {
            return false;
        }
    }
}
