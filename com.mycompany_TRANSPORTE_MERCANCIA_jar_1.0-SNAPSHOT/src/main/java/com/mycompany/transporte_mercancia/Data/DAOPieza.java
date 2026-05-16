/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.transporte_mercancia.Data;

import com.mycompany.transporte_mercancia.Logica.Pieza;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DAOPieza {

    // ---------------------------------------------------
    // LISTAR TODAS LAS PIEZAS
    // ---------------------------------------------------
    /**
     * Recupera todas las piezas registradas en la base de datos,
     * ordenadas por ID ascendente.
     *
     * @return {@link ArrayList} con todos los objetos {@link Pieza}
     *         encontrados; lista vacía si no hay registros o si ocurre
     *         un error de base de datos.
     */
    public ArrayList<Pieza> listarTodos() {
        ArrayList<Pieza> lista = new ArrayList<>();

        String sql = """
            SELECT id_pieza, nombre, descripcion, stock, fecha_registro
            FROM pieza
            ORDER BY id_pieza ASC
        """;

        try (Connection conn = Conn.get();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Pieza p = new Pieza();
                p.setIdPieza(rs.getInt("id_pieza"));
                p.setNombre(rs.getString("nombre"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setStock(rs.getInt("stock"));
                p.setFechaRegistro(rs.getTimestamp("fecha_registro"));
                lista.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // ---------------------------------------------------
    // LISTAR PIEZAS CON STOCK DISPONIBLE
    // ---------------------------------------------------
    /**
     * Recupera solo las piezas que tienen stock mayor a 0.
     * Útil para llenar el ComboBox al agregar piezas a un mantenimiento.
     *
     * @return {@link ArrayList} con las piezas disponibles.
     */
    public ArrayList<Pieza> listarConStock() {
        ArrayList<Pieza> lista = new ArrayList<>();

        String sql = """
            SELECT id_pieza, nombre, descripcion, stock, fecha_registro
            FROM pieza
            WHERE stock > 0
            ORDER BY nombre ASC
        """;

        try (Connection conn = Conn.get();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Pieza p = new Pieza();
                p.setIdPieza(rs.getInt("id_pieza"));
                p.setNombre(rs.getString("nombre"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setStock(rs.getInt("stock"));
                p.setFechaRegistro(rs.getTimestamp("fecha_registro"));
                lista.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // ---------------------------------------------------
    // BUSCAR PIEZA POR ID
    // ---------------------------------------------------
    /**
     * Busca una pieza en la base de datos por su identificador único.
     *
     * @param id identificador de la pieza a buscar.
     * @return el objeto {@link Pieza} encontrado, o {@code null} si no
     *         existe ninguna pieza con ese ID o si ocurre un error.
     */
    public Pieza buscarPorId(int id) {
        Pieza p = null;

        String sql = """
            SELECT id_pieza, nombre, descripcion, stock, fecha_registro
            FROM pieza
            WHERE id_pieza = ?
        """;

        try (Connection conn = Conn.get();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                p = new Pieza();
                p.setIdPieza(rs.getInt("id_pieza"));
                p.setNombre(rs.getString("nombre"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setStock(rs.getInt("stock"));
                p.setFechaRegistro(rs.getTimestamp("fecha_registro"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return p;
    }

    // ---------------------------------------------------
    // BUSCAR PIEZA POR NOMBRE (para flujo alterno: duplicados)
    // ---------------------------------------------------
    /**
     * Busca una pieza por su nombre exacto.
     * Útil para validar el flujo alterno del RF-09:
     * "Si se intenta agregar una pieza que ya existe, se muestra advertencia."
     *
     * @param nombre nombre de la pieza a buscar.
     * @return el objeto {@link Pieza} encontrado, o {@code null} si no existe.
     */
    public Pieza buscarPorNombre(String nombre) {
        Pieza p = null;

        String sql = """
            SELECT id_pieza, nombre, descripcion, stock, fecha_registro
            FROM pieza
            WHERE nombre = ?
        """;

        try (Connection conn = Conn.get();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                p = new Pieza();
                p.setIdPieza(rs.getInt("id_pieza"));
                p.setNombre(rs.getString("nombre"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setStock(rs.getInt("stock"));
                p.setFechaRegistro(rs.getTimestamp("fecha_registro"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return p;
    }

    // ---------------------------------------------------
    // CREAR PIEZA
    // ---------------------------------------------------
    /**
     * Inserta una nueva pieza en la base de datos.
     * La fecha de registro se genera automáticamente por la BD.
     *
     * @param p objeto {@link Pieza} con los datos a insertar.
     * @return {@code true} si se insertó correctamente, {@code false}
     *         si ocurrió un error (ej. nombre duplicado).
     */
    public boolean create(Pieza p) {
        String sql = """
            INSERT INTO pieza (nombre, descripcion, stock)
            VALUES (?, ?, ?)
        """;

        try (Connection conn = Conn.get();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, p.getNombre());
            pstmt.setString(2, p.getDescripcion());
            pstmt.setInt(3, p.getStock());
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ---------------------------------------------------
    // ACTUALIZAR PIEZA
    // ---------------------------------------------------
    /**
     * Actualiza los datos de una pieza existente en la base de datos.
     *
     * @param p objeto {@link Pieza} con los datos actualizados.
     */
    public void update(Pieza p) {
        String sql = """
            UPDATE pieza
            SET nombre = ?, descripcion = ?, stock = ?
            WHERE id_pieza = ?
        """;

        try (Connection conn = Conn.get();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, p.getNombre());
            pstmt.setString(2, p.getDescripcion());
            pstmt.setInt(3, p.getStock());
            pstmt.setInt(4, p.getIdPieza());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------
    // ACTUALIZAR STOCK (descontar piezas usadas)
    // ---------------------------------------------------
    /**
     * Descuenta una cantidad del stock de una pieza.
     * Se usa cuando se registran piezas en un mantenimiento.
     *
     * @param idPieza   ID de la pieza.
     * @param cantidad  cantidad a descontar del stock.
     * @param conn      conexión existente (para usar dentro de transacción).
     * @throws SQLException si ocurre un error de base de datos.
     */
    public void descontarStock(int idPieza, int cantidad, Connection conn) throws SQLException {
        String sql = "UPDATE pieza SET stock = stock - ? WHERE id_pieza = ? AND stock >= ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cantidad);
            pstmt.setInt(2, idPieza);
            pstmt.setInt(3, cantidad);

            int filas = pstmt.executeUpdate();
            if (filas == 0) {
                throw new SQLException("Stock insuficiente para la pieza ID: " + idPieza);
            }
        }
    }

    // ---------------------------------------------------
    // RESTAURAR STOCK (al eliminar un mantenimiento)
    // ---------------------------------------------------
    /**
     * Restaura una cantidad al stock de una pieza.
     * Se usa cuando se elimina un mantenimiento y se devuelven las piezas.
     *
     * @param idPieza   ID de la pieza.
     * @param cantidad  cantidad a restaurar al stock.
     * @param conn      conexión existente (para usar dentro de transacción).
     * @throws SQLException si ocurre un error de base de datos.
     */
    public void restaurarStock(int idPieza, int cantidad, Connection conn) throws SQLException {
        String sql = "UPDATE pieza SET stock = stock + ? WHERE id_pieza = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cantidad);
            pstmt.setInt(2, idPieza);
            pstmt.executeUpdate();
        }
    }

    // ---------------------------------------------------
    // ELIMINAR PIEZA
    // ---------------------------------------------------
    /**
     * Elimina una pieza de la base de datos según su identificador.
     * Fallará si la pieza está siendo usada en algún detalle de mantenimiento
     * (por el RESTRICT en la FK).
     *
     * @param id identificador de la pieza a eliminar.
     * @return {@code true} si se eliminó, {@code false} si no se pudo
     *         (ej. pieza en uso).
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM pieza WHERE id_pieza = ?";

        try (Connection conn = Conn.get();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
