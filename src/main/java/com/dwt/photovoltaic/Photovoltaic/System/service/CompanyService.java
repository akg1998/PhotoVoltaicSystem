package com.dwt.photovoltaic.Photovoltaic.System.service;

import com.dwt.photovoltaic.Photovoltaic.System.model.Company;
import com.dwt.photovoltaic.Photovoltaic.System.model.ResponseMessage;
import com.dwt.photovoltaic.Photovoltaic.System.model.Product;
import com.dwt.photovoltaic.Photovoltaic.System.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CompanyService {

    @Autowired
    CompanyRepository companyRepo;

    public Company registerCompany(Company company) {
        company.setPassword(new BCryptPasswordEncoder().encode(company.getPassword()));
        Company companyObj = companyRepo.save(company);
        return companyObj;
    }

    public List<Company> findAllCompanies() {
        return companyRepo.findAll();
    }

    public Company getCompanyDetails(String username) {
        Company company = companyRepo.findByUsername(username);
        return company;
    }

    public ResponseEntity<?> saveProductDetails(String username, Product productDetails) {
        if (username != null) {
            Company companyObj = companyRepo.findByUsername(username);
            List<Product> products = companyObj.getProducts();
            //productDetails.setId(UUID.randomUUID().toString());
            if (companyObj != null) {
                if(products == null) {
                    List<Product> newProductList = new ArrayList<>();
                    newProductList.add(productDetails);
                    companyObj.setProducts(newProductList);
                    companyRepo.save(companyObj);
                    return new ResponseEntity<>(productDetails, HttpStatus.OK);
                }
                else{
                    products.add(productDetails);
                    companyObj.setProducts(products);
                    companyRepo.save(companyObj);
                    return new ResponseEntity<>(productDetails, HttpStatus.OK);
                }
            } else {
                ResponseMessage responseMessage = new ResponseMessage();
                responseMessage.setMessage("Company account is not present it might be deleted or inactive");
                return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
            }
        }
        else {
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("You are not valid user to perform this action!");
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }
}