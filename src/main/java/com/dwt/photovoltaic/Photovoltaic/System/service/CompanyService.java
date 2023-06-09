package com.dwt.photovoltaic.Photovoltaic.System.service;

import com.dwt.photovoltaic.Photovoltaic.System.model.Company;
import com.dwt.photovoltaic.Photovoltaic.System.model.User;
import com.dwt.photovoltaic.Photovoltaic.System.repository.CompanyRepository;
import com.dwt.photovoltaic.Photovoltaic.System.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {

    @Autowired
    CompanyRepository companyRepository;

    public Company registerCompany(Company company){
        company.setPassword(new BCryptPasswordEncoder().encode(company.getPassword()));
        Company companyObj = companyRepository.save(company);
        return companyObj;
    }

    public List<Company> findAllCompanies() {
        return companyRepository.findAll();
    }
}


