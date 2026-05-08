/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.transporte_mercancia.Data;

import com.mycompany.transporte_mercancia.Logica.Software;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class DAOSoftware {

    // ---------------------------------------------------
    // LISTAR TODOS LOS SOFTWARE
    // ---------------------------------------------------
    /**
     * Recupera todos los software registrados en la base de datos,
     * ordenados alfabéticamente por nombre.
     *
     * @return {@link ArrayList} con todos los objetos {@link Software}
     *         encontrados; lista vacía si no hay registros o si ocurre
     *         un error de base de datos.
     */
    public ArrayList<Software> listarTodos() {
        ArrayList<Software> lista = new ArrayList<>();

        String sql = """
            SELECT id_software, nombre, version, fabricante, fecha_registro
            FROM software
            ORDER BY nombre ASC
        """;

        try (Connection conn = Conn.get();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Software s = new Software();
                s.setIdSoftware(rs.getInt("id_software"));
                s.setNombre(rs.getString("nombre"));
                s.setVersion(rs.getString("version"));
                s.setFabricante(rs.getString("fabricante"));
                s.setFechaRegistro(rs.getTimestamp("fecha_registro"));
                lista.add(s);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // ---------------------------------------------------
    // BUSCAR SOFTWARE POR ID
    // ---------------------------------------------------
    /**
     * Busca un software en la base de datos por su identificador único.
     *
     * @param id identificador del software a buscar.
     * @return el objeto {@link Software} encontrado, o {@code null} si no
     *         existe ningún software con ese ID o si ocurre un error de
     *         base de datos.
     */
    public Software buscarPorId(int id) {
        Software s = null;

        String sql = """
            SELECT id_software, nombre, version, fabricante, fecha_registro
            FROM software
            WHERE id_software = ?
        """;

        try (Connection conn = Conn.get();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                s = new Software();
                s.setIdSoftware(rs.getInt("id_software"));
                s.setNombre(rs.getString("nombre"));
                s.setVersion(rs.getString("version"));
                s.setFabricante(rs.getString("fabricante"));
                s.setFechaRegistro(rs.getTimestamp("fecha_registro"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return s;
    }

    // ---------------------------------------------------
    // CREAR SOFTWARE
    // ---------------------------------------------------
    /**
     * Inserta un nuevo software en la base de datos.
     * La fecha de registro es generada automáticamente por la base de datos.
     *
     * @param s objeto {@link Software} con los datos a insertar;
     *          no debe ser {@code null}.
     */
    public void create(Software s) {
        String sql = """
            INSERT INTO software (nombre, version, fabricante)
            VALUES (?, ?, ?)
        """;

        try (Connection conn = Conn.get();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, s.getNombre());
            pstmt.setString(2, s.getVersion());
            pstmt.setString(3, s.getFabricante());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------
    // ACTUALIZAR SOFTWARE
    // ---------------------------------------------------
    /**
     * Actualiza los datos de un software existente en la base de datos.
     * La fila a actualizar se identifica por el {@code id_software}
     * del objeto recibido.
     *
     * @param s objeto {@link Software} con los datos actualizados; debe
     *          tener un {@code idSoftware} válido correspondiente a un
     *          registro existente.
     */
    public void update(Software s) {
        String sql = """
            UPDATE software
            SET nombre = ?, version = ?, fabricante = ?
            WHERE id_software = ?
        """;

        try (Connection conn = Conn.get();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, s.getNombre());
            pstmt.setString(2, s.getVersion());
            pstmt.setString(3, s.getFabricante());
            pstmt.setInt(4, s.getIdSoftware());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------
    // ELIMINAR SOFTWARE
    // ---------------------------------------------------
    /**
     * Elimina un software de la base de datos según su identificador.
     *
     * @param id identificador del software a eliminar.
     */
    public void delete(int id) {
        String sql = "DELETE FROM software WHERE id_software = ?";

        try (Connection conn = Conn.get();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------
    // VERIFICAR DUPLICADO AL REGISTRAR
    // ---------------------------------------------------
    /**
     * Verifica si ya existe un software con el mismo nombre y versión
     * en la base de datos. Usado al momento de registrar uno nuevo.
     *
     * @param nombre  nombre del software a verificar.
     * @param version versión del software a verificar.
     * @return {@code true} si ya existe, {@code false} en caso contrario.
     */
    public boolean existeSoftware(String nombre, String version) {
        String sql = """
            SELECT COUNT(*) FROM software
            WHERE nombre = ? AND version = ?
        """;

        try (Connection conn = Conn.get();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            pstmt.setString(2, version);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // ---------------------------------------------------
    // VERIFICAR DUPLICADO AL MODIFICAR
    // ---------------------------------------------------
    /**
     * Verifica si ya existe otro software con el mismo nombre y versión,
     * excluyendo el registro que se está editando. Usado al modificar.
     *
     * @param nombre      nombre del software a verificar.
     * @param version     versión del software a verificar.
     * @param idSoftware  ID del software que se está editando (se excluye).
     * @return {@code true} si ya existe otro registro igual,
     *         {@code false} en caso contrario.
     */
    public boolean existeSoftware(String nombre, String version, int idSoftware) {
        String sql = """
            SELECT COUNT(*) FROM software
            WHERE nombre = ? AND version = ? AND id_software != ?
        """;

        try (Connection conn = Conn.get();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            pstmt.setString(2, version);
            pstmt.setInt(3, idSoftware);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}

