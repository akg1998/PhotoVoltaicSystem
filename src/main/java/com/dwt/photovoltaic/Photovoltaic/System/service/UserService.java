package com.dwt.photovoltaic.Photovoltaic.System.service;

import com.dwt.photovoltaic.Photovoltaic.System.model.*;
import com.dwt.photovoltaic.Photovoltaic.System.repository.CompanyRepository;
import com.dwt.photovoltaic.Photovoltaic.System.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    UserRepository userRepo;
    @Autowired
    CompanyRepository companyRepo;
    private final MongoTemplate mongoTemplate;


    public UserService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

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
            projectDetails.setId(UUID.randomUUID().toString());
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

    // Two ways -  first scan whole project using projectId or second one is scan by userId and then ProjectId
    public ResponseEntity<?> saveProductDetails(Project projectObj, String username) {
        User user = userRepo.findByUsername(username);
        if (user != null) {
            if (projectObj.getId()!=null) {
                Project project = user.getProjects().stream()
                        .filter(p -> p.getId().equals(projectObj.getId()))
                        .findFirst()
                        .orElse(null);
                if (project != null) {
                        List<Product> products = projectObj.getProducts();
                    if (project.getProducts() == null) {
                        project.setProducts(new ArrayList<>()); // Initialize the products list
                    }
                        for(Product product : products){
                            product.setId(UUID.randomUUID().toString());
                            project.getProducts().add(product);
                            userRepo.save(user);
                            return new ResponseEntity<>(product, HttpStatus.OK);
                        }
                    }
                    else {
                        ErrorResponse errorResponse = new ErrorResponse();
                        errorResponse.setMessage("No products given in request, please add some products!");
                        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
                    }

                }
                else{
                    ErrorResponse errorResponse = new ErrorResponse();
                    errorResponse.setMessage("Given project is not present in database, it might be deleted!");
                    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
                }
            }
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage("You are not valid user to perform this action!");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> updateAccountForUser(User user, String username) {
        User userObj = userRepo.findByUsername(username);
        if(userObj!=null && userObj.getUsername().equals(user.getUsername())){
            userObj.setUserType(user.getUserType());
            userObj.setFullName(user.getFullName());
            userObj.setEmailId(user.getEmailId());
            userObj.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            userObj.setContactNo(user.getContactNo());
            userObj = userRepo.save(userObj);
            return new ResponseEntity<>(userObj, HttpStatus.OK);
        }
        else{
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage("You are not valid user to perform this action!");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> deleteAccountUser(String username) {
        if(username!=null){
            User userObj = userRepo.findByUsername(username);
            if(userObj!=null) {
                userRepo.delete(userObj);
                return new ResponseEntity<>(true,HttpStatus.OK);
            }
            else{
                ErrorResponse errorResponse = new ErrorResponse();
                errorResponse.setMessage("User not present!");
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
        }
        else{
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage("Invalid action against User, Contact Administrator!");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> fetchAllProjects(String username) {
        if(username!=null){
            User userObj = userRepo.findByUsername(username);
            if(userObj!=null){
                List<Project> projects = userObj.getProjects();
                if(projects!=null){
                    return new ResponseEntity<>(projects, HttpStatus.OK);
                }
                else{
                    ErrorResponse errorResponse = new ErrorResponse();
                    errorResponse.setMessage("No Projects are created yet!");
                    return new ResponseEntity<>(errorResponse, HttpStatus.OK);
                }
            }
            else{
                ErrorResponse errorResponse = new ErrorResponse();
                errorResponse.setMessage("User not present!");
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
        }
        else{
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage("Not valid User!");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }
}
