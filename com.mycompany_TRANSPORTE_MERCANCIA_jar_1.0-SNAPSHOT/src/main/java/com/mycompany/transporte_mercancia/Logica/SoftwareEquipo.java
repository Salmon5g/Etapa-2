/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.transporte_mercancia.Logica;

import java.sql.Date;

public class SoftwareEquipo {

    private int           id_software_equipo;
    private Software      software;
    private EquipoOficina equipo;
    private Date          fecha_instalacion;
    private String        estado; // 'Activo' | 'Desinstalado'

    // -------------------------------------------------------
    // GETTERS
    // -------------------------------------------------------

    public int getIdSoftwareEquipo() {
        return id_software_equipo;
    }

    public Software getSoftware() {
        return software;
    }

    public EquipoOficina getEquipo() {
        return equipo;
    }

    public Date getFechaInstalacion() {
        return fecha_instalacion;
    }

    public String getEstado() {
        return estado;
    }

    // -------------------------------------------------------
    // SETTERS
    // -------------------------------------------------------

    public void setIdSoftwareEquipo(int id_software_equipo) {
        this.id_software_equipo = id_software_equipo;
    }

    public void setSoftware(Software software) {
        this.software = software;
    }

    public void setEquipo(EquipoOficina equipo) {
        this.equipo = equipo;
    }

    public void setFechaInstalacion(Date fecha_instalacion) {
        this.fecha_instalacion = fecha_instalacion;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
