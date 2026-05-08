/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.transporte_mercancia.Data;


import com.mycompany.transporte_mercancia.Logica.EquipoOficina;
import com.mycompany.transporte_mercancia.Logica.Software;
import com.mycompany.transporte_mercancia.Logica.SoftwareEquipo;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DAOSoftwareEquipo {

    // ---------------------------------------------------
    // LISTAR TODOS
    // ---------------------------------------------------
    public ArrayList<SoftwareEquipo> listarTodos() {
        ArrayList<SoftwareEquipo> lista = new ArrayList<>();

        String sql = """
            SELECT se.id_software_equipo,
                   se.fecha_instalacion,
                   se.estado,
                   s.id_software, s.nombre, s.version, s.fabricante, s.fecha_registro AS sw_fecha,
                   e.id_equipo, e.tipo, e.marca, e.numero_serie, e.estado AS eq_estado,
                   e.fecha_registro AS eq_fecha
            FROM software_equipo se
            JOIN software       s ON se.id_software = s.id_software
            JOIN equipo_oficina e ON se.id_equipo   = e.id_equipo
            ORDER BY s.nombre, e.marca
        """;

        try (Connection conn = Conn.get();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // ---------------------------------------------------
    // LISTAR POR EQUIPO
    // ---------------------------------------------------
    public ArrayList<SoftwareEquipo> listarPorEquipo(int id_equipo) {
        ArrayList<SoftwareEquipo> lista = new ArrayList<>();

        String sql = """
            SELECT se.id_software_equipo,
                   se.fecha_instalacion,
                   se.estado,
                   s.id_software, s.nombre, s.version, s.fabricante, s.fecha_registro AS sw_fecha,
                   e.id_equipo, e.tipo, e.marca, e.numero_serie, e.estado AS eq_estado,
                   e.fecha_registro AS eq_fecha
            FROM software_equipo se
            JOIN software       s ON se.id_software = s.id_software
            JOIN equipo_oficina e ON se.id_equipo   = e.id_equipo
            WHERE se.id_equipo = ?
            ORDER BY s.nombre
        """;

        try (Connection conn = Conn.get();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id_equipo);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // ---------------------------------------------------
    // LISTAR POR SOFTWARE
    // ---------------------------------------------------
    public ArrayList<SoftwareEquipo> listarPorSoftware(int id_software) {
        ArrayList<SoftwareEquipo> lista = new ArrayList<>();

        String sql = """
            SELECT se.id_software_equipo,
                   se.fecha_instalacion,
                   se.estado,
                   s.id_software, s.nombre, s.version, s.fabricante, s.fecha_registro AS sw_fecha,
                   e.id_equipo, e.tipo, e.marca, e.numero_serie, e.estado AS eq_estado,
                   e.fecha_registro AS eq_fecha
            FROM software_equipo se
            JOIN software       s ON se.id_software = s.id_software
            JOIN equipo_oficina e ON se.id_equipo   = e.id_equipo
            WHERE se.id_software = ?
            ORDER BY e.marca
        """;

        try (Connection conn = Conn.get();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id_software);
            ResultSet rs = pstmt.executeQuery();

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
    public SoftwareEquipo buscarPorId(int id) {
        SoftwareEquipo se = null;

        String sql = """
            SELECT se.id_software_equipo,
                   se.fecha_instalacion,
                   se.estado,
                   s.id_software, s.nombre, s.version, s.fabricante, s.fecha_registro AS sw_fecha,
                   e.id_equipo, e.tipo, e.marca, e.numero_serie, e.estado AS eq_estado,
                   e.fecha_registro AS eq_fecha
            FROM software_equipo se
            JOIN software       s ON se.id_software = s.id_software
            JOIN equipo_oficina e ON se.id_equipo   = e.id_equipo
            WHERE se.id_software_equipo = ?
        """;

        try (Connection conn = Conn.get();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                se = mapear(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return se;
    }

    // ---------------------------------------------------
    // CREAR
    // ---------------------------------------------------
    public void create(SoftwareEquipo se) {
        String sql = """
            INSERT INTO software_equipo (id_software, id_equipo, fecha_instalacion, estado)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = Conn.get();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, se.getSoftware().getIdSoftware());
            pstmt.setInt(2, se.getEquipo().getIdEquipo());
            pstmt.setDate(3, se.getFechaInstalacion());
            pstmt.setString(4, se.getEstado());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------
    // ACTUALIZAR (estado y fecha_instalacion)
    // ---------------------------------------------------
    public void update(SoftwareEquipo se) {
        String sql = """
            UPDATE software_equipo
            SET fecha_instalacion = ?, estado = ?
            WHERE id_software_equipo = ?
        """;

        try (Connection conn = Conn.get();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, se.getFechaInstalacion());
            pstmt.setString(2, se.getEstado());
            pstmt.setInt(3, se.getIdSoftwareEquipo());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------
    // ELIMINAR
    // ---------------------------------------------------
    public void delete(int id) {
        String sql = "DELETE FROM software_equipo WHERE id_software_equipo = ?";

        try (Connection conn = Conn.get();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------
    // EXISTE RELACION (para validar UNIQUE al registrar)
    // ---------------------------------------------------
    public boolean existeRelacion(int id_software, int id_equipo) {
        String sql = """
            SELECT COUNT(*) FROM software_equipo
            WHERE id_software = ? AND id_equipo = ?
        """;

        try (Connection conn = Conn.get();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id_software);
            pstmt.setInt(2, id_equipo);
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
    // EXISTE RELACION (para validar UNIQUE al modificar)
    // ---------------------------------------------------
    public boolean existeRelacion(int id_software, int id_equipo, int id_software_equipo) {
        String sql = """
            SELECT COUNT(*) FROM software_equipo
            WHERE id_software = ? AND id_equipo = ?
              AND id_software_equipo != ?
        """;

        try (Connection conn = Conn.get();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id_software);
            pstmt.setInt(2, id_equipo);
            pstmt.setInt(3, id_software_equipo);
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
    // MAPEAR ResultSet → SoftwareEquipo
    // ---------------------------------------------------
    private SoftwareEquipo mapear(ResultSet rs) throws SQLException {
        // Software
        Software s = new Software();
        s.setIdSoftware(rs.getInt("id_software"));
        s.setNombre(rs.getString("nombre"));
        s.setVersion(rs.getString("version"));
        s.setFabricante(rs.getString("fabricante"));
        s.setFechaRegistro(rs.getTimestamp("sw_fecha"));

        // EquipoOficina
        EquipoOficina e = new EquipoOficina();
        e.setIdEquipo(rs.getInt("id_equipo"));
        e.setTipo(rs.getString("tipo"));
        e.setMarca(rs.getString("marca"));
        e.setNumeroSerie(rs.getString("numero_serie"));
        e.setEstado(rs.getString("eq_estado"));
        e.setFechaRegistro(rs.getTimestamp("eq_fecha"));

        // SoftwareEquipo
        SoftwareEquipo se = new SoftwareEquipo();
        se.setIdSoftwareEquipo(rs.getInt("id_software_equipo"));
        se.setSoftware(s);
        se.setEquipo(e);
        se.setFechaInstalacion(rs.getDate("fecha_instalacion"));
        se.setEstado(rs.getString("estado"));

        return se;
    }
}
