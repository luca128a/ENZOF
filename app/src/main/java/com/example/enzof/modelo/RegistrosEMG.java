package com.example.enzof.modelo;

import java.util.ArrayList;

public class RegistrosEMG {


    public String getPresento() {
        return presento;
    }

    public void setPresento(String presento) {
        this.presento = presento;
    }

    public Double getTiempof() {
        return tiempof;
    }

    public void setTiempof(Double tiempof) {
        this.tiempof = tiempof;
    }

    public String getFechadia() {
        return fechadia;
    }

    public void setFechadia(String fechadia) {
        this.fechadia = fechadia;
    }

    public String getMusculo() {
        return musculo;
    }

    public void setMusculo(String musculo) {
        this.musculo = musculo;
    }

    public ArrayList<Double> getVector() {
        return vector;
    }

    public void setVector(ArrayList<Double>  vector) {
        this.vector = vector;
    }

    public Float getPeso() {
        return peso;
    }

    public void setPeso(Float peso) {
        this.peso = peso;
    }

    public String getLadocorporal() {
        return ladocorporal;
    }

    public void setLadocorporal(String ladocorporal) {
        this.ladocorporal = ladocorporal;
    }

    public ArrayList<Double> getVectorF() {
        return vectorF;
    }

    public void setVectorF(ArrayList<Double>  vectorF) {
        this.vectorF = vectorF;
    }

    public String getTipoejercicio() {
        return tipoejercicio;
    }

    public void setTipoejercicio(String tipoejercicio) {
        this.tipoejercicio = tipoejercicio;
    }

    public Double getLinea_regresion() {
        return linea_regresion;
    }

    public void setLinea_regresion(Double linea_regresion) {
        this.linea_regresion = linea_regresion;
    }

    public ArrayList<Double> getRMS() {
        return RMS;
    }

    public void setRMS(ArrayList<Double> RMS) {
        this.RMS = RMS;
    }

    public ArrayList<Double> getMF() {
        return MF;
    }

    public void setMF(ArrayList<Double> MF) {
        this.MF = MF;
    }


    public Double getLinea_regresionMF() {
        return linea_regresionMF;
    }

    public void setLinea_regresionMF(Double linea_regresionMF) {
        this.linea_regresion = linea_regresionMF;
    }



    public RegistrosEMG(){

    }

    public RegistrosEMG(String fechadia, String musculo, String ladocorporal, String tipoejercicio, Float peso, ArrayList<Double> vector, ArrayList<Double> vectorF, String presento, Double tiempof, Double linea_regresion, Double linea_regresionMF, ArrayList<Double> RMS, ArrayList<Double>  MF){
        this.fechadia = fechadia;
        this.musculo = musculo;
        this.ladocorporal = ladocorporal;
        this.tipoejercicio = tipoejercicio;
        this.vector = vector;
        this.vectorF = vectorF;
        this.peso = peso;
        this.presento = presento;
        this.tiempof = tiempof;
        this.linea_regresion = linea_regresion;
        this.linea_regresionMF = linea_regresionMF;
        this.RMS = RMS; // Corrección aquí
        this.MF = MF; // Corrección aquí
    }

    private String fechadia;
    private String musculo;
    private String ladocorporal;
    private String tipoejercicio;
    private ArrayList<Double> vector;
    private ArrayList<Double> vectorF;
    private Float peso;
    private String presento;
    private Double tiempof;
    private Double linea_regresion;
    private Double linea_regresionMF;
    private ArrayList<Double> MF;
    private ArrayList<Double> RMS;
}
