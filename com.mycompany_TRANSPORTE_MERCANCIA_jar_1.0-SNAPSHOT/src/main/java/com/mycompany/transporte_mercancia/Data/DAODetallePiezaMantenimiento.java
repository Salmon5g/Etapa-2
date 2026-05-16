/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.transporte_mercancia.Data;

import com.mycompany.transporte_mercancia.Logica.DetallePiezaMantenimiento;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DAODetallePiezaMantenimiento {

    // ---------------------------------------------------
    // LISTAR DETALLES POR MANTENIMIENTO
    // ---------------------------------------------------
    /**
     * Recupera todas las piezas asociadas a un mantenimiento específico.
     * Incluye el nombre de la pieza mediante JOIN para mostrar en JTable.
     *
     * @param idMantenimiento ID del mantenimiento.
     * @return {@link ArrayList} con los detalles encontrados.
     */
    public ArrayList<DetallePiezaMantenimiento> listarPorMantenimiento(int idMantenimiento) {
        ArrayList<DetallePiezaMantenimiento> lista = new ArrayList<>();

        String sql = """
            SELECT d.id_detalle, d.id_mantenimiento, d.id_pieza, p.nombre AS nombre_pieza, d.cantidad
            FROM detalle_pieza_mantenimiento d
            INNER JOIN pieza p ON d.id_pieza = p.id_pieza
            WHERE d.id_mantenimiento = ?
            ORDER BY d.id_detalle ASC
        """;

        try (Connection conn = Conn.get();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idMantenimiento);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                DetallePiezaMantenimiento d = new DetallePiezaMantenimiento();
                d.setIdDetalle(rs.getInt("id_detalle"));
                d.setIdMantenimiento(rs.getInt("id_mantenimiento"));
                d.setIdPieza(rs.getInt("id_pieza"));
                d.setNombrePieza(rs.getString("nombre_pieza"));
                d.setCantidad(rs.getInt("cantidad"));
                lista.add(d);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // ---------------------------------------------------
    // GUARDAR TODOS LOS DETALLES (con transacción)
    // ---------------------------------------------------
    /**
     * Inserta todos los detalles de piezas para un mantenimiento y
     * descuenta el stock de cada pieza. Todo en una sola transacción.
     * <p>
     * Se usa al momento de CREAR un mantenimiento con piezas asociadas.
     *
     * @param idMantenimiento ID del mantenimiento recién creado.
     * @param detalles        lista de piezas a asociar.
     * @return {@code true} si todo se guardó correctamente,
     *         {@code false} si hubo error (se hace rollback).
     */
    public boolean guardarDetalles(int idMantenimiento, ArrayList<DetallePiezaMantenimiento> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            return true; // no hay piezas que guardar, no es error
        }

        String sqlInsert = """
            INSERT INTO detalle_pieza_mantenimiento (id_mantenimiento, id_pieza, cantidad)
            VALUES (?, ?, ?)
        """;

        Connection conn = null;

        try {
            conn = Conn.get();
            conn.setAutoCommit(false);

            DAOPieza daoPieza = new DAOPieza();

            for (DetallePiezaMantenimiento d : detalles) {
                // 1. Insertar detalle
                try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
                    pstmt.setInt(1, idMantenimiento);
                    pstmt.setInt(2, d.getIdPieza());
                    pstmt.setInt(3, d.getCantidad());
                    pstmt.executeUpdate();
                }

                // 2. Descontar stock
                daoPieza.descontarStock(d.getIdPieza(), d.getCantidad(), conn);
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // ---------------------------------------------------
    // ELIMINAR DETALLES Y RESTAURAR STOCK (al eliminar mantenimiento)
    // ---------------------------------------------------
    /**
     * Elimina todos los detalles de un mantenimiento y restaura el stock
     * de cada pieza. Se usa ANTES de eliminar el mantenimiento.
     * <p>
     * Nota: El ON DELETE CASCADE también eliminaría los detalles, pero
     * este método restaura el stock antes de perder los datos.
     *
     * @param idMantenimiento ID del mantenimiento a limpiar.
     * @return {@code true} si se completó correctamente.
     */
    public boolean eliminarDetallesYRestaurarStock(int idMantenimiento) {
        Connection conn = null;

        try {
            conn = Conn.get();
            conn.setAutoCommit(false);

            DAOPieza daoPieza = new DAOPieza();

            // 1. Obtener los detalles actuales para saber qué restaurar
            ArrayList<DetallePiezaMantenimiento> detalles = new ArrayList<>();
            String sqlSelect = """
                SELECT id_pieza, cantidad
                FROM detalle_pieza_mantenimiento
                WHERE id_mantenimiento = ?
            """;

            try (PreparedStatement pstmt = conn.prepareStatement(sqlSelect)) {
                pstmt.setInt(1, idMantenimiento);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    DetallePiezaMantenimiento d = new DetallePiezaMantenimiento();
                    d.setIdPieza(rs.getInt("id_pieza"));
                    d.setCantidad(rs.getInt("cantidad"));
                    detalles.add(d);
                }
            }

            // 2. Restaurar stock de cada pieza
            for (DetallePiezaMantenimiento d : detalles) {
                daoPieza.restaurarStock(d.getIdPieza(), d.getCantidad(), conn);
            }

            // 3. Eliminar los detalles
            String sqlDelete = "DELETE FROM detalle_pieza_mantenimiento WHERE id_mantenimiento = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDelete)) {
                pstmt.setInt(1, idMantenimiento);
                pstmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // ---------------------------------------------------
    // AGREGAR UNA PIEZA A MANTENIMIENTO EXISTENTE
    // ---------------------------------------------------
    /**
     * Agrega una sola pieza a un mantenimiento ya existente y descuenta stock.
     * Útil si se permite editar piezas después de crear el mantenimiento.
     *
     * @param detalle el detalle a agregar.
     * @return {@code true} si se guardó correctamente.
     */
    public boolean agregarDetalle(DetallePiezaMantenimiento detalle) {
        String sqlInsert = """
            INSERT INTO detalle_pieza_mantenimiento (id_mantenimiento, id_pieza, cantidad)
            VALUES (?, ?, ?)
        """;

        Connection conn = null;

        try {
            conn = Conn.get();
            conn.setAutoCommit(false);

            // 1. Insertar detalle
            try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
                pstmt.setInt(1, detalle.getIdMantenimiento());
                pstmt.setInt(2, detalle.getIdPieza());
                pstmt.setInt(3, detalle.getCantidad());
                pstmt.executeUpdate();
            }

            // 2. Descontar stock
            DAOPieza daoPieza = new DAOPieza();
            daoPieza.descontarStock(detalle.getIdPieza(), detalle.getCantidad(), conn);

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // ---------------------------------------------------
    // ELIMINAR UN DETALLE ESPECÍFICO Y RESTAURAR STOCK
    // ---------------------------------------------------
    /**
     * Elimina un detalle específico por su ID y restaura el stock.
     *
     * @param idDetalle  ID del detalle a eliminar.
     * @param idPieza    ID de la pieza (para restaurar stock).
     * @param cantidad   cantidad a restaurar.
     * @return {@code true} si se completó correctamente.
     */
    public boolean eliminarDetalle(int idDetalle, int idPieza, int cantidad) {
        Connection conn = null;

        try {
            conn = Conn.get();
            conn.setAutoCommit(false);

            // 1. Restaurar stock
            DAOPieza daoPieza = new DAOPieza();
            daoPieza.restaurarStock(idPieza, cantidad, conn);

            // 2. Eliminar detalle
            String sql = "DELETE FROM detalle_pieza_mantenimiento WHERE id_detalle = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, idDetalle);
                pstmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
