/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.transporte_mercancia.Data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase encargada de gestionar la conexión a la base de datos MySQL del sistema
 * de reservas de habitaciones de hotel.
 * <p>
 * Proporciona un método estático para obtener una instancia de {@link Connection}
 * utilizando los parámetros de conexión configurados internamente.
 * </p>
 * 
 * @author INFOPUNTO
 */
public class Conn {

    /** URL de conexión a la base de datos MySQL. */
    private static final String URL = "jdbc:mysql://localhost:3306/Transporte_mercancia";
    
    /** Usuario de acceso a la base de datos. */
    private static final String USUARIO = "root";
    
    /** Contraseña del usuario de la base de datos. */
    private static final String CONTRASENA = "Metraka20..";

    /**
     * Obtiene una conexión a la base de datos utilizando los parámetros definidos
     * en la clase.
     *
     * @return una instancia de {@link Connection} ya conectada a la base de datos
     * @throws SQLException si ocurre un error al intentar establecer la conexión
     */
    public static Connection get() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }

}
