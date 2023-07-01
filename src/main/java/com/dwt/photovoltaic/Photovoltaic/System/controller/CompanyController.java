package com.dwt.photovoltaic.Photovoltaic.System.controller;

import com.dwt.photovoltaic.Photovoltaic.System.model.Company;
import com.dwt.photovoltaic.Photovoltaic.System.model.ResponseMessage;
import com.dwt.photovoltaic.Photovoltaic.System.model.Product;
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
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage(String.valueOf(e));
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    // Save project and product in single json
    @PostMapping(value="/saveCompanyProduct")
    @CrossOrigin
    public ResponseEntity<?> saveProductForCompany(@RequestBody Product productDetails, Principal principal){
        try {
            ResponseEntity<?> message = companyService.saveProductDetails(principal.getName(), productDetails);
            return new ResponseEntity<>(message, message.getStatusCode());
        }
        catch(Exception e){
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage(String.valueOf(e));
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value="/updateCompanyProduct")
    @CrossOrigin
    public ResponseEntity<?> updateCompanyProduct(@RequestBody Product productDetails, Principal principal){
        try{
            ResponseEntity<?> updatedProduct = companyService.updateProductDetails(principal.getName(), productDetails);
            return new ResponseEntity<>(updatedProduct, updatedProduct.getStatusCode());
        }
        catch(Exception e){
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage(String.valueOf(e));
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

}
