package com.dwt.photovoltaic.Photovoltaic.System.controller;

import com.dwt.photovoltaic.Photovoltaic.System.model.*;
import com.dwt.photovoltaic.Photovoltaic.System.service.CompanyService;
import com.dwt.photovoltaic.Photovoltaic.System.service.JwtTokenUtil;
import com.dwt.photovoltaic.Photovoltaic.System.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(value = "/auth")
public class AuthApiController {
    @Autowired
    AuthenticationManager authManager;
    @Autowired
    JwtTokenUtil jwtUtil;

    @Autowired
    UserService userService;

    @Autowired
    CompanyService companyService;

    @PostMapping(value="/checkUniqueUsername")
    @CrossOrigin
    public boolean isUsernameAvailable(@RequestBody String username){
       try{
           boolean isAvailable = userService.checkAvailability(username);
           return isAvailable;
       }
       catch(Exception e){
           ResponseMessage responseMessage = new ResponseMessage();
           responseMessage.setMessage("Multiple username present with same name, please try another!");
           return false;
       }

    }
    @PostMapping(value="/registerUser")
    @CrossOrigin
    public ResponseEntity<?> registerUser(@RequestBody User user){
        try {
            if (isUsernameAvailable(user.getUsername())) {
                User userObj = userService.registerUser(user);
                return new ResponseEntity<>(userObj, HttpStatus.OK);
            }else{
                ResponseMessage responseMessage = new ResponseMessage();
                responseMessage.setMessage("Username not available, please try another!");
                return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
            }
        }
        catch(Exception e){
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("Multiple username present with same name, please try another!");
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value="/registerCompany")
    @CrossOrigin
    public ResponseEntity<?> registerCompany(@RequestBody Company company){
        try {
            if (isUsernameAvailable(company.getUsername())) {
                Company companyObj = companyService.registerCompany(company);
                return new ResponseEntity<>(companyObj, HttpStatus.OK);
            }
            else{
                ResponseMessage responseMessage = new ResponseMessage();
                responseMessage.setMessage("Username not available, please try another!");
                return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
            }
        }
        catch(Exception e){
                ResponseMessage responseMessage = new ResponseMessage();
                responseMessage.setMessage("Multiple username present with same name, please try another!");
                return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping(value = "/userLogin")
    @CrossOrigin
    public ResponseEntity<?> loginUser(@RequestBody @Valid AuthRequest request) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword())
            );

            User user = (User) authentication.getPrincipal();
            String accessToken = jwtUtil.generateAccessTokenForUser(user);
            AuthResponse response = new AuthResponse(user.getUsername(), accessToken);

            return ResponseEntity.ok().body(response);

        } catch (Exception ex) {
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("Username or Password is incorrect");
            return new ResponseEntity<>(responseMessage, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping(value = "/companyLogin")
    @CrossOrigin
    public ResponseEntity<?> loginCompany(@RequestBody @Valid AuthRequest request) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword())
            );

            Company company = (Company) authentication.getPrincipal();
            String accessToken = jwtUtil.generateAccessTokenForCompany(company);
            AuthResponse response = new AuthResponse(company.getUsername(), accessToken);

            return ResponseEntity.ok().body(response);

        } catch (Exception ex) {
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("Username or Password is incorrect");
            return new ResponseEntity<>(responseMessage, HttpStatus.UNAUTHORIZED);
        }
    }
}