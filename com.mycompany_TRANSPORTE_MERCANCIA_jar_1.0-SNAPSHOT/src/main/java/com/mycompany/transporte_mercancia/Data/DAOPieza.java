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
    // HELPER: mapear ResultSet → Pieza
    // ---------------------------------------------------
    private Pieza mapear(ResultSet rs) throws SQLException {
        Pieza p = new Pieza();
        p.setIdPieza(rs.getInt("id_pieza"));
        p.setCodigo(rs.getString("codigo"));
        p.setNombre(rs.getString("nombre"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setStock(rs.getInt("stock"));
        p.setFechaRegistro(rs.getTimestamp("fecha_registro"));
        return p;
    }

    // ---------------------------------------------------
    // LISTAR TODAS LAS PIEZAS
    // ---------------------------------------------------
    /**
     * Recupera todas las piezas registradas, ordenadas por código ASC.
     *
     * @return lista de {@link Pieza}; vacía si no hay registros o error.
     */
    public ArrayList<Pieza> listarTodos() {
        ArrayList<Pieza> lista = new ArrayList<>();

        String sql = """
              SELECT id_pieza, codigo, nombre, descripcion, stock, fecha_registro
              FROM pieza
              ORDER BY codigo ASC
          """;

        try (Connection conn = Conn.get(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
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
     * Recupera solo las piezas con stock > 0. Útil para el ComboBox al agregar
     * piezas a un mantenimiento.
     *
     * @return lista de {@link Pieza} disponibles.
     */
    public ArrayList<Pieza> listarConStock() {
        ArrayList<Pieza> lista = new ArrayList<>();

        String sql = """
              SELECT id_pieza, codigo, nombre, descripcion, stock, fecha_registro
              FROM pieza
              WHERE stock > 0
              ORDER BY codigo ASC
          """;

        try (Connection conn = Conn.get(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // ---------------------------------------------------
    // BUSCAR POR ID
    // ---------------------------------------------------
    /**
     * Busca una pieza por su ID.
     *
     * @param id identificador de la pieza.
     * @return {@link Pieza} encontrada, o {@code null}.
     */
    public Pieza buscarPorId(int id) {
        String sql = """
              SELECT id_pieza, codigo, nombre, descripcion, stock, fecha_registro
              FROM pieza
              WHERE id_pieza = ?
          """;

        try (Connection conn = Conn.get(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapear(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // ---------------------------------------------------
    // BUSCAR POR CÓDIGO
    // ---------------------------------------------------
    /**
     * Busca una pieza por su código exacto. Útil para validar duplicados al
     * registrar.
     *
     * @param codigo código de la pieza.
     * @return {@link Pieza} encontrada, o {@code null}.
     */
    public Pieza buscarPorCodigo(String codigo) {
        String sql = """
              SELECT id_pieza, codigo, nombre, descripcion, stock, fecha_registro
              FROM pieza
              WHERE codigo = ?
          """;

        try (Connection conn = Conn.get(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, codigo);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapear(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // ---------------------------------------------------
    // BUSCAR POR NOMBRE
    // ---------------------------------------------------
    /**
     * Busca una pieza por su nombre exacto. Útil para el flujo alterno RF-09
     * (detección de duplicados).
     *
     * @param nombre nombre de la pieza.
     * @return {@link Pieza} encontrada, o {@code null}.
     */
    public Pieza buscarPorNombre(String nombre) {
        String sql = """
              SELECT id_pieza, codigo, nombre, descripcion, stock, fecha_registro
              FROM pieza
              WHERE nombre = ?
          """;

        try (Connection conn = Conn.get(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapear(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // ---------------------------------------------------
    // CREAR PIEZA
    // ---------------------------------------------------
    /**
     * Inserta una nueva pieza. La fecha de registro la genera la BD.
     *
     * @param p pieza a insertar.
     * @return {@code true} si se insertó; {@code false} si hubo error (ej.
     * código o nombre duplicado).
     */
    public boolean create(Pieza p) {
        String sql = """
              INSERT INTO pieza (codigo, nombre, descripcion, stock)
              VALUES (?, ?, ?, ?)
          """;

        try (Connection conn = Conn.get(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, p.getCodigo());
            pstmt.setString(2, p.getNombre());
            pstmt.setString(3, p.getDescripcion());
            pstmt.setInt(4, p.getStock());
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
     * Actualiza los datos de una pieza existente.
     *
     * @param p pieza con los datos actualizados (debe tener idPieza).
     */
    public void update(Pieza p) {
        String sql = """
              UPDATE pieza
              SET codigo = ?, nombre = ?, descripcion = ?, stock = ?
              WHERE id_pieza = ?
          """;

        try (Connection conn = Conn.get(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, p.getCodigo());
            pstmt.setString(2, p.getNombre());
            pstmt.setString(3, p.getDescripcion());
            pstmt.setInt(4, p.getStock());
            pstmt.setInt(5, p.getIdPieza());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------
    // DESCONTAR STOCK (dentro de transacción)
    // ---------------------------------------------------
    /**
     * Descuenta cantidad del stock. Lanza excepción si el stock es
     * insuficiente.
     *
     * @param idPieza ID de la pieza.
     * @param cantidad cantidad a descontar.
     * @param conn conexión de la transacción activa.
     * @throws SQLException si stock insuficiente o error de BD.
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
    // RESTAURAR STOCK (dentro de transacción)
    // ---------------------------------------------------
    /**
     * Devuelve cantidad al stock de una pieza.
     *
     * @param idPieza ID de la pieza.
     * @param cantidad cantidad a restaurar.
     * @param conn conexión de la transacción activa.
     * @throws SQLException si ocurre un error de BD.
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
     * Elimina una pieza. Fallará si está referenciada en algún mantenimiento
     * (FK RESTRICT).
     *
     * @param id ID de la pieza a eliminar.
     * @return {@code true} si se eliminó; {@code false} si está en uso.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM pieza WHERE id_pieza = ?";

        try (Connection conn = Conn.get(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
