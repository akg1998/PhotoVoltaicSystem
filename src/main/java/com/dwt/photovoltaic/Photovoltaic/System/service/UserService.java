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
        user.setStatus("ACTIVE");
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
           // projectDetails.setId(UUID.randomUUID().toString());
            if (userObj != null && userObj.getStatus().equals("ACTIVE")) {
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
                ResponseMessage responseMessage = new ResponseMessage();
                responseMessage.setMessage("User might be deleted or not valid user to perform this action");
                return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
            }
        }
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessage("You are not valid user to perform this action!");
        return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
    }

    // Two ways -  first scan whole project using projectId or second one is scan by userId and then ProjectId
    public ResponseEntity<?> saveProductDetails(Project projectObj, String username) {
        User user = userRepo.findByUsername(username);
        if (user != null && user.getStatus().equals("ACTIVE")) {
            if (projectObj.getProjectName()!=null) {
                Project project = user.getProjects().stream()
                        .filter(p -> p.getProjectName().equals(projectObj.getProjectName()))
                        .findFirst()
                        .orElse(null);
                if (project != null) {
                        List<Product> products = projectObj.getProducts();
                    if (project.getProducts() == null) {
                        project.setProducts(new ArrayList<>()); // Initialize the products list
                    }
                        for(Product product : products){
                            //product.setId(UUID.randomUUID().toString());
                            project.getProducts().add(product);
                            userRepo.save(user);
                            return new ResponseEntity<>(product, HttpStatus.OK);
                        }
                    }
                    else {
                        ResponseMessage responseMessage = new ResponseMessage();
                        responseMessage.setMessage("No products given in request, please add some products!");
                        return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
                    }

                }
                else{
                    ResponseMessage responseMessage = new ResponseMessage();
                    responseMessage.setMessage("Given project is not present in database, it might be deleted!");
                    return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
                }
            }
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessage("You are not valid user to perform this action!");
        return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> updateAccountForUser(User user, String username) {
        User userObj = userRepo.findByUsername(username);
        if(userObj!=null && userObj.getUsername().equals(user.getUsername()) && userObj.getStatus().equals("ACTIVE")){
            userObj.setUserType(user.getUserType());
            userObj.setFullName(user.getFullName());
            userObj.setEmailId(user.getEmailId());
            userObj.setContactNo(user.getContactNo());
            userObj = userRepo.save(userObj);
            return new ResponseEntity<>(userObj, HttpStatus.OK);
        }
        else{
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("You are not valid user to perform this action!");
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> deleteAccountUser(String username) {
        if(username!=null){
            User userObj = userRepo.findByUsername(username);
            if(userObj!=null && userObj.getStatus().equals("ACTIVE")) {
                userObj.setStatus("DELETED");
                userObj = userRepo.save(userObj);
                return new ResponseEntity<>(userObj,HttpStatus.OK);
            }
            else{
                ResponseMessage responseMessage = new ResponseMessage();
                responseMessage.setMessage("User not present!");
                return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
            }
        }
        else{
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("Invalid action against User, Contact Administrator!");
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
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
                    ResponseMessage responseMessage = new ResponseMessage();
                    responseMessage.setMessage("No Projects are created yet!");
                    return new ResponseEntity<>(responseMessage, HttpStatus.OK);
                }
            }
            else{
                ResponseMessage responseMessage = new ResponseMessage();
                responseMessage.setMessage("User not present!");
                return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
            }
        }
        else{
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("Not valid User!");
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    public List<User> getAllDeletedUsers() {
        List<User> deletedUsers = userRepo.showUsersbyStatus("DELETED");
        return deletedUsers;
    }

    public ResponseEntity<?> updateProduct(Project updatedProduct, String username) {
        User userObj = userRepo.findByUsername(username);
        if(userObj!=null && userObj.getStatus().equals("ACTIVE")) {
            if (updatedProduct.getProjectName() != null) {
                Project project = userObj.getProjects().stream()
                        .filter(p -> p.getProjectName().equals(updatedProduct.getProjectName()))
                        .findFirst()
                        .orElse(null);
                if (project != null) {
                    Product fetchProduct =  updatedProduct.getProducts().get(0);
                    // Only first product in project will be fetched using this.
                    Product product = project.getProducts().stream()
                            .filter(p -> p.getProductName().equals(fetchProduct.getProductName()))
                            .findFirst()
                            .orElse(null);
                    if (product != null) {
                        List<Product> listOfProducts = new ArrayList<>();
                        product.setArea(fetchProduct.getArea());
                        product.setInclination(fetchProduct.getInclination());
                        product.setOrientation(fetchProduct.getOrientation());
                        product.setLongitude(fetchProduct.getLongitude());
                        product.setLatitude(fetchProduct.getLatitude());
                        product.setCloudCover(fetchProduct.getCloudCover());
                        product.setSystemLoss(fetchProduct.getSystemLoss());
                        product.setPowerPeak(fetchProduct.getPowerPeak());
                        listOfProducts.add(product);
                        project.setProducts(listOfProducts);
                        userRepo.save(userObj);
                        return new ResponseEntity<>(project, HttpStatus.OK);
                    }
                    else{
                        ResponseMessage responseMessage = new ResponseMessage();
                        responseMessage.setMessage("Product name is empty or given Product is not present");
                        return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
                    }
                }
                else{
                    ResponseMessage responseMessage = new ResponseMessage();
                    responseMessage.setMessage("Project is not present it might be deleted or inactive");
                    return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
                }
            }
            else{
                ResponseMessage responseMessage = new ResponseMessage();
                responseMessage.setMessage("Project name is empty");
                return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
            }
        }
        else{
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("Not valid User!");
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> deleteProduct(Project productDetails, String username) {
        User userObj = userRepo.findByUsername(username);
        if(userObj!=null && userObj.getStatus().equals("ACTIVE")) {
            if (productDetails.getProjectName() != null) {
                Project project = userObj.getProjects().stream()
                        .filter(p -> p.getProjectName().equals(productDetails.getProjectName()))
                        .findFirst()
                        .orElse(null);
                if (project != null) {
                    Product fetchProduct = productDetails.getProducts().get(0);
                    List<Product> listOfProducts = project.getProducts();
                    listOfProducts.removeIf(product -> product.getProductName().equals(fetchProduct.getProductName()));
                    userRepo.save(userObj);
                    ResponseMessage responseMessage = new ResponseMessage();
                    responseMessage.setMessage("Product deleted successfully");
                    return new ResponseEntity<>(responseMessage, HttpStatus.OK);
                }
                else{
                    ResponseMessage responseMessage = new ResponseMessage();
                    responseMessage.setMessage("Project is not present it might be deleted or inactive");
                    return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
                }
            }
            else{
                ResponseMessage responseMessage = new ResponseMessage();
                responseMessage.setMessage("Project name is empty");
                return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
            }
        }
        else{
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("Not valid User!");
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> getAllProducts(String username) {
        User userObj = userRepo.findByUsername(username);
        if(userObj!=null){
            List<Company> companies = companyRepo.findAll();
            if(companies!=null){
                List<Product> products = new ArrayList<>();
                for (Company company: companies){
                    if(company.getProducts() != null) {
                        products.addAll(company.getProducts());
                    }
                }
                return new ResponseEntity<>(products, HttpStatus.OK);
            }
            else{
                ResponseMessage responseMessage = new ResponseMessage();
                responseMessage.setMessage("No Companies are present!");
                return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
            }
        }
        else{
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("Not valid User!");
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }
}
