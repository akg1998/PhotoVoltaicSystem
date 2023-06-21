package com.dwt.photovoltaic.Photovoltaic.System.controller;

import com.dwt.photovoltaic.Photovoltaic.System.model.*;
import com.dwt.photovoltaic.Photovoltaic.System.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    @Autowired
    UserService userService;


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
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("Invalid Data! Contact administrator");
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping(value="/updateUser")
    @CrossOrigin
    public ResponseEntity<?> updateUserAccount(@RequestBody User user, Principal principal){
        ResponseEntity<?> userObj = userService.updateAccountForUser(user, principal.getName());
        return new ResponseEntity<>(userObj, userObj.getStatusCode());
    }

    @DeleteMapping(value="/deleteUser")
    @CrossOrigin
    public ResponseEntity<?> deleteUserAccount(Principal principal){
        ResponseEntity<?> userObj = userService.deleteAccountUser(principal.getName());
        return new ResponseEntity<>(userObj, userObj.getStatusCode());
    }

    @GetMapping(value= "/getDeletedAccounts")
    @CrossOrigin
    public ResponseEntity<?> fetchDeletedAccounts(){
        List<User> userObj = userService.getAllDeletedUsers();
        return ResponseEntity.ok(userObj);
    }

    // Save project and product in single json
    @PostMapping(value="/saveUserProject")
    @CrossOrigin
    public ResponseEntity<?> saveProjectForUser(@RequestBody Project projectDetails, Principal principal){
        try {
            ResponseEntity<?> project = userService.saveProjectDetails(principal.getName(), projectDetails);
            return ResponseEntity.ok(project.getBody());
        }
        catch(Exception e){
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("Invalid Data! Contact administrator");
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/showProjects")
    @CrossOrigin
    public ResponseEntity<?> getProjects(Principal principal){
        ResponseEntity<?> projects = userService.fetchAllProjects(principal.getName());
        return new ResponseEntity<>(projects, projects.getStatusCode());
    }

    @PostMapping(value="/saveProduct")
    @CrossOrigin
    public ResponseEntity<?> saveProduct(@RequestBody Project project,  Principal principal){
        try {
            ResponseEntity<?> value =  userService.saveProductDetails(project, principal.getName());
            return new ResponseEntity<>(value.getBody(), value.getStatusCode());
        }
        catch(Exception e){
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage(String.valueOf(e));
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping(value="/updateProduct")
    @CrossOrigin
    public ResponseEntity<?> updateProduct(@RequestBody Project updatedProduct, Principal principal){
        ResponseEntity<?> project = userService.updateProduct(updatedProduct, principal.getName());
        return new ResponseEntity<>(project.getBody(), HttpStatus.OK);
    }

    @DeleteMapping(value = "/deleteProduct")
    @CrossOrigin
    public ResponseEntity<?> deleteProduct(@RequestBody Project productDetails, Principal principal){
        ResponseEntity<?> deletedProduct = userService.deleteProduct(productDetails,principal.getName());
        return new ResponseEntity<>(deletedProduct, HttpStatus.OK);
    }

    @GetMapping(value="/showCompanyProducts")
    @CrossOrigin
    public ResponseEntity<?> showAllCompanyProducts(Principal principal){
        ResponseEntity<?> allProducts = userService.getAllProducts(principal.getName());
        return new ResponseEntity<>(allProducts, allProducts.getStatusCode());
    }
}
