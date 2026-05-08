package com.mycompany.transporte_mercancia.Data;

import com.mycompany.transporte_mercancia.Logica.Camion;
import com.mycompany.transporte_mercancia.Logica.Personal;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * Pruebas unitarias (integración con BD) para DAOCamion (RF-05). REQUISITO: La
 * base de datos MySQL debe estar activa en localhost:3306.
 *
 * @author Pruebas Unitarias - Sistema Transporte Mercancía
 */
@DisplayName("Pruebas Unitarias - DAOCamion (CRUD)")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DAOCamionTest {

    private static DAOCamion daoCamion;
    private static DAOPersonal daoPersonal;
    private static boolean dbDisponible = false;
    private static Personal conductorPrueba = null;
    private static boolean conductorCreadoPorTest = false;

    private static final String RUT_PRUEBA = "99.999.999-9";

    // ----------------------------------------------------------
    // SETUP GLOBAL
    // ----------------------------------------------------------
    @BeforeAll
    static void prepararEntorno() {
        daoCamion = new DAOCamion();
        daoPersonal = new DAOPersonal();

        // 1) Verificar conexión
        try (Connection c = Conn.get()) {
            dbDisponible = (c != null && !c.isClosed());
        } catch (Exception e) {
            dbDisponible = false;
        }

        if (!dbDisponible) {
            return;
        }

        // 2) Intentar usar un conductor ya existente en la BD
        ArrayList<Personal> lista = daoPersonal.listarTodos();
        if (lista != null && !lista.isEmpty()) {
            conductorPrueba = lista.get(0);
            conductorCreadoPorTest = false;
            return;
        }

        // 3) Si la tabla personal está vacía, crear uno propio
        if (daoPersonal.existeRut(RUT_PRUEBA)) {
            for (Personal p : daoPersonal.listarTodos()) {
                if (RUT_PRUEBA.equals(p.getRut())) {
                    conductorPrueba = p;
                    conductorCreadoPorTest = false;
                    return;
                }
            }
        }

        Personal nuevo = new Personal();
        nuevo.setNombre("Conductor Test");
        nuevo.setRut(RUT_PRUEBA);
        nuevo.setContrasena("test1234");
        nuevo.setRol("conductor");
        daoPersonal.create(nuevo);

        ArrayList<Personal> actualizada = daoPersonal.listarTodos();
        if (actualizada != null) {
            for (Personal p : actualizada) {
                if (RUT_PRUEBA.equals(p.getRut())) {
                    conductorPrueba = p;
                    conductorCreadoPorTest = true;
                    break;
                }
            }
        }
    }

    // ----------------------------------------------------------
    // TEARDOWN GLOBAL — limpia el conductor solo si lo creamos nosotros
    // ----------------------------------------------------------
    @AfterAll
    static void limpiarEntorno() {
        if (conductorCreadoPorTest && conductorPrueba != null) {
            daoPersonal.delete(conductorPrueba.getIdPersonal());
        }
    }

    // ----------------------------------------------------------
    // TC-DC05: existeMatricula() retorna true para matrícula existente
    // ----------------------------------------------------------
    @Test
    @Order(5)
    @DisplayName("TC-DC05: existeMatricula() retorna true para matrícula ya registrada")
    void testExisteMatricula_matriculaExistente() {
        assumeTrue(dbDisponible, "Se omite: BD no disponible");

        assertTrue(daoCamion.existeMatricula("hfur87"),
                "La matrícula 'hfur87' debe existir en la BD");
    }

    // ----------------------------------------------------------
    // TC-DC06: existeMatricula() retorna false para matrícula inexistente
    // ----------------------------------------------------------
    @Test
    @Order(6)
    @DisplayName("TC-DC06: existeMatricula() retorna false para matrícula no registrada")
    void testExisteMatricula_matriculaInexistente() {
        assumeTrue(dbDisponible, "Se omite: BD no disponible");

        assertFalse(daoCamion.existeMatricula("NO-EXISTE-999"),
                "Una matrícula no registrada debe retornar false");
    }

    // ----------------------------------------------------------
    // TC-DC07: listarTodos() retorna lista no nula
    // ----------------------------------------------------------
    @Test
    @Order(7)
    @DisplayName("TC-DC07: listarTodos() retorna una lista válida (no nula)")
    void testListarTodos_retornaListaNoNula() {
        assumeTrue(dbDisponible, "Se omite: BD no disponible");

        ArrayList<Camion> lista = daoCamion.listarTodos();

        assertNotNull(lista, "La lista de camiones no debe ser null");
    }
}
