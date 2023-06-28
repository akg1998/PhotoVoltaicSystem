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
        try {
            List<User> listOfUsers = userService.findAllUsers();
            return new ResponseEntity<>(listOfUsers, HttpStatus.OK);
        }
        catch(Exception e){
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage(String.valueOf(e));
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
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
            responseMessage.setMessage(String.valueOf(e));
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping(value="/updateUser")
    @CrossOrigin
    public ResponseEntity<?> updateUserAccount(@RequestBody User user, Principal principal){
        try {
            ResponseEntity<?> userObj = userService.updateAccountForUser(user, principal.getName());
            return new ResponseEntity<>(userObj, userObj.getStatusCode());
        }
        catch(Exception e){
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage(String.valueOf(e));
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value="/deleteUser")
    @CrossOrigin
    public ResponseEntity<?> deleteUserAccount(Principal principal){
        try {
            ResponseEntity<?> userObj = userService.deleteAccountUser(principal.getName());
            return new ResponseEntity<>(userObj, userObj.getStatusCode());
        }
        catch(Exception e){
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage(String.valueOf(e));
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value= "/getDeletedAccounts")
    @CrossOrigin
    public ResponseEntity<?> fetchDeletedAccounts(){
        try {
            List<User> userObj = userService.getAllDeletedUsers();
            return ResponseEntity.ok(userObj);
        }
        catch(Exception e){
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage(String.valueOf(e));
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
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
            responseMessage.setMessage(String.valueOf(e));
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value="/updateProject")
    @CrossOrigin
    public ResponseEntity<?> updateProject(@RequestBody Project projectInfo, Principal principal){
        try {
            ResponseEntity<?> updatedProject = userService.getUpdatedProject(projectInfo, principal.getName());
            return new ResponseEntity<>(updatedProject, updatedProject.getStatusCode());
        }
        catch(Exception e){
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage(String.valueOf(e));
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value="/deleteProject")
    @CrossOrigin
    public ResponseEntity<?> deleteProject(@RequestBody Project projectInfo, Principal principal){
        try {
            ResponseEntity<?> deletedProject = userService.deleteProject(projectInfo, principal.getName());
            return new ResponseEntity<>(deletedProject, deletedProject.getStatusCode());
        }
        catch(Exception e){
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage(String.valueOf(e));
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/showProjects")
    @CrossOrigin
    public ResponseEntity<?> getProjects(Principal principal){
        try {
            ResponseEntity<?> projects = userService.fetchAllProjects(principal.getName());
            return new ResponseEntity<>(projects, projects.getStatusCode());
        }
        catch(Exception e){
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage(String.valueOf(e));
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
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
        try {
            ResponseEntity<?> project = userService.updateProduct(updatedProduct, principal.getName());
            return new ResponseEntity<>(project.getBody(), project.getStatusCode());
        }
        catch(Exception e){
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage(String.valueOf(e));
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/deleteProduct")
    @CrossOrigin
    public ResponseEntity<?> deleteProduct(@RequestBody Project productDetails, Principal principal){
        try {
            ResponseEntity<?> deletedProduct = userService.deleteProduct(productDetails, principal.getName());
            return new ResponseEntity<>(deletedProduct, deletedProduct.getStatusCode());
        }
        catch(Exception e){
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage(String.valueOf(e));
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value="/showCompanyProducts")
    @CrossOrigin
    public ResponseEntity<?> showAllCompanyProducts(Principal principal){
        try {
            ResponseEntity<?> allProducts = userService.getAllProducts(principal.getName());
            return new ResponseEntity<>(allProducts, allProducts.getStatusCode());
        }
        catch(Exception e){
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage(String.valueOf(e));
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value="/showProjects/{projectName}")
    @CrossOrigin
    public ResponseEntity<?> showAllProducts(@PathVariable String projectName, Principal principal){
        try {
            ResponseEntity<?> projects = userService.getProjectsByProjectName(projectName, principal.getName());
            return new ResponseEntity<>(projects.getBody(), projects.getStatusCode());
        }
        catch(Exception e){
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage(String.valueOf(e));
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value="/generateReport")
    @CrossOrigin
    public ResponseEntity<?> generateReport(@RequestBody Project projectDetails, Principal principal){
        try {
            ResponseEntity<?> result = userService.generateReport(projectDetails, principal.getName());
            return new ResponseEntity<>(result, result.getStatusCode());
        }
        catch(Exception e){
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage(String.valueOf(e));
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value="/generateGraph")
    @CrossOrigin
    public ResponseEntity<?> generateGraph(Principal principal){
        try {
            ResponseEntity<?> graphData = userService.generateGraphData(principal.getName());
            return new ResponseEntity<>(graphData.getBody(), graphData.getStatusCode());
        }
        catch(Exception e){
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage(String.valueOf(e));
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value="/projects/{typeOfProject}")
    @CrossOrigin
    public ResponseEntity<?> showTypeOfProject(@PathVariable String typeOfProject, Principal principal){
        try {
            ResponseEntity<?> listOfProjects = userService.showActiveOrOldProjects(typeOfProject, principal.getName());
            return new ResponseEntity<>(listOfProjects.getBody(), listOfProjects.getStatusCode());
        }
        catch(Exception e){
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage(String.valueOf(e));
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }
}
