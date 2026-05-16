/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.transporte_mercancia.Logica;

import java.sql.Timestamp;

public class Pieza {

    private int idPieza;
    private String nombre;
    private String descripcion;
    private int stock;
    private Timestamp fechaRegistro;

    public Pieza() {
    }

    public Pieza(int idPieza, String nombre, String descripcion, int stock, Timestamp fechaRegistro) {
        this.idPieza = idPieza;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.stock = stock;
        this.fechaRegistro = fechaRegistro;
    }

    public int getIdPieza() {
        return idPieza;
    }

    public void setIdPieza(int idPieza) {
        this.idPieza = idPieza;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}

