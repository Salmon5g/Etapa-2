/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.transporte_mercancia.Data;

import com.mycompany.transporte_mercancia.Logica.Mantenimiento;
import java.sql.*;
import java.util.ArrayList;

public class DAOMantenimiento {

    // -------------------------------------------------------
    // LISTAR TODOS
    // -------------------------------------------------------
    public ArrayList<Mantenimiento> listarTodos() {
        ArrayList<Mantenimiento> lista = new ArrayList<>();
        String sql = "SELECT m.id_mantenimiento, c.matricula, m.estado, "
                   + "       m.fechaEntrada, m.fecha_fin, m.dias_activos, "
                   + "       m.fecha_ultimo_inicio, m.descripcion "
                   + "FROM mantenimiento m "
                   + "JOIN camion c ON m.id_camion = c.id_camion";

        try (Connection conn = Conn.get();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // -------------------------------------------------------
    // BUSCAR POR ID
    // -------------------------------------------------------
    public Mantenimiento buscarPorId(int id) {
        String sql = "SELECT m.id_mantenimiento, c.matricula, m.estado, "
                   + "       m.fechaEntrada, m.fecha_fin, m.dias_activos, "
                   + "       m.fecha_ultimo_inicio, m.descripcion "
                   + "FROM mantenimiento m "
                   + "JOIN camion c ON m.id_camion = c.id_camion "
                   + "WHERE m.id_mantenimiento = ?";

        try (Connection conn = Conn.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // -------------------------------------------------------
    // HELPER — mapear ResultSet → Mantenimiento
    // -------------------------------------------------------
    private Mantenimiento mapear(ResultSet rs) throws SQLException {
        Mantenimiento m = new Mantenimiento();
        m.setIdMantenimiento(rs.getInt("id_mantenimiento"));
        m.setMatricula(rs.getString("matricula"));
        m.setEstado(rs.getString("estado"));
        m.setFechaEntrada(rs.getDate("fechaEntrada"));
        m.setFechaFin(rs.getDate("fecha_fin"));
        m.setDiasActivos(rs.getInt("dias_activos"));
        m.setFechaUltimoInicio(rs.getDate("fecha_ultimo_inicio"));
        m.setDescripcion(rs.getString("descripcion"));
        return m;
    }

    // -------------------------------------------------------
    // VERIFICAR MANTENIMIENTO ACTIVO (actualizado al nuevo sistema)
    // -------------------------------------------------------
    public boolean tieneMantenimientoActivo(int idCamion) {
        String sql = "SELECT COUNT(*) FROM mantenimiento "
                   + "WHERE id_camion = ? AND estado IN ('En progreso', 'Postergado')";

        try (Connection conn = Conn.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCamion);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // -------------------------------------------------------
    // CREAR — siempre inicia como "En progreso"
    // -------------------------------------------------------
    public void create(Mantenimiento m) {
        String sql = "INSERT INTO mantenimiento "
                   + "(id_camion, fechaEntrada, estado, dias_activos, fecha_ultimo_inicio, descripcion) "
                   + "VALUES ((SELECT id_camion FROM camion WHERE matricula = ?), "
                   + "        ?, 'En progreso', 0, ?, ?)";

        try (Connection conn = Conn.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, m.getMatricula());
            ps.setDate(2, m.getFechaEntrada());
            ps.setDate(3, m.getFechaEntrada()); // fecha_ultimo_inicio = fechaEntrada
            ps.setString(4, m.getDescripcion());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------
    // ACTUALIZAR — lógica de transición de estados
    // -------------------------------------------------------
    public void update(Mantenimiento m) {
        Mantenimiento actual = buscarPorId(m.getIdMantenimiento());
        if (actual == null) return;

        String estadoAnterior = actual.getEstado();
        String estadoNuevo    = m.getEstado();

        String sql;

        if ("Postergado".equals(estadoNuevo) && "En progreso".equals(estadoAnterior)) {
            // Acumular días y limpiar fecha_ultimo_inicio
            sql = "UPDATE mantenimiento SET "
                + "id_camion = (SELECT id_camion FROM camion WHERE matricula = ?), "
                + "descripcion = ?, estado = 'Postergado', "
                + "dias_activos = dias_activos + DATEDIFF(CURDATE(), fecha_ultimo_inicio), "
                + "fecha_ultimo_inicio = NULL "
                + "WHERE id_mantenimiento = ?";

        } else if ("En progreso".equals(estadoNuevo) && "Postergado".equals(estadoAnterior)) {
            // Reanudar: asignar fecha_ultimo_inicio = hoy
            sql = "UPDATE mantenimiento SET "
                + "id_camion = (SELECT id_camion FROM camion WHERE matricula = ?), "
                + "descripcion = ?, estado = 'En progreso', "
                + "fecha_ultimo_inicio = CURDATE() "
                + "WHERE id_mantenimiento = ?";

        } else if ("Terminado".equals(estadoNuevo)) {
            if ("En progreso".equals(estadoAnterior)) {
                // Acumular días finales + cerrar
                sql = "UPDATE mantenimiento SET "
                    + "id_camion = (SELECT id_camion FROM camion WHERE matricula = ?), "
                    + "descripcion = ?, estado = 'Terminado', "
                    + "dias_activos = dias_activos + DATEDIFF(CURDATE(), fecha_ultimo_inicio), "
                    + "fecha_fin = CURDATE(), fecha_ultimo_inicio = NULL "
                    + "WHERE id_mantenimiento = ?";
            } else {
                // Venía de Postergado (dias_activos ya acumulados)
                sql = "UPDATE mantenimiento SET "
                    + "id_camion = (SELECT id_camion FROM camion WHERE matricula = ?), "
                    + "descripcion = ?, estado = 'Terminado', "
                    + "fecha_fin = CURDATE(), fecha_ultimo_inicio = NULL "
                    + "WHERE id_mantenimiento = ?";
            }

        } else {
            // Sin cambio de estado (ej: editar descripción en mismo estado)
            sql = "UPDATE mantenimiento SET "
                + "id_camion = (SELECT id_camion FROM camion WHERE matricula = ?), "
                + "descripcion = ?, estado = ? "
                + "WHERE id_mantenimiento = ?";

            try (Connection conn = Conn.get();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, m.getMatricula());
                ps.setString(2, m.getDescripcion());
                ps.setString(3, estadoNuevo);
                ps.setInt(4, m.getIdMantenimiento());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return;
        }

        try (Connection conn = Conn.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, m.getMatricula());
            ps.setString(2, m.getDescripcion());
            ps.setInt(3, m.getIdMantenimiento());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------
    // ELIMINAR
    // -------------------------------------------------------
    public void delete(int id) {
        String sql = "DELETE FROM mantenimiento WHERE id_mantenimiento = ?";

        try (Connection conn = Conn.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}