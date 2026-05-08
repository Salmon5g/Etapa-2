/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.transporte_mercancia.Data;

import com.mycompany.transporte_mercancia.Logica.Personal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Clase de acceso a datos (DAO) para la entidad {@link Personal}.
 * <p>
 * Gestiona todas las operaciones CRUD sobre la tabla {@code personal} de la
 * base de datos, incluyendo la autenticación de usuarios, búsqueda por rol o
 * identificador, y verificación de RUT duplicado.
 * </p>
 *
 * @author TransporteMercancia
 * @version 1.0
 */
public class DAOPersonal {

    // ---------------------------------------------------
    // LOGIN
    // ---------------------------------------------------
    /**
     * Autentica a un usuario verificando su RUT y contraseña contra la base de
     * datos.
     *
     * @param rut RUT del usuario que intenta iniciar sesión; no debe ser
     * {@code null}.
     * @param contrasena contraseña en texto plano del usuario; no debe ser
     * {@code null}.
     * @return el objeto {@link Personal} correspondiente al usuario
     * autenticado, o {@code null} si las credenciales no coinciden o si ocurre
     * un error de base de datos.
     */
    public Personal login(String rut, String contrasena) {
        Personal p = null;
        String sql = "SELECT id_personal, nombre, rut, contrasena, rol "
                + "FROM personal WHERE rut = ? AND contrasena = ?";

        try (Connection conn = Conn.get(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, rut);
            pstmt.setString(2, contrasena);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                p = new Personal();
                p.setIdPersonal(rs.getInt("id_personal"));
                p.setNombre(rs.getString("nombre"));
                p.setRut(rs.getString("rut"));
                p.setContrasena(rs.getString("contrasena"));
                p.setRol(rs.getString("rol"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return p;
    }

    // ---------------------------------------------------
    // VERIFICAR RUT DUPLICADO  ← corregido: tabla personal
    // ---------------------------------------------------
    /**
     * Verifica si un RUT ya se encuentra registrado en la base de datos.
     * <p>
     * Utilizado para prevenir duplicados al crear o actualizar un registro de
     * personal.
     * </p>
     *
     * @param rut RUT a verificar; no debe ser {@code null}.
     * @return {@code true} si el RUT ya existe en la tabla {@code personal};
     * {@code false} en caso contrario o si ocurre un error de base de datos.
     */
    public boolean existeRut(String rut) {
        String sql = "SELECT COUNT(*) FROM personal WHERE rut = ?";

        try (Connection conn = Conn.get(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, rut);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Para EDICIÓN — ignora el personal que se está editando
    public boolean existeRut(String rut, int idExcluir) {
        String sql = "SELECT COUNT(*) FROM personal WHERE rut = ? AND id_personal != ?";

        try (Connection conn = Conn.get(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, rut);
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
    // LISTAR TODOS
    // ---------------------------------------------------
    /**
     * Recupera todos los registros de personal almacenados en la base de datos.
     *
     * @return {@link ArrayList} con todos los objetos {@link Personal}
     * encontrados; lista vacía si no hay registros o si ocurre un error de base
     * de datos.
     */
    public ArrayList<Personal> listarTodos() {
        ArrayList<Personal> lista = new ArrayList<>();
        String sql = "SELECT id_personal, nombre, rut, contrasena, rol FROM personal";

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
    // LISTAR POR ROL  ← nuevo (usado en FrmConductores y FrmCamiones)
    // ---------------------------------------------------
    /**
     * Recupera todos los registros de personal que tienen asignado un rol
     * específico.
     * <p>
     * Utilizado principalmente para filtrar conductores en los formularios
     * {@code FrmConductores} y {@code FrmCamiones}.
     * </p>
     *
     * @param rol nombre del rol por el que se desea filtrar (p. ej.
     * {@code "conductor"}).
     * @return {@link ArrayList} con los objetos {@link Personal} que poseen el
     * rol indicado; lista vacía si no hay coincidencias o si ocurre un error de
     * base de datos.
     */
    public ArrayList<Personal> listarPorRol(String rol) {
        ArrayList<Personal> lista = new ArrayList<>();
        String sql = "SELECT id_personal, nombre, rut, contrasena, rol "
                + "FROM personal WHERE rol = ?";

        try (Connection conn = Conn.get(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, rol);
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
    // BUSCAR POR ID  ← nuevo (usado en DAOCamion)
    // ---------------------------------------------------
    /**
     * Busca un registro de personal por su identificador único.
     * <p>
     * Utilizado internamente por {@link DAOCamion} para resolver el conductor
     * asociado a un camión.
     * </p>
     *
     * @param id identificador del personal a buscar.
     * @return el objeto {@link Personal} encontrado, o {@code null} si no
     * existe ningún registro con ese ID o si ocurre un error de base de datos.
     */
    public Personal buscarPorId(int id) {
        Personal p = null;
        String sql = "SELECT id_personal, nombre, rut, contrasena, rol "
                + "FROM personal WHERE id_personal = ?";

        try (Connection conn = Conn.get(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                p = mapear(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return p;
    }

    // ---------------------------------------------------
    // CREAR
    // ---------------------------------------------------
    /**
     * Inserta un nuevo registro de personal en la base de datos.
     *
     * @param p objeto {@link Personal} con los datos a insertar; no debe ser
     * {@code null} y su RUT no debe estar ya registrado.
     */
    public void create(Personal p) {
        String sql = "INSERT INTO personal (nombre, rut, contrasena, rol) VALUES (?, ?, ?, ?)";

        try (Connection conn = Conn.get(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, p.getNombre());
            pstmt.setString(2, p.getRut());
            pstmt.setString(3, p.getContrasena());
            pstmt.setString(4, p.getRol());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------
    // ACTUALIZAR
    // ---------------------------------------------------
    /**
     * Actualiza los datos de un registro de personal existente en la base de
     * datos.
     * <p>
     * La fila a modificar se identifica por el {@code idPersonal} del objeto
     * recibido.
     * </p>
     *
     * @param p objeto {@link Personal} con los datos actualizados; debe tener
     * un {@code idPersonal} válido correspondiente a un registro existente.
     */
    public void update(Personal p) {
        String sql = "UPDATE personal SET nombre = ?, rut = ?, contrasena = ?, rol = ? "
                + "WHERE id_personal = ?";

        try (Connection conn = Conn.get(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, p.getNombre());
            pstmt.setString(2, p.getRut());
            pstmt.setString(3, p.getContrasena());
            pstmt.setString(4, p.getRol());
            pstmt.setInt(5, p.getIdPersonal());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------
    // ELIMINAR
    // ---------------------------------------------------
    /**
     * Elimina un registro de personal de la base de datos según su
     * identificador.
     *
     * @param id identificador del personal a eliminar.
     */
    public void delete(int id) {
        String sql = "DELETE FROM personal WHERE id_personal = ?";

        try (Connection conn = Conn.get(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------
    // HELPER PRIVADO — mapea ResultSet → Personal
    // ---------------------------------------------------
    /**
     * Método auxiliar privado que convierte una fila del {@link ResultSet} en
     * un objeto {@link Personal}.
     * <p>
     * Centraliza el mapeo de columnas para evitar duplicación de código en los
     * métodos de lectura
     * ({@link #listarTodos()}, {@link #listarPorRol(String)}, {@link #buscarPorId(int)}).
     * </p>
     *
     * @param rs {@link ResultSet} posicionado en la fila a mapear; no debe ser
     * {@code null}.
     * @return nuevo objeto {@link Personal} con los datos de la fila actual.
     * @throws SQLException si ocurre un error al leer las columnas del
     * {@link ResultSet}.
     */
    private Personal mapear(ResultSet rs) throws SQLException {
        Personal p = new Personal();
        p.setIdPersonal(rs.getInt("id_personal"));
        p.setNombre(rs.getString("nombre"));
        p.setRut(rs.getString("rut"));
        p.setContrasena(rs.getString("contrasena"));
        p.setRol(rs.getString("rol"));
        return p;
    }
}
