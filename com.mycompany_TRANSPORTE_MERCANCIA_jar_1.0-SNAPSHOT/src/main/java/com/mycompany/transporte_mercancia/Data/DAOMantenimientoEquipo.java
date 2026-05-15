/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.transporte_mercancia.Data;

import com.mycompany.transporte_mercancia.Logica.MantenimientoEquipo;
import java.sql.*;
import java.util.ArrayList;

public class DAOMantenimientoEquipo {

    // ----------------------------------------------------------
    // CREATE — siempre inicia en "En progreso"
    // ----------------------------------------------------------
    public boolean create(MantenimientoEquipo m) {
        String sql = "INSERT INTO mantenimiento_equipo "
                + "(id_equipo, tipo_mantenimiento, estado, fecha_entrada, "
                + " fecha_fin, dias_activos, fecha_ultimo_inicio, descripcion) "
                + "VALUES (?, ?, 'En progreso', ?, NULL, 0, ?, ?)";

        try (Connection conn = Conn.get();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt   (1, m.getIdEquipo());
            ps.setString(2, m.getTipoMantenimiento());
            ps.setDate  (3, m.getFechaEntrada());
            ps.setDate  (4, m.getFechaEntrada()); // fecha_ultimo_inicio = fecha_entrada al crear
            ps.setString(5, m.getDescripcion());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) m.setIdMantenimiento(keys.getInt(1));
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
    // READ — buscar por ID (auxiliar para el update)
    // ----------------------------------------------------------
    public MantenimientoEquipo buscarPorId(int idMantenimiento) {
        String sql = "SELECT * FROM mantenimiento_equipo WHERE id_mantenimiento = ?";
        try (Connection conn = Conn.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMantenimiento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("[DAOMantenimientoEquipo.buscarPorId] Error: " + e.getMessage());
        }
        return null;
    }

    // ----------------------------------------------------------
    // READ — listar todos
    // ----------------------------------------------------------
    public ArrayList<MantenimientoEquipo> listarTodos() {
        String sql = "SELECT * FROM mantenimiento_equipo ORDER BY id_mantenimiento ASC";
        ArrayList<MantenimientoEquipo> lista = new ArrayList<>();

        try (Connection conn = Conn.get();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("[DAOMantenimientoEquipo.listarTodos] Error: " + e.getMessage());
        }
        return lista;
    }

    // ----------------------------------------------------------
    // UPDATE — maneja la lógica de cambio de estado y días
    // ----------------------------------------------------------
    public boolean update(MantenimientoEquipo m) {

        // Obtener el registro actual para comparar estados
        MantenimientoEquipo actual = buscarPorId(m.getIdMantenimiento());
        if (actual == null) return false;

        String estadoActual = actual.getEstado();
        String estadoNuevo  = m.getEstado();

        // Bloquear modificaciones si ya estaba Terminado
        if ("Terminado".equals(estadoActual)) {
            System.err.println("[DAOMantenimientoEquipo.update] No se puede modificar un mantenimiento Terminado.");
            return false;
        }

        java.sql.Date hoy = new java.sql.Date(System.currentTimeMillis());

        // ── Lógica de acumulación de días ────────────────────────
        if ("En progreso".equals(estadoActual) && !"En progreso".equals(estadoNuevo)) {
            // Saliendo de "En progreso" → calcular días del período actual
            if (actual.getFechaUltimoInicio() != null) {
                long diff = hoy.getTime() - actual.getFechaUltimoInicio().getTime();
                int diasPeriodo = (int) (diff / (1000L * 60 * 60 * 24));
                m.setDiasActivos(actual.getDiasActivos() + diasPeriodo);
            } else {
                m.setDiasActivos(actual.getDiasActivos());
            }

            if ("Terminado".equals(estadoNuevo)) {
                m.setFechaFin(hoy);
                m.setFechaUltimoInicio(null);
            } else {
                // Postergado
                m.setFechaFin(null);
                m.setFechaUltimoInicio(null);
            }

        } else if (!"En progreso".equals(estadoActual) && "En progreso".equals(estadoNuevo)) {
            // Reanudando → actualizar fecha_ultimo_inicio
            m.setFechaUltimoInicio(hoy);
            m.setDiasActivos(actual.getDiasActivos()); // conservar días acumulados
            m.setFechaFin(null);

        } else {
            // Sin cambio de estado (editando descripción, tipo, etc.)
            m.setDiasActivos(actual.getDiasActivos());
            m.setFechaUltimoInicio(actual.getFechaUltimoInicio());
            m.setFechaFin(actual.getFechaFin());
        }
        // ─────────────────────────────────────────────────────────

        String sql = "UPDATE mantenimiento_equipo "
                + "SET id_equipo = ?, tipo_mantenimiento = ?, estado = ?, "
                + "    fecha_fin = ?, dias_activos = ?, fecha_ultimo_inicio = ?, descripcion = ? "
                + "WHERE id_mantenimiento = ?";

        try (Connection conn = Conn.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt   (1, m.getIdEquipo());
            ps.setString(2, m.getTipoMantenimiento());
            ps.setString(3, m.getEstado());
            ps.setDate  (4, m.getFechaFin());
            ps.setInt   (5, m.getDiasActivos());
            ps.setDate  (6, m.getFechaUltimoInicio());
            ps.setString(7, m.getDescripcion());
            ps.setInt   (8, m.getIdMantenimiento());

            if (ps.executeUpdate() > 0) {
                // Actualizar estado del equipo en equipo_oficina
                String nuevoEstadoEquipo = "Terminado".equals(estadoNuevo) ? "Operativo" : "En mantención";
                new DAOEquipoOficina().actualizarEstado(m.getIdEquipo(), nuevoEstadoEquipo);
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
        int idEquipo = -1;
        String sqlBuscar = "SELECT id_equipo FROM mantenimiento_equipo WHERE id_mantenimiento = ?";

        try (Connection conn = Conn.get();
             PreparedStatement ps = conn.prepareStatement(sqlBuscar)) {
            ps.setInt(1, idMantenimiento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) idEquipo = rs.getInt("id_equipo");
            }
        } catch (SQLException e) {
            System.err.println("[DAOMantenimientoEquipo.delete] Error al buscar equipo: " + e.getMessage());
        }

        String sql = "DELETE FROM mantenimiento_equipo WHERE id_mantenimiento = ?";
        try (Connection conn = Conn.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMantenimiento);
            if (ps.executeUpdate() > 0) {
                if (idEquipo != -1)
                    new DAOEquipoOficina().actualizarEstado(idEquipo, "Operativo");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[DAOMantenimientoEquipo.delete] Error: " + e.getMessage());
        }
        return false;
    }

    // ----------------------------------------------------------
    // READ — historial filtrado (actualizado con nuevas columnas)
    // ----------------------------------------------------------
    public ArrayList<Object[]> listarHistorialFiltrado(
            String numeroSerie,
            String tipoMantenimiento,
            java.sql.Date fechaDesde,
            java.sql.Date fechaHasta) {

        ArrayList<Object[]> lista = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT me.id_mantenimiento, e.numero_serie, e.marca, e.tipo AS tipo_equipo, "
          + "       me.tipo_mantenimiento, me.estado, me.fecha_entrada, "
          + "       me.fecha_fin, me.dias_activos, me.descripcion "
          + "FROM mantenimiento_equipo me "
          + "JOIN equipo_oficina e ON me.id_equipo = e.id_equipo "
          + "WHERE 1=1 "
        );

        ArrayList<Object> params = new ArrayList<>();

        if (numeroSerie != null && !numeroSerie.isBlank()) {
            sql.append("AND e.numero_serie LIKE ? ");
            params.add("%" + numeroSerie.toUpperCase().trim() + "%");
        }
        if (tipoMantenimiento != null && !tipoMantenimiento.isBlank()
                && !tipoMantenimiento.equalsIgnoreCase("Todos")) {
            sql.append("AND me.tipo_mantenimiento = ? ");
            params.add(tipoMantenimiento);
        }
        if (fechaDesde != null) {
            sql.append("AND me.fecha_entrada >= ? ");
            params.add(fechaDesde);
        }
        if (fechaHasta != null) {
            sql.append("AND me.fecha_entrada <= ? ");
            params.add(fechaHasta);
        }
        sql.append("ORDER BY me.fecha_entrada DESC, me.id_mantenimiento DESC");

        try (Connection conn = Conn.get();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                Object val = params.get(i);
                if (val instanceof String)         ps.setString(i + 1, (String) val);
                else if (val instanceof java.sql.Date) ps.setDate(i + 1, (java.sql.Date) val);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // [0] id  [1] serie  [2] marca  [3] tipoEquipo
                    // [4] tipoMant  [5] estado  [6] fechaEntrada
                    // [7] fechaFin  [8] diasActivos  [9] descripcion
                    Object[] fila = {
                        rs.getInt   ("id_mantenimiento"),
                        rs.getString("numero_serie"),
                        rs.getString("marca"),
                        rs.getString("tipo_equipo"),
                        rs.getString("tipo_mantenimiento"),
                        rs.getString("estado"),
                        rs.getDate  ("fecha_entrada"),
                        rs.getDate  ("fecha_fin"),
                        rs.getInt   ("dias_activos"),
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
    // PRIVADO — mapear ResultSet → MantenimientoEquipo
    // ----------------------------------------------------------
    private MantenimientoEquipo mapear(ResultSet rs) throws SQLException {
        MantenimientoEquipo m = new MantenimientoEquipo();
        m.setIdMantenimiento  (rs.getInt   ("id_mantenimiento"));
        m.setIdEquipo         (rs.getInt   ("id_equipo"));
        m.setTipoMantenimiento(rs.getString("tipo_mantenimiento"));
        m.setEstado           (rs.getString("estado"));
        m.setFechaEntrada     (rs.getDate  ("fecha_entrada"));
        m.setFechaFin         (rs.getDate  ("fecha_fin"));
        m.setDiasActivos      (rs.getInt   ("dias_activos"));
        m.setFechaUltimoInicio(rs.getDate  ("fecha_ultimo_inicio"));
        m.setDescripcion      (rs.getString("descripcion"));
        return m;
    }
}