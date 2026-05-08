/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.transporte_mercancia.Data;

import com.mycompany.transporte_mercancia.Logica.MantenimientoEquipo;
import java.sql.*;
import java.util.ArrayList;

/**
 * DAO para la tabla mantenimiento_equipo. Operaciones: create, update, delete,
 * listarTodos.
 */
public class DAOMantenimientoEquipo {

    // ----------------------------------------------------------
    // CREATE
    // ----------------------------------------------------------
    public boolean create(MantenimientoEquipo m) {
        String sql = "INSERT INTO mantenimiento_equipo "
                + "(id_equipo, tipo_mantenimiento, fecha_entrada, fecha_salida, descripcion) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Conn.get(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, m.getIdEquipo());
            ps.setString(2, m.getTipoMantenimiento());
            ps.setDate(3, m.getFechaEntrada());
            ps.setDate(4, m.getFechaSalida());
            ps.setString(5, m.getDescripcion());

            int filas = ps.executeUpdate();

            if (filas > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        m.setIdMantenimiento(keys.getInt(1));
                    }
                }
                new DAOEquipoOficina().actualizarEstado(m.getIdEquipo(), "En mantención");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("[DAOMantenimientoEquipo.create] Error: " + e.getMessage());
        }
        return false;
    }

    // ----------------------------------------------------------
    // READ — listar todos
    // ----------------------------------------------------------
    public ArrayList<MantenimientoEquipo> listarTodos() {
        String sql = "SELECT id_mantenimiento, id_equipo, tipo_mantenimiento, "
                + "fecha_entrada, fecha_salida, descripcion "
                + "FROM mantenimiento_equipo ORDER BY id_mantenimiento ASC";

        ArrayList<MantenimientoEquipo> lista = new ArrayList<>();

        try (Connection conn = Conn.get(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            System.err.println("[DAOMantenimientoEquipo.listarTodos] Error: " + e.getMessage());
        }
        return lista;
    }

    // ----------------------------------------------------------
    // UPDATE
    // ----------------------------------------------------------
    public boolean update(MantenimientoEquipo m) {
        String sql = "UPDATE mantenimiento_equipo "
                + "SET id_equipo = ?, tipo_mantenimiento = ?, "
                + "fecha_entrada = ?, fecha_salida = ?, descripcion = ? "
                + "WHERE id_mantenimiento = ?";

        try (Connection conn = Conn.get(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, m.getIdEquipo());
            ps.setString(2, m.getTipoMantenimiento());
            ps.setDate(3, m.getFechaEntrada());
            ps.setDate(4, m.getFechaSalida());
            ps.setString(5, m.getDescripcion());
            ps.setInt(6, m.getIdMantenimiento());

            if (ps.executeUpdate() > 0) {
                new DAOEquipoOficina().actualizarEstado(m.getIdEquipo(), "En mantención");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("[DAOMantenimientoEquipo.update] Error: " + e.getMessage());
        }
        return false;
    }

    // ----------------------------------------------------------
    // DELETE
    // ----------------------------------------------------------
    public boolean delete(int idMantenimiento) {
        // Primero obtener el id_equipo antes de eliminar
        int idEquipo = -1;
        String sqlBuscar = "SELECT id_equipo FROM mantenimiento_equipo WHERE id_mantenimiento = ?";

        try (Connection conn = Conn.get(); PreparedStatement ps = conn.prepareStatement(sqlBuscar)) {
            ps.setInt(1, idMantenimiento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idEquipo = rs.getInt("id_equipo");
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAOMantenimientoEquipo.delete] Error al buscar equipo: " + e.getMessage());
        }

        // Eliminar el mantenimiento
        String sql = "DELETE FROM mantenimiento_equipo WHERE id_mantenimiento = ?";

        try (Connection conn = Conn.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMantenimiento);

            if (ps.executeUpdate() > 0) {
                if (idEquipo != -1) {
                    new DAOEquipoOficina().actualizarEstado(idEquipo, "Operativo");
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("[DAOMantenimientoEquipo.delete] Error: " + e.getMessage());
        }
        return false;
    }

    // ----------------------------------------------------------
    // PRIVADO — mapear ResultSet → MantenimientoEquipo
    // ----------------------------------------------------------
    private MantenimientoEquipo mapear(ResultSet rs) throws SQLException {
        MantenimientoEquipo m = new MantenimientoEquipo();
        m.setIdMantenimiento(rs.getInt("id_mantenimiento"));
        m.setIdEquipo(rs.getInt("id_equipo"));
        m.setTipoMantenimiento(rs.getString("tipo_mantenimiento"));
        m.setFechaEntrada(rs.getDate("fecha_entrada"));
        m.setFechaSalida(rs.getDate("fecha_salida"));
        m.setDescripcion(rs.getString("descripcion"));
        return m;
    }
}