/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.transporte_mercancia.Logica;

import java.sql.Timestamp;

/**
 *
 * @author saave
 */
public class Software {
    private int       id_software;
    private String    nombre;
    private String    version;
    private String    fabricante;
    private Timestamp fecha_registro;

    
    public int getIdSoftware() {
        return id_software;
    }

    public void setIdSoftware(int id_software) {
        this.id_software = id_software;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFabricante() {
        return fabricante;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    public Timestamp getFechaRegistro() {
        return fecha_registro;
    }

    public void setFechaRegistro(Timestamp fecha_registro) {
        this.fecha_registro = fecha_registro;
    }

}
