package com.dwt.photovoltaic.Photovoltaic.System.model;

public class DataEntry {
    private String weatherDate;
    private double electricityProduced;

    public DataEntry(String weatherDate, double electricityProduced) {
        this.weatherDate = weatherDate;
        this.electricityProduced = electricityProduced;
    }

    public String getWeatherDate() {
        return weatherDate;
    }

    public void setWeatherDate(String weatherDate) {
        this.weatherDate = weatherDate;
    }

    public double getElectricityProduced() {
        return electricityProduced;
    }

    public void setElectricityProduced(double electricityProduced) {
        this.electricityProduced = electricityProduced;
    }
}
