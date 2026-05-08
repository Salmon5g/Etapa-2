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
public class EquipoOficina {

    private int id_equipo;
    private String tipo;
    private String marca;
    private String numero_serie;
    private String estado;
    private Timestamp fecha_registro;

    public int getIdEquipo() {
        return id_equipo;
    }

    public void setIdEquipo(int id_equipo) {
        this.id_equipo = id_equipo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getNumeroSerie() {
        return numero_serie;
    }

    public void setNumeroSerie(String numero_serie) {
        this.numero_serie = numero_serie;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Timestamp getFechaRegistro() {
        return fecha_registro;
    }

    public void setFechaRegistro(Timestamp fecha_registro) {
        this.fecha_registro = fecha_registro;
    }

}
