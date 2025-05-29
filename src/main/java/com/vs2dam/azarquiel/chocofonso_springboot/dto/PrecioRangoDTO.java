package com.vs2dam.azarquiel.chocofonso_springboot.dto;

public class PrecioRangoDTO {
    private Double min;
    private Double max;

    public PrecioRangoDTO(Double min, Double max) {
        this.min = min;
        this.max = max;
    }

    public Double getMin() { return min; }
    public Double getMax() { return max; }
    public void setMin(Double min) { this.min = min; }
    public void setMax(Double max) { this.max = max; }
}
