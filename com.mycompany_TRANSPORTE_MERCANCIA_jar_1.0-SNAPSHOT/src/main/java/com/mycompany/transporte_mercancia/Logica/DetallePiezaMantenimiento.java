/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.transporte_mercancia.Logica;

public class DetallePiezaMantenimiento {

    private int idDetalle;
    private int idMantenimiento;
    private int idPieza;
    private String nombrePieza;   // auxiliar para mostrar en JTable (no se persiste)
    private int cantidad;

    public DetallePiezaMantenimiento() {
    }

    public DetallePiezaMantenimiento(int idDetalle, int idMantenimiento, int idPieza,
                                      String nombrePieza, int cantidad) {
        this.idDetalle = idDetalle;
        this.idMantenimiento = idMantenimiento;
        this.idPieza = idPieza;
        this.nombrePieza = nombrePieza;
        this.cantidad = cantidad;
    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public int getIdMantenimiento() {
        return idMantenimiento;
    }

    public void setIdMantenimiento(int idMantenimiento) {
        this.idMantenimiento = idMantenimiento;
    }

    public int getIdPieza() {
        return idPieza;
    }

    public void setIdPieza(int idPieza) {
        this.idPieza = idPieza;
    }

    public String getNombrePieza() {
        return nombrePieza;
    }

    public void setNombrePieza(String nombrePieza) {
        this.nombrePieza = nombrePieza;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
