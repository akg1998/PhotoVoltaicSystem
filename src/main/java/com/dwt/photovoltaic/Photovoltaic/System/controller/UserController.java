package com.dwt.photovoltaic.Photovoltaic.System.controller;

import com.dwt.photovoltaic.Photovoltaic.System.model.Company;
import com.dwt.photovoltaic.Photovoltaic.System.model.User;
import com.dwt.photovoltaic.Photovoltaic.System.repository.UserRepository;
import com.dwt.photovoltaic.Photovoltaic.System.service.CompanyService;
import com.dwt.photovoltaic.Photovoltaic.System.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;
    @Autowired
    CompanyService companyService;


    @RequestMapping(value="/")
    @CrossOrigin
    public void redirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui.html");
    }

    @GetMapping(value="/users")
    @CrossOrigin
    public List<User> getUsers(){
        List<User> listOfUsers = userService.findAllUsers();
        return listOfUsers;
    }

    @GetMapping(value= "/user")
    @CrossOrigin
    public User getUserDetails(Principal principal){
        User userDetails = userRepository.findByUsername(principal.getName());
        return userDetails;
    }

    @GetMapping(value="/companies")
    @CrossOrigin
    public List<Company> getCompanies(){
        List<Company> listOfCompanies = companyService.findAllCompanies();
        return listOfCompanies;
    }
}
