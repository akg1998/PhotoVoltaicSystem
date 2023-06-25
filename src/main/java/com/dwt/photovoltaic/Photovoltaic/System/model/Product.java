package com.dwt.photovoltaic.Photovoltaic.System.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

//    @Id
//    private String id;
    private String productName;
    private String orientation;
    private BigDecimal inclination;
    private BigDecimal area;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private Integer systemLoss;
    private String status; // It can have two values, either it will be ACTIVE or INACTIVE
    private String locationOfProduct;
    private List<PhotovoltaicCell> weatherInfo;

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude.setScale(4, BigDecimal.ROUND_HALF_UP);;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude.setScale(4, BigDecimal.ROUND_HALF_UP);;
    }

    // Setter method for inclination with two decimal places
    public void setInclination(BigDecimal inclination) {
        this.inclination = inclination.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    // Setter method for area with two decimal places
    public void setArea(BigDecimal area) {
        this.area = area.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}



