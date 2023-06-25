package com.dwt.photovoltaic.Photovoltaic.System.model;

public class DataArrays {
    private String[] weatherDates;
    private double[] electricityProducedValues;

    public DataArrays(String[] weatherDates, double[] electricityProducedValues) {
        this.weatherDates = weatherDates;
        this.electricityProducedValues = electricityProducedValues;
    }

    public String[] getWeatherDates() {
        return weatherDates;
    }

    public double[] getElectricityProducedValues() {
        return electricityProducedValues;
    }
}
