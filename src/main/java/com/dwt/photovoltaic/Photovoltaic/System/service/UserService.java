package com.dwt.photovoltaic.Photovoltaic.System.service;

import com.dwt.photovoltaic.Photovoltaic.System.model.Company;
import com.dwt.photovoltaic.Photovoltaic.System.model.ErrorResponse;
import com.dwt.photovoltaic.Photovoltaic.System.model.Project;
import com.dwt.photovoltaic.Photovoltaic.System.model.User;
import com.dwt.photovoltaic.Photovoltaic.System.repository.CompanyRepository;
import com.dwt.photovoltaic.Photovoltaic.System.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepo;
    @Autowired
    CompanyRepository companyRepo;

    public boolean checkAvailability(String username){
        User userObj = userRepo.findByUsername(username);
        if(userObj!=null){
            return false;
        }
        else{
            Company companyObj = companyRepo.findByUsername(username);
            if(companyObj!=null){
                return false;
            }
        }
        return true;
    }
    public boolean authenticate(String username, String password) {
        if (username != null && password != null) {
            User user = userRepo.findByUsername(username);
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
        User userObj = userRepo.save(user);
        return userObj;
    }


    public List<User> findAllUsers() {
        return userRepo.findAll();
    }

    public User getUserDetails(String username){
        User userObj = userRepo.findByUsername(username);
        return userObj;
    }

    public ResponseEntity<?> saveProjectDetails(String username, Project projectDetails) {
        if (username != null) {
            User userObj = userRepo.findByUsername(username);
            List<Project> project = userObj.getProjects();
            if (userObj != null) {
                if (project == null || project.isEmpty()) {
                    // No existing data, create a new list and add the new object
                    List<Project> newProjectList = new ArrayList<>();
                    newProjectList.add(projectDetails);
                    userObj.setProjects(newProjectList);
                    userRepo.save(userObj);
                    return new ResponseEntity<>(projectDetails, HttpStatus.OK);
                } else {
                        project.add(projectDetails);
                        userObj.setProjects(project);
                        userRepo.save(userObj);
                        return new ResponseEntity<>(projectDetails, HttpStatus.OK);
                    }
            }
            else {
                ErrorResponse errorResponse = new ErrorResponse();
                errorResponse.setMessage("Invalid Data! Contact administrator");
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
        }
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage("You are not valid user to perform this action!");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
