/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.transporte_mercancia.Data;

import com.mycompany.transporte_mercancia.Logica.EquipoOficina;
import java.sql.*;
import java.util.ArrayList;

/**
 * DAO para la tabla equipo_oficina. Proporciona operaciones CRUD y consultas
 * auxiliares.
 *
 * Métodos disponibles: create(EquipoOficina) → INSERT update(EquipoOficina) →
 * UPDATE delete(int) → DELETE por id_equipo buscarPorId(int) → SELECT por
 * id_equipo listarTodos() → SELECT todos existeNumeroSerie(String) → verifica
 * duplicado de numero_serie
 */
public class DAOEquipoOficina {

    // ----------------------------------------------------------
    // CREATE
    // ----------------------------------------------------------
    /**
     * Inserta un nuevo equipo de oficina en la base de datos.
     *
     * @param equipo objeto con tipo, marca, numero_serie y estado cargados.
     * fecha_registro la asigna automáticamente la BD.
     * @return true si se insertó correctamente, false en caso de error.
     */
    public boolean create(EquipoOficina equipo) {
        String sql = "INSERT INTO equipo_oficina (tipo, marca, numero_serie, estado) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conn = Conn.get(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, equipo.getTipo());
            ps.setString(2, equipo.getMarca());
            ps.setString(3, equipo.getNumeroSerie());
            ps.setString(4, equipo.getEstado());

            int filas = ps.executeUpdate();

            // Recuperar el id generado por AUTO_INCREMENT y cargarlo en el objeto
            if (filas > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        equipo.setIdEquipo(keys.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("[DAOEquipoOficina.create] Error: " + e.getMessage());
        }
        return false;
    }

    // ----------------------------------------------------------
    // READ — buscar por ID
    // ----------------------------------------------------------
    /**
     * Busca un equipo por su id_equipo.
     *
     * @param id id_equipo a buscar.
     * @return objeto EquipoOficina si existe, null si no se encuentra.
     */
    public EquipoOficina buscarPorId(int id) {
        String sql = "SELECT id_equipo, tipo, marca, numero_serie, estado, fecha_registro "
                + "FROM equipo_oficina WHERE id_equipo = ?";

        try (Connection conn = Conn.get(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("[DAOEquipoOficina.buscarPorId] Error: " + e.getMessage());
        }
        return null;
    }

    public void actualizarEstado(int idEquipo, String nuevoEstado) {
        String sql = "UPDATE equipo_oficina SET estado = ? WHERE id_equipo = ?";
        try (Connection conn = Conn.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idEquipo);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ----------------------------------------------------------
    // READ — listar todos
    // ----------------------------------------------------------
    /**
     * Retorna todos los equipos de oficina registrados.
     *
     * @return lista de EquipoOficina; lista vacía si no hay registros.
     */
    public ArrayList<EquipoOficina> listarTodos() {
        String sql = "SELECT id_equipo, tipo, marca, numero_serie, estado, fecha_registro "
                + "FROM equipo_oficina ORDER BY id_equipo ASC";

        ArrayList<EquipoOficina> lista = new ArrayList<>();

        try (Connection conn = Conn.get(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            System.err.println("[DAOEquipoOficina.listarTodos] Error: " + e.getMessage());
        }
        return lista;
    }

    public ArrayList<EquipoOficina> listarOperativos() {
        String sql = "SELECT id_equipo, tipo, marca, numero_serie, estado, fecha_registro "
                + "FROM equipo_oficina WHERE estado = 'Operativo' ORDER BY id_equipo ASC";

        ArrayList<EquipoOficina> lista = new ArrayList<>();

        try (Connection conn = Conn.get(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            System.err.println("[DAOEquipoOficina.listarOperativos] Error: " + e.getMessage());
        }
        return lista;
    }

    // ----------------------------------------------------------
    // UPDATE
    // ----------------------------------------------------------
    /**
     * Actualiza los datos de un equipo existente. Se actualiza por id_equipo;
     * fecha_registro NO se modifica.
     *
     * @param equipo objeto con id_equipo y campos actualizados.
     * @return true si se actualizó al menos una fila, false en caso de error.
     */
    public boolean update(EquipoOficina equipo) {
        String sql = "UPDATE equipo_oficina "
                + "SET tipo = ?, marca = ?, numero_serie = ?, estado = ? "
                + "WHERE id_equipo = ?";

        try (Connection conn = Conn.get(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, equipo.getTipo());
            ps.setString(2, equipo.getMarca());
            ps.setString(3, equipo.getNumeroSerie());
            ps.setString(4, equipo.getEstado());
            ps.setInt(5, equipo.getIdEquipo());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[DAOEquipoOficina.update] Error: " + e.getMessage());
        }
        return false;
    }

    // ----------------------------------------------------------
    // DELETE
    // ----------------------------------------------------------
    /**
     * Elimina un equipo por su id_equipo.
     *
     * @param id id_equipo del equipo a eliminar.
     * @return true si se eliminó correctamente, false en caso de error.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM equipo_oficina WHERE id_equipo = ?";

        try (Connection conn = Conn.get(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[DAOEquipoOficina.delete] Error: " + e.getMessage());
        }
        return false;
    }

    // ----------------------------------------------------------
    // AUXILIAR — verificar número de serie duplicado
    // ----------------------------------------------------------
    /**
     * Verifica si ya existe un equipo con el mismo número de serie. Útil para
     * validar antes de hacer un INSERT.
     *
     * @param numeroSerie número de serie a verificar.
     * @return true si ya existe, false si está disponible.
     */
    public boolean existeNumeroSerie(String numeroSerie) {
        String sql = "SELECT 1 FROM equipo_oficina WHERE numero_serie = ? LIMIT 1";

        try (Connection conn = Conn.get(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, numeroSerie);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("[DAOEquipoOficina.existeNumeroSerie] Error: " + e.getMessage());
        }
        return false;
    }

    // ----------------------------------------------------------
    // PRIVADO — mapear ResultSet → EquipoOficina
    // ----------------------------------------------------------
    /**
     * Convierte una fila del ResultSet en un objeto EquipoOficina. Centraliza
     * el mapeo para evitar repetición en cada método de lectura.
     */
    private EquipoOficina mapear(ResultSet rs) throws SQLException {
        EquipoOficina e = new EquipoOficina();
        e.setIdEquipo(rs.getInt("id_equipo"));
        e.setTipo(rs.getString("tipo"));
        e.setMarca(rs.getString("marca"));
        e.setNumeroSerie(rs.getString("numero_serie"));
        e.setEstado(rs.getString("estado"));
        e.setFechaRegistro(rs.getTimestamp("fecha_registro"));
        return e;
    }
}
