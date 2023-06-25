package com.dwt.photovoltaic.Photovoltaic.System.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Project {

//    @Id
//    private String id;
    private String projectName;
    private String description;
    private String status;  // It can have two values read-only and ACTIVE
    private List<Product> products;
}
