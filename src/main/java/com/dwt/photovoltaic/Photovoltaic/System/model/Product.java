package com.dwt.photovoltaic.Photovoltaic.System.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    private String id;
    private String productName;
    private String orientation;
    private BigDecimal powerPeak;
    private BigDecimal inclination;
    private BigDecimal area;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private BigDecimal cloudCover;
    private Integer systemLoss;

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude.setScale(2, BigDecimal.ROUND_HALF_UP);;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude.setScale(2, BigDecimal.ROUND_HALF_UP);;
    }

    public void setCloudCover(BigDecimal cloudCover) {
        this.cloudCover = cloudCover.setScale(2, BigDecimal.ROUND_HALF_UP);;
    }

    public void setPowerPeak(BigDecimal powerPeak) {
        this.powerPeak = powerPeak.setScale(2, BigDecimal.ROUND_HALF_UP);
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



