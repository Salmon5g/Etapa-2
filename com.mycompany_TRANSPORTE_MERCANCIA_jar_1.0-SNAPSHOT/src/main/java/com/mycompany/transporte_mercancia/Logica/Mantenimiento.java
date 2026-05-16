/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.transporte_mercancia.Logica;

import java.sql.Date;

public class Mantenimiento {

    private Integer idMantenimiento;
    private String matricula;
    private String estado;
    private Date fechaEntrada;
    private Date fechaFin;
    private int diasActivos;
    private Date fechaUltimoInicio;
    private String descripcion;

    public Integer getIdMantenimiento() {
        return idMantenimiento;
    }

    public void setIdMantenimiento(Integer id) {
        this.idMantenimiento = id;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaEntrada() {
        return fechaEntrada;
    }

    public void setFechaEntrada(Date fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public int getDiasActivos() {
        return diasActivos;
    }

    public void setDiasActivos(int diasActivos) {
        this.diasActivos = diasActivos;
    }

    public Date getFechaUltimoInicio() {
        return fechaUltimoInicio;
    }

    public void setFechaUltimoInicio(Date fechaUltimoInicio) {
        this.fechaUltimoInicio = fechaUltimoInicio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
