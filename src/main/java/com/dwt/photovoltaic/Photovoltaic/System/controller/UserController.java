package com.dwt.photovoltaic.Photovoltaic.System.controller;

import com.dwt.photovoltaic.Photovoltaic.System.model.User;
import com.dwt.photovoltaic.Photovoltaic.System.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;
    @RequestMapping(value="/")
    public void redirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui.html");
    }

    @GetMapping(value="/users")
    public List<User> getUsers(){
        return userRepository.findAll();
    }

    @PostMapping(value="/registerUser")
    public User registerUser(@RequestBody User user){
        return userRepository.save(user);
    }
}
