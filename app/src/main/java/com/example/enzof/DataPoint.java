package com.example.enzof;

public class DataPoint {
    public int getxValue() {
        return xValue;
    }
    public void setxValue(int xValue) {
        this.xValue = xValue;
    }
    public int getyValue() {
        return yValue;
    }
    public void setyValue(int yValue) {
        this.yValue = yValue;
    }
    public DataPoint(){
    }
    public DataPoint(int xValue, int yValue) {
        this.xValue = xValue;
        this.yValue = yValue;
    }
    private int xValue;
    private int yValue;
}
