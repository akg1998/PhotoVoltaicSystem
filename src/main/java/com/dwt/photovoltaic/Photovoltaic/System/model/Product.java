package com.dwt.photovoltaic.Photovoltaic.System.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    private ObjectId id;
    private String productName;
    private Float powerPeak;
    private String orientation;
    private Float inclination;
    private Float area;
    private Float longitude;
    private Float latitude;
    private Float cloudCover;
    private Integer systemLoss;

}



