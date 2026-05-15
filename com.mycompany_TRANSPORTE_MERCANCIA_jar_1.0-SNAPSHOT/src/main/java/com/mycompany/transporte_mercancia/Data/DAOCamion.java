/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.transporte_mercancia.Data;

import com.mycompany.transporte_mercancia.Logica.Camion;
import com.mycompany.transporte_mercancia.Logica.Personal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de acceso a datos (DAO) para la entidad {@link Camion}.
 * <p>
 * Proporciona operaciones CRUD sobre la tabla {@code camion} de la base de
 * datos, así como consultas adicionales para filtrar camiones por conductor o
 * disponibilidad.
 * </p>
 *
 * @author TransporteMercancia
 * @version 1.0
 */
public class DAOCamion {

    // ---------------------------------------------------
    // LISTAR TODOS LOS CAMIONES
    // ---------------------------------------------------
    /**
     * Recupera todos los camiones registrados en la base de datos.
     * <p>
     * Para cada camión, si tiene asignado un conductor ({@code id_conductor} no
     * nulo), se consulta el {@link DAOPersonal} para resolver el objeto
     * {@link Personal} correspondiente.
     * </p>
     *
     * @return {@link ArrayList} con todos los objetos {@link Camion}
     * encontrados; lista vacía si no hay registros o si ocurre un error de base
     * de datos.
     */
    public ArrayList<Camion> listarTodos() {
        ArrayList<Camion> lista = new ArrayList<>();

        String sql = """
            SELECT id_camion, marca, modelo, anio,
                   kilometraje_total, id_conductor, matricula
            FROM camion
        """;

        try (Connection conn = Conn.get(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            DAOPersonal daoP = new DAOPersonal();

            while (rs.next()) {
                Camion c = new Camion();
                c.setIdCamion(rs.getInt("id_camion"));
                c.setMarca(rs.getString("marca"));
                c.setModelo(rs.getString("modelo"));
                c.setAnio(rs.getInt("anio"));
                c.setKilometrajeTotal(rs.getInt("kilometraje_total"));
                c.setMatricula(rs.getString("matricula"));

                int idCo = rs.getInt("id_conductor");
                if (!rs.wasNull()) {
                    Personal p = daoP.buscarPorId(idCo);
                    c.setConductor(p);
                }

                lista.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // ---------------------------------------------------
    // LISTAR CAMIONES DISPONIBLES (sin mantenimiento activo)
    // ---------------------------------------------------
    /**
     * Recupera los camiones que no tienen ningún mantenimiento activo o futuro
     * programado.
     * <p>
     * Un camión se considera disponible cuando no existe ningún registro en la
     * tabla {@code mantenimiento} cuya {@code fechaSalida} sea igual o
     * posterior a la fecha actual.
     * </p>
     *
     * @return {@link List} con los camiones disponibles; lista vacía si todos
     * están en mantenimiento o si ocurre un error de base de datos.
     */
    public List<Camion> listarDisponibles() {
        List<Camion> lista = new ArrayList<>();

        String sql = "SELECT * FROM camion c "
                + "WHERE NOT EXISTS ("
                + "  SELECT 1 FROM mantenimiento m "
                + "  WHERE m.id_camion = c.id_camion "
                + "  AND m.fecha_fin >= CURDATE()"
                + ")";

        try (Connection conn = Conn.get(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Camion c = new Camion();
                c.setIdCamion(rs.getInt("id_camion"));
                c.setMatricula(rs.getString("matricula"));
                c.setMarca(rs.getString("marca"));
                c.setModelo(rs.getString("modelo"));
                lista.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    /**
     * Verifica si una matrícula ya se encuentra registrada en la base de datos.
     * <p>
     * Utilizado para evitar duplicados al crear o actualizar un camión.
     * </p>
     *
     * @param matricula matrícula a verificar; no debe ser {@code null}.
     * @return {@code true} si la matrícula ya existe en la tabla
     * {@code camion}; {@code false} en caso contrario o si ocurre un error de
     * base de datos.
     */
    // Para REGISTRO nuevo — verifica si la matrícula ya existe en toda la tabla
    public boolean existeMatricula(String matricula) {
        String sql = "SELECT COUNT(*) FROM camion WHERE matricula = ?";

        try (Connection conn = Conn.get(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, matricula);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

// Para EDICIÓN — verifica duplicado pero ignora el camión que se está editando
    public boolean existeMatricula(String matricula, int idExcluir) {
        String sql = "SELECT COUNT(*) FROM camion WHERE matricula = ? AND id_camion != ?";

        try (Connection conn = Conn.get(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, matricula);
            pstmt.setInt(2, idExcluir);
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
    // BUSCAR CAMION POR ID
    // ---------------------------------------------------
    /**
     * Busca un camión en la base de datos por su identificador único.
     * <p>
     * Si el camión tiene asignado un conductor, se resuelve el objeto
     * {@link Personal} correspondiente mediante
     * {@link DAOPersonal#buscarPorId(int)}.
     * </p>
     *
     * @param id identificador del camión a buscar.
     * @return el objeto {@link Camion} encontrado, o {@code null} si no existe
     * ningún camión con ese ID o si ocurre un error de base de datos.
     */
    public Camion buscarPorId(Integer id) {
        Camion c = null;

        String sql = """
            SELECT id_camion, marca, modelo, anio,
                   kilometraje_total, id_conductor, matricula
            FROM camion
            WHERE id_camion = ?
        """;

        try (Connection conn = Conn.get(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                c = new Camion();
                c.setIdCamion(rs.getInt("id_camion"));
                c.setMarca(rs.getString("marca"));
                c.setModelo(rs.getString("modelo"));
                c.setAnio(rs.getInt("anio"));
                c.setKilometrajeTotal(rs.getInt("kilometraje_total"));
                c.setMatricula(rs.getString("matricula"));

                int idCo = rs.getInt("id_conductor");
                if (!rs.wasNull()) {
                    DAOPersonal daoP = new DAOPersonal();
                    Personal p = daoP.buscarPorId(idCo);
                    c.setConductor(p);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return c;
    }

    // ---------------------------------------------------
    // LISTAR CAMIONES POR CONDUCTOR
    // ---------------------------------------------------
    /**
     * Recupera todos los camiones asignados a un conductor específico.
     * <p>
     * El objeto {@link Personal} del conductor se consulta una sola vez y se
     * reutiliza para todos los camiones retornados, optimizando las consultas a
     * la base de datos.
     * </p>
     *
     * @param idConductor identificador del conductor cuyos camiones se desean
     * obtener.
     * @return {@link ArrayList} con los camiones asignados al conductor
     * indicado; lista vacía si no tiene camiones asignados o si ocurre un error
     * de base de datos.
     */
    public ArrayList<Camion> listarPorConductor(int idConductor) {
        ArrayList<Camion> lista = new ArrayList<>();

        String sql = """
            SELECT id_camion, marca, modelo, anio,
                   kilometraje_total, id_conductor, matricula
            FROM camion
            WHERE id_conductor = ?
        """;

        try (Connection conn = Conn.get(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idConductor);
            ResultSet rs = pstmt.executeQuery();

            DAOPersonal daoP = new DAOPersonal();
            Personal p = daoP.buscarPorId(idConductor);

            while (rs.next()) {
                Camion c = new Camion();
                c.setIdCamion(rs.getInt("id_camion"));
                c.setMarca(rs.getString("marca"));
                c.setModelo(rs.getString("modelo"));
                c.setAnio(rs.getInt("anio"));
                c.setKilometrajeTotal(rs.getInt("kilometraje_total"));
                c.setMatricula(rs.getString("matricula"));
                c.setConductor(p);
                lista.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // ---------------------------------------------------
    // CREAR CAMION
    // ---------------------------------------------------
    /**
     * Inserta un nuevo camión en la base de datos.
     * <p>
     * Si el camión no tiene conductor asignado
     * ({@code getConductor() == null}), el campo {@code id_conductor} se
     * almacena como {@code NULL}.
     * </p>
     *
     * @param c objeto {@link Camion} con los datos a insertar; no debe ser
     * {@code null}.
     */
    public void create(Camion c) {
        String sql = """
            INSERT INTO camion (marca, modelo, anio, kilometraje_total, id_conductor, matricula)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = Conn.get(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, c.getMarca());
            pstmt.setString(2, c.getModelo());
            pstmt.setInt(3, c.getAnio());
            pstmt.setInt(4, c.getKilometrajeTotal());

            if (c.getConductor() != null) {
                pstmt.setInt(5, c.getConductor().getIdPersonal());
            } else {
                pstmt.setNull(5, java.sql.Types.INTEGER);
            }

            pstmt.setString(6, c.getMatricula());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------
    // ACTUALIZAR CAMION
    // ---------------------------------------------------
    /**
     * Actualiza los datos de un camión existente en la base de datos.
     * <p>
     * La fila a actualizar se identifica por el {@code id_camion} del objeto
     * recibido. Si el conductor es {@code null}, el campo {@code id_conductor}
     * se establece a {@code NULL}.
     * </p>
     *
     * @param c objeto {@link Camion} con los datos actualizados; debe tener un
     * {@code idCamion} válido correspondiente a un registro existente.
     */
    public void update(Camion c) {
        String sql = """
            UPDATE camion
            SET marca = ?, modelo = ?,
                anio = ?, kilometraje_total = ?,
                id_conductor = ?, matricula = ?
            WHERE id_camion = ?
        """;

        try (Connection conn = Conn.get(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, c.getMarca());
            pstmt.setString(2, c.getModelo());
            pstmt.setInt(3, c.getAnio());
            pstmt.setInt(4, c.getKilometrajeTotal());

            if (c.getConductor() != null) {
                pstmt.setInt(5, c.getConductor().getIdPersonal());
            } else {
                pstmt.setNull(5, java.sql.Types.INTEGER);
            }

            pstmt.setString(6, c.getMatricula());
            pstmt.setInt(7, c.getIdCamion());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------
    // ELIMINAR CAMION
    // ---------------------------------------------------
    /**
     * Elimina un camión de la base de datos según su identificador.
     *
     * @param id identificador del camión a eliminar.
     */
    public void delete(Integer id) {
        String sql = "DELETE FROM camion WHERE id_camion = ?";

        try (Connection conn = Conn.get(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
