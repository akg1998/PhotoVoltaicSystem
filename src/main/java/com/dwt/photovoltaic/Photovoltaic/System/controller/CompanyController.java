package com.dwt.photovoltaic.Photovoltaic.System.controller;

import com.dwt.photovoltaic.Photovoltaic.System.model.Company;
import com.dwt.photovoltaic.Photovoltaic.System.model.ResponseMessage;
import com.dwt.photovoltaic.Photovoltaic.System.model.Product;
import com.dwt.photovoltaic.Photovoltaic.System.service.CompanyService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@SecurityRequirement(name="jwtAuth")
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

    @PatchMapping(value="/updateCompany")
    @CrossOrigin
    public ResponseEntity<?> updateCompany(@RequestBody Company companyDetails, Principal principal){
        try {
            ResponseEntity<?> company = companyService.updateCompany(companyDetails,principal.getName());
            return new ResponseEntity<>(company, company.getStatusCode());
        }
        catch(Exception e){
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage(String.valueOf(e));
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value="/deleteCompany")
    @CrossOrigin
    public ResponseEntity<?> deleteCompany(@RequestBody Company companyDetails, Principal principal){
        try {
            ResponseEntity<?> deletedCompany = companyService.deleteCompany(companyDetails,principal.getName());
            return new ResponseEntity<>(deletedCompany, deletedCompany.getStatusCode());
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

    @PatchMapping(value="/updateCompanyProduct")
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

    @DeleteMapping(value="/deleteCompanyProduct")
    @CrossOrigin
    public ResponseEntity<?> deleteCompanyProduct(@RequestBody Product productDetails, Principal principal) {
        try {
            ResponseEntity<?> deletedProduct = companyService.deleteProduct(principal.getName(), productDetails);
            return new ResponseEntity<>(deletedProduct, deletedProduct.getStatusCode());
        } catch (Exception e) {
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage(String.valueOf(e));
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

}
