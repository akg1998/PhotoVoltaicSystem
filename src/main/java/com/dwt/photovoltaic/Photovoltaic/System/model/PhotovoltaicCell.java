package com.dwt.photovoltaic.Photovoltaic.System.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhotovoltaicCell {

    private String weatherDate;
    private double electricityProduced;
    private int solarIrradiance;
    private double panelArea;
    private double systemLoss;
    private int cloudCover;
    private int sunHours;

    public void setPanelArea(double panelArea) {
        this.panelArea = Math.round(panelArea * 100.0) / 100.0;
    }

    public void setSystemLoss(double systemLoss) {
        this.systemLoss = Math.round(systemLoss * 100.0) / 100.0;
    }

}
