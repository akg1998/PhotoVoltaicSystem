package com.dwt.photovoltaic.Photovoltaic.System.service;

import com.dwt.photovoltaic.Photovoltaic.System.model.Company;
import com.dwt.photovoltaic.Photovoltaic.System.model.ErrorResponse;
import com.dwt.photovoltaic.Photovoltaic.System.model.Project;
import com.dwt.photovoltaic.Photovoltaic.System.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

    public ResponseEntity<?> saveProjectDetails(String username, Project projectDetails) {
        if (username != null) {
            Company companyObj = companyRepo.findByUsername(username);
            List<Project> project = companyObj.getProjects();
            if (companyObj != null) {
                project.add(projectDetails);
                companyObj.setProjects(project);
                companyRepo.save(companyObj);
                return new ResponseEntity<>(projectDetails, HttpStatus.OK);
            } else {
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






