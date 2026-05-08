package com.mycompany.transporte_mercancia.Data;

import com.mycompany.transporte_mercancia.Logica.Personal;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * Pruebas unitarias (integración con BD) para DAOPersonal (RF-05).
 * REQUISITO: La base de datos MySQL debe estar activa en localhost:3306
 *            con la base de datos "Transporte_mercancia".
 *
 * Cada prueba crea datos propios de prueba y los elimina al finalizar
 * para no afectar los datos reales del sistema.
 *
 * @author Pruebas Unitarias - Sistema Transporte Mercancía
 */
@DisplayName("Pruebas Unitarias - DAOPersonal (CRUD + Login)")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DAOPersonalTest {

    private static DAOPersonal dao;
    private static boolean dbDisponible = false;
    private static final String RUT_PRUEBA = "99-TEST-P";

    @BeforeAll
    static void verificarConexion() {
        dao = new DAOPersonal();
        try (Connection c = Conn.get()) {
            dbDisponible = (c != null && !c.isClosed());
        } catch (Exception e) {
            dbDisponible = false;
        }
    }

    @AfterEach
    void limpiarDatosPrueba() {
        // Elimina el personal de prueba si quedó registrado
        Personal p = dao.login(RUT_PRUEBA, "pass_test");
        if (p != null) {
            dao.delete(p.getIdPersonal());
        }
    }

    // ----------------------------------------------------------
    // TC-DP01: Registrar un personal nuevo (CREATE)
    // ----------------------------------------------------------
    @Test
    @Order(1)
    @DisplayName("TC-DP01: Registrar nuevo personal en la base de datos")
    void testCreate_insertarPersonal() {
        assumeTrue(dbDisponible, "Se omite: BD no disponible");

        Personal nuevo = new Personal();
        nuevo.setNombre("Empleado Test");
        nuevo.setRut(RUT_PRUEBA);
        nuevo.setContrasena("pass_test");
        nuevo.setRol("conductor");

        dao.create(nuevo);

        // Verificar que se guardó con login
        Personal resultado = dao.login(RUT_PRUEBA, "pass_test");
        assertNotNull(resultado, "El personal debería haber sido creado");
        assertEquals("Empleado Test", resultado.getNombre(), "El nombre debe coincidir");
    }

    // ----------------------------------------------------------
    // TC-DP02: Buscar personal por ID
    // ----------------------------------------------------------
    @Test
    @Order(2)
    @DisplayName("TC-DP02: Buscar personal por ID retorna el registro correcto")
    void testBuscarPorId_retornaPersonalCorrecto() {
        assumeTrue(dbDisponible, "Se omite: BD no disponible");

        // Crear personal de prueba
        Personal nuevo = new Personal();
        nuevo.setNombre("Buscar Test");
        nuevo.setRut(RUT_PRUEBA);
        nuevo.setContrasena("pass_test");
        nuevo.setRol("conductor");
        dao.create(nuevo);

        Personal creado = dao.login(RUT_PRUEBA, "pass_test");
        assertNotNull(creado, "Debe haber sido creado primero");

        Personal encontrado = dao.buscarPorId(creado.getIdPersonal());

        assertNotNull(encontrado, "buscarPorId debe retornar un objeto Personal");
        assertEquals(RUT_PRUEBA, encontrado.getRut(), "El RUT debe coincidir");
    }

    // ----------------------------------------------------------
    // TC-DP03: Actualizar datos de un personal (UPDATE)
    // ----------------------------------------------------------
    @Test
    @Order(3)
    @DisplayName("TC-DP03: Actualizar datos de un personal existente")
    void testUpdate_actualizarPersonal() {
        assumeTrue(dbDisponible, "Se omite: BD no disponible");

        Personal nuevo = new Personal();
        nuevo.setNombre("Antes Update");
        nuevo.setRut(RUT_PRUEBA);
        nuevo.setContrasena("pass_test");
        nuevo.setRol("conductor");
        dao.create(nuevo);

        Personal creado = dao.login(RUT_PRUEBA, "pass_test");
        assertNotNull(creado);

        creado.setNombre("Despues Update");
        creado.setRol("jefe_flota");
        dao.update(creado);

        Personal actualizado = dao.buscarPorId(creado.getIdPersonal());
        assertEquals("Despues Update", actualizado.getNombre(), "Nombre debe estar actualizado");
        assertEquals("jefe_flota",     actualizado.getRol(),    "Rol debe estar actualizado");
    }

    // ----------------------------------------------------------
    // TC-DP04: Eliminar un personal (DELETE)
    // ----------------------------------------------------------
    @Test
    @Order(4)
    @DisplayName("TC-DP04: Eliminar un personal de la base de datos")
    void testDelete_eliminarPersonal() {
        assumeTrue(dbDisponible, "Se omite: BD no disponible");

        Personal nuevo = new Personal();
        nuevo.setNombre("Para Eliminar");
        nuevo.setRut(RUT_PRUEBA);
        nuevo.setContrasena("pass_test");
        nuevo.setRol("conductor");
        dao.create(nuevo);

        Personal creado = dao.login(RUT_PRUEBA, "pass_test");
        assertNotNull(creado, "Debe existir antes de eliminar");

        dao.delete(creado.getIdPersonal());

        Personal eliminado = dao.buscarPorId(creado.getIdPersonal());
        assertNull(eliminado, "Después de eliminar, buscarPorId debe retornar null");
    }

    // ----------------------------------------------------------
    // TC-DP05: Login con credenciales correctas
    // ----------------------------------------------------------
    @Test
    @Order(5)
    @DisplayName("TC-DP05: Login exitoso con credenciales correctas")
    void testLogin_credencialesCorrectas() {
        assumeTrue(dbDisponible, "Se omite: BD no disponible");

        // Usa el admin inicial del script SQL (siempre debe existir)
        Personal p = dao.login("11111111-1", "1234");

        assertNotNull(p, "El login con credenciales correctas debe retornar un Personal");
        assertEquals("admin_jefe", p.getRol(), "El rol debe ser admin_jefe");
    }

    // ----------------------------------------------------------
    // TC-DP06: Login con contraseña incorrecta retorna null
    // ----------------------------------------------------------
    @Test
    @Order(6)
    @DisplayName("TC-DP06: Login fallido con contraseña incorrecta retorna null")
    void testLogin_contrasenaIncorrecta() {
        assumeTrue(dbDisponible, "Se omite: BD no disponible");

        Personal p = dao.login("11111111-1", "contrasena_incorrecta");

        assertNull(p, "El login con contraseña incorrecta debe retornar null");
    }

    // ----------------------------------------------------------
    // TC-DP07: Verificar si un RUT ya existe
    // ----------------------------------------------------------
    @Test
    @Order(7)
    @DisplayName("TC-DP07: existeRut() retorna true para RUT existente")
    void testExisteRut_rutExistente() {
        assumeTrue(dbDisponible, "Se omite: BD no disponible");

        // El RUT del admin inicial siempre debe existir
        assertTrue(dao.existeRut("11111111-1"),
            "El RUT 11111111-1 debe existir en la base de datos");
    }

    // ----------------------------------------------------------
    // TC-DP08: existeRut() retorna false para RUT inexistente
    // ----------------------------------------------------------
    @Test
    @Order(8)
    @DisplayName("TC-DP08: existeRut() retorna false para RUT no registrado")
    void testExisteRut_rutInexistente() {
        assumeTrue(dbDisponible, "Se omite: BD no disponible");

        assertFalse(dao.existeRut("00-NOEXISTE"),
            "Un RUT no registrado debe retornar false");
    }

    // ----------------------------------------------------------
    // TC-DP09: listarTodos() no retorna null y contiene al menos un registro
    // ----------------------------------------------------------
    @Test
    @Order(9)
    @DisplayName("TC-DP09: listarTodos() retorna lista no nula con registros")
    void testListarTodos_retornaLista() {
        assumeTrue(dbDisponible, "Se omite: BD no disponible");

        ArrayList<Personal> lista = dao.listarTodos();

        assertNotNull(lista, "La lista no debe ser null");
        assertFalse(lista.isEmpty(), "Debe haber al menos un registro (admin inicial)");
    }
}
