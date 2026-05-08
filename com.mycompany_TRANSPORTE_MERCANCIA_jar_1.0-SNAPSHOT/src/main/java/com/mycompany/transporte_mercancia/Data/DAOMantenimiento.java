/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.transporte_mercancia.Data;

import com.mycompany.transporte_mercancia.Logica.Mantenimiento;
import java.sql.*;
import java.util.ArrayList;

/**
 * Clase de acceso a datos (DAO) para la entidad {@link Mantenimiento}.
 * <p>
 * Gestiona las operaciones CRUD sobre la tabla {@code mantenimiento} de la base de datos.
 * Las consultas de lectura realizan un JOIN con la tabla {@code camion} para obtener
 * directamente la matrícula asociada a cada registro de mantenimiento.
 * </p>
 *
 * @author TransporteMercancia
 * @version 1.0
 */
public class DAOMantenimiento {

    // ---------------------------------------------------
    // LISTAR TODOS — JOIN para traer matricula directo
    // ---------------------------------------------------

    /**
     * Recupera todos los registros de mantenimiento almacenados en la base de datos.
     * <p>
     * Realiza un {@code JOIN} con la tabla {@code camion} para incluir la matrícula
     * del camión en cada objeto {@link Mantenimiento} retornado.
     * </p>
     *
     * @return {@link ArrayList} con todos los registros de mantenimiento;
     *         lista vacía si no hay registros o si ocurre un error de base de datos.
     */
    public ArrayList<Mantenimiento> listarTodos() {
        ArrayList<Mantenimiento> lista = new ArrayList<>();
        String sql = "SELECT m.id_mantenimiento, c.matricula, m.fechaEntrada, m.fechaSalida, m.descripcion "
                + "FROM mantenimiento m "
                + "JOIN camion c ON m.id_camion = c.id_camion";

        try (Connection conn = Conn.get(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Mantenimiento m = new Mantenimiento();
                m.setIdMantenimiento(rs.getInt("id_mantenimiento"));
                m.setMatricula(rs.getString("matricula"));
                m.setFechaEntrada(rs.getDate("fechaEntrada"));
                m.setFechaSalida(rs.getDate("fechaSalida"));
                m.setDescripcion(rs.getString("descripcion"));
                lista.add(m);
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
     * Busca un registro de mantenimiento por su identificador único.
     * <p>
     * Realiza un {@code JOIN} con la tabla {@code camion} para incluir la matrícula
     * en el objeto retornado.
     * </p>
     *
     * @param id identificador del mantenimiento a buscar.
     * @return el objeto {@link Mantenimiento} encontrado, o {@code null} si no existe
     *         ningún registro con ese ID o si ocurre un error de base de datos.
     */
    public Mantenimiento buscarPorId(int id) {
        Mantenimiento m = null;
        String sql = "SELECT m.id_mantenimiento, c.matricula, m.fechaEntrada, m.fechaSalida, m.descripcion "
                + "FROM mantenimiento m "
                + "JOIN camion c ON m.id_camion = c.id_camion "
                + "WHERE m.id_mantenimiento = ?";

        try (Connection conn = Conn.get(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                m = new Mantenimiento();
                m.setIdMantenimiento(rs.getInt("id_mantenimiento"));
                m.setMatricula(rs.getString("matricula"));
                m.setFechaEntrada(rs.getDate("fechaEntrada"));
                m.setFechaSalida(rs.getDate("fechaSalida"));
                m.setDescripcion(rs.getString("descripcion"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return m;
    }

    // ---------------------------------------------------
    // VERIFICAR SI EL CAMION YA TIENE MANTENIMIENTO AGENDADO
    // ---------------------------------------------------

    /**
     * Verifica si un camión tiene algún mantenimiento programado activo o futuro.
     * <p>
     * Se considera que hay un mantenimiento programado cuando existe al menos un registro
     * cuya {@code fechaSalida} sea igual o posterior a la fecha actual ({@code CURDATE()}).
     * </p>
     *
     * @param idCamion identificador del camión a verificar.
     * @return {@code true} si el camión tiene un mantenimiento programado vigente;
     *         {@code false} en caso contrario o si ocurre un error de base de datos.
     */
    public boolean tieneMantenimientoProgramado(int idCamion) {
        String sql = "SELECT COUNT(*) FROM mantenimiento "
                + "WHERE id_camion = ? AND fechaSalida >= CURDATE()";

        try (Connection conn = Conn.get(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCamion);
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
    // CREAR — subquery para obtener id_camion desde matricula
    // ---------------------------------------------------

    /**
     * Inserta un nuevo registro de mantenimiento en la base de datos.
     * <p>
     * Utiliza una subconsulta para resolver el {@code id_camion} a partir de la
     * matrícula almacenada en el objeto {@link Mantenimiento}. Si la base de datos
     * detecta que {@code fechaSalida} no es posterior a {@code fechaEntrada}
     * (restricción {@code chk_fecha_salida}), se lanza una {@link RuntimeException}
     * con un mensaje descriptivo.
     * </p>
     *
     * @param m objeto {@link Mantenimiento} con los datos a insertar; no debe ser {@code null}
     *          y su matrícula debe corresponder a un camión existente.
     * @throws RuntimeException si la fecha de salida no es posterior a la fecha de entrada.
     */
    public void create(Mantenimiento m) {
        String sql = "INSERT INTO mantenimiento (id_camion, fechaEntrada, fechaSalida, descripcion) "
                + "VALUES ((SELECT id_camion FROM camion WHERE matricula = ?), ?, ?, ?)";

        try (Connection conn = Conn.get(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, m.getMatricula());
            pstmt.setDate(2, m.getFechaEntrada());
            pstmt.setDate(3, m.getFechaSalida());
            pstmt.setString(4, m.getDescripcion());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            if (e.getMessage().contains("chk_fecha_salida")) {
                throw new RuntimeException("La fecha de salida debe ser posterior a la fecha de entrada.");
            }
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------
    // ACTUALIZAR
    // ---------------------------------------------------

    /**
     * Actualiza un registro de mantenimiento existente en la base de datos.
     * <p>
     * La fila a modificar se identifica por el {@code idMantenimiento} del objeto recibido.
     * Al igual que en {@link #create(Mantenimiento)}, el {@code id_camion} se resuelve
     * mediante subconsulta a partir de la matrícula. Si se viola la restricción de fechas
     * ({@code chk_fecha_salida}), se lanza una {@link RuntimeException}.
     * </p>
     *
     * @param m objeto {@link Mantenimiento} con los datos actualizados; debe tener un
     *          {@code idMantenimiento} válido correspondiente a un registro existente.
     * @throws RuntimeException si la fecha de salida no es posterior a la fecha de entrada.
     */
    public void update(Mantenimiento m) {
        String sql = "UPDATE mantenimiento "
                + "SET id_camion = (SELECT id_camion FROM camion WHERE matricula = ?), "
                + "fechaEntrada = ?, fechaSalida = ?, descripcion = ? "
                + "WHERE id_mantenimiento = ?";

        try (Connection conn = Conn.get(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, m.getMatricula());
            pstmt.setDate(2, m.getFechaEntrada());
            pstmt.setDate(3, m.getFechaSalida());
            pstmt.setString(4, m.getDescripcion());
            pstmt.setInt(5, m.getIdMantenimiento());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            if (e.getMessage().contains("chk_fecha_salida")) {
                throw new RuntimeException("La fecha de salida debe ser posterior a la fecha de entrada.");
            }
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------
    // ELIMINAR
    // ---------------------------------------------------

    /**
     * Elimina un registro de mantenimiento de la base de datos según su identificador.
     *
     * @param id identificador del mantenimiento a eliminar.
     */
    public void delete(int id) {
        String sql = "DELETE FROM mantenimiento WHERE id_mantenimiento = ?";

        try (Connection conn = Conn.get(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}