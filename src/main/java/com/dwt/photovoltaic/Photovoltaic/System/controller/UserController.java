package com.dwt.photovoltaic.Photovoltaic.System.controller;

import com.dwt.photovoltaic.Photovoltaic.System.model.Company;
import com.dwt.photovoltaic.Photovoltaic.System.model.ErrorResponse;
import com.dwt.photovoltaic.Photovoltaic.System.model.User;
import com.dwt.photovoltaic.Photovoltaic.System.service.CompanyService;
import com.dwt.photovoltaic.Photovoltaic.System.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

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
    public ResponseEntity<?> getUsers(){
        List<User> listOfUsers = userService.findAllUsers();
        return new ResponseEntity<>(listOfUsers, HttpStatus.OK);
    }

    @GetMapping(value= "/user")
    @CrossOrigin
    public ResponseEntity<?> getUserDetails(Principal principal){
        try {
            User userDetails = userService.getUserDetails(principal.getName());
            return new ResponseEntity<>(userDetails, HttpStatus.OK);
        }
        catch(Exception e){
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage("Invalid Data! Contact administrator");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value="/company")
    @CrossOrigin
    public ResponseEntity<?> getCompanyDetails(Principal principal){
        try {
            Company company = companyService.getCompanyDetails(principal.getName());
            return new ResponseEntity<>(company, HttpStatus.OK);
        }
        catch(Exception e){
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage("Invalid Data! Contact administrator");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }
}
