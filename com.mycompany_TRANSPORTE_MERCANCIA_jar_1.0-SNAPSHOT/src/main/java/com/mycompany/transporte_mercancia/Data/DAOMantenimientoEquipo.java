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
    
    // ============================================================
// AGREGAR ESTE MÉTODO dentro de la clase DAOMantenimientoEquipo
// (antes del método privado mapear)
// ============================================================

    // ----------------------------------------------------------
    // READ — historial filtrado (RF-08)
    // ----------------------------------------------------------
    /**
     * Retorna el historial de mantenimientos aplicando filtros opcionales.
     * Hace JOIN con equipo_oficina para obtener numero_serie, marca y tipo de equipo.
     *
     * @param numeroSerie       filtrar por número de serie (parcial, ignora mayúsculas/minúsculas).
     *                          Pasar null o vacío para ignorar este filtro.
     * @param tipoMantenimiento "Preventivo", "Correctivo" o "Todos" para ignorar.
     * @param fechaDesde        fecha de entrada mínima; null para ignorar.
     * @param fechaHasta        fecha de entrada máxima; null para ignorar.
     * @return lista de Object[] con 8 elementos por fila:
     *         [0] id_mantenimiento (Integer)
     *         [1] numero_serie     (String)
     *         [2] marca            (String)
     *         [3] tipo_equipo      (String)
     *         [4] tipo_mantenimiento (String)
     *         [5] fecha_entrada    (java.sql.Date)
     *         [6] fecha_salida     (java.sql.Date)
     *         [7] descripcion      (String)
     */
    public ArrayList<Object[]> listarHistorialFiltrado(
            String numeroSerie,
            String tipoMantenimiento,
            java.sql.Date fechaDesde,
            java.sql.Date fechaHasta) {

        ArrayList<Object[]> lista = new ArrayList<>();

        // --- Construcción dinámica del WHERE ---
        StringBuilder sql = new StringBuilder(
            "SELECT me.id_mantenimiento, e.numero_serie, e.marca, e.tipo AS tipo_equipo, "
          + "       me.tipo_mantenimiento, me.fecha_entrada, me.fecha_salida, me.descripcion "
          + "FROM mantenimiento_equipo me "
          + "JOIN equipo_oficina e ON me.id_equipo = e.id_equipo "
          + "WHERE 1=1 "
        );

        ArrayList<Object> params = new ArrayList<>();

        // Filtro por número de serie (búsqueda parcial)
        if (numeroSerie != null && !numeroSerie.isBlank()) {
            sql.append("AND e.numero_serie LIKE ? ");
            params.add("%" + numeroSerie.toUpperCase().trim() + "%");
        }

        // Filtro por tipo de mantenimiento
        if (tipoMantenimiento != null
                && !tipoMantenimiento.isBlank()
                && !tipoMantenimiento.equalsIgnoreCase("Todos")) {
            sql.append("AND me.tipo_mantenimiento = ? ");
            params.add(tipoMantenimiento);
        }

        // Filtro por fecha desde
        if (fechaDesde != null) {
            sql.append("AND me.fecha_entrada >= ? ");
            params.add(fechaDesde);
        }

        // Filtro por fecha hasta
        if (fechaHasta != null) {
            sql.append("AND me.fecha_entrada <= ? ");
            params.add(fechaHasta);
        }

        sql.append("ORDER BY me.fecha_entrada DESC, me.id_mantenimiento DESC");

        try (Connection conn = Conn.get();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            // Asignar parámetros dinámicos
            for (int i = 0; i < params.size(); i++) {
                Object val = params.get(i);
                if (val instanceof String)         ps.setString(i + 1, (String) val);
                else if (val instanceof java.sql.Date) ps.setDate(i + 1, (java.sql.Date) val);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = {
                        rs.getInt("id_mantenimiento"),
                        rs.getString("numero_serie"),
                        rs.getString("marca"),
                        rs.getString("tipo_equipo"),
                        rs.getString("tipo_mantenimiento"),
                        rs.getDate("fecha_entrada"),
                        rs.getDate("fecha_salida"),
                        rs.getString("descripcion")
                    };
                    lista.add(fila);
                }
            }

        } catch (SQLException e) {
            System.err.println("[DAOMantenimientoEquipo.listarHistorialFiltrado] Error: " + e.getMessage());
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