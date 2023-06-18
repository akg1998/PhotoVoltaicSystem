package com.dwt.photovoltaic.Photovoltaic.System.controller;

import com.dwt.photovoltaic.Photovoltaic.System.model.Company;
import com.dwt.photovoltaic.Photovoltaic.System.model.ErrorResponse;
import com.dwt.photovoltaic.Photovoltaic.System.model.Project;
import com.dwt.photovoltaic.Photovoltaic.System.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class CompanyController {

    @Autowired
    CompanyService companyService;

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

    // Save project and product in single json
    @PostMapping(value="/saveCompanyProject")
    @CrossOrigin
    public ResponseEntity<?> saveProjectForCompany(@RequestBody Project projectDetails, Principal principal){
        try {
            ResponseEntity<?> message = companyService.saveProjectDetails(principal.getName(), projectDetails);
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        catch(Exception e){
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage("Invalid Data! Contact administrator");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }
}
