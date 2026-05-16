/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.transporte_mercancia.Logica;

import java.sql.Date;

public class MantenimientoEquipo {

    private int idMantenimiento;
    private int idEquipo;
    private String tipoMantenimiento;   // "Preventivo" o "Correctivo"
    private String estado;              // "En progreso", "Postergado", "Terminado"
    private Date fechaEntrada;        // fecha de inicio
    private Date fechaFin;            // fecha de cierre (se llena al terminar)
    private int diasActivos;         // días acumulados en estado "En progreso"
    private Date fechaUltimoInicio;   // última vez que arrancó "En progreso"
    private String descripcion;

    // --- Getters & Setters ---
    public int getIdMantenimiento() {
        return idMantenimiento;
    }

    public void setIdMantenimiento(int idMantenimiento) {
        this.idMantenimiento = idMantenimiento;
    }

    public int getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
    }

    public String getTipoMantenimiento() {
        return tipoMantenimiento;
    }

    public void setTipoMantenimiento(String tipoMantenimiento) {
        this.tipoMantenimiento = tipoMantenimiento;
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
