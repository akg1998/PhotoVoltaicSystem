package com.dwt.photovoltaic.Photovoltaic.System.controller;

import javax.validation.Valid;

import com.dwt.photovoltaic.Photovoltaic.System.model.AuthRequest;
import com.dwt.photovoltaic.Photovoltaic.System.model.AuthResponse;
import com.dwt.photovoltaic.Photovoltaic.System.model.Company;
import com.dwt.photovoltaic.Photovoltaic.System.model.User;
import com.dwt.photovoltaic.Photovoltaic.System.repository.UserRepository;
import com.dwt.photovoltaic.Photovoltaic.System.service.CompanyService;
import com.dwt.photovoltaic.Photovoltaic.System.service.JwtTokenUtil;
import com.dwt.photovoltaic.Photovoltaic.System.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(value = "/auth")
public class AuthApiController {
    @Autowired
    AuthenticationManager authManager;
    @Autowired
    JwtTokenUtil jwtUtil;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    CompanyService companyService;

    @PostMapping(value="/registerUser")
    @CrossOrigin
    public User registerUser(@RequestBody User user){
        User userObj = userService.registerUser(user);
        return userObj;
    }

    @PostMapping(value="/registerCompany")
    @CrossOrigin
    public Company registerCompany(@RequestBody Company company){
        Company companyObj = companyService.registerCompany(company);
        return companyObj;
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
            String accessToken = jwtUtil.generateAccessToken(user);
            AuthResponse response = new AuthResponse(user.getUsername(), accessToken);

            return ResponseEntity.ok().body(response);

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}