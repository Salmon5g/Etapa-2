/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.transporte_mercancia.Logica;

import com.mycompany.transporte_mercancia.Logica.Personal;

/**
 *
 * @author saave
 */
public class Camion {

    private Integer id_Camion;
    private String marca;
    private String modelo;
    private Integer anio;
    private Integer kilometraje_total;
    private Personal conductor;
    private String matricula;

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public Personal getConductor() {
        return conductor;
    }

    public void setConductor(Personal conductor) {
        this.conductor = conductor;
    }

    public Integer getIdCamion() {
        return id_Camion;
    }

    public void setIdCamion(Integer idCamion) {
        this.id_Camion = idCamion;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Integer getKilometrajeTotal() {
        return kilometraje_total;
    }

    public void setKilometrajeTotal(Integer kilometraje_total) {
        this.kilometraje_total = kilometraje_total;
    }

    public String toString() {
        return matricula;
    }
}
