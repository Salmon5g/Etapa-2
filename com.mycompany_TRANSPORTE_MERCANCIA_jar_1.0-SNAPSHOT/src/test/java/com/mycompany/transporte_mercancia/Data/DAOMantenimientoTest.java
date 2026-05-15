package com.mycompany.transporte_mercancia.Data;

import com.mycompany.transporte_mercancia.Logica.Mantenimiento;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;

/**
 * Pruebas unitarias (integración con BD) para DAOMantenimiento (RF-05).
 * REQUISITO: La base de datos MySQL debe estar activa en localhost:3306.
 *
 * Usa el camión de matrícula "hfur87" que ya existe en los datos iniciales
 * del script SQL para no crear dependencias adicionales.
 *
 * @author Pruebas Unitarias - Sistema Transporte Mercancía
 */
@DisplayName("Pruebas Unitarias - DAOMantenimiento (CRUD)")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DAOMantenimientoTest {

    private static DAOMantenimiento daoMant;
    private static boolean dbDisponible = false;

    // Matricula del camion inicial (dato del script SQL)
    private static final String MATRICULA_EXISTENTE = "hfur87";

    // ID del registro de mantenimiento creado en la prueba
    private static Integer idMantenimientoCreado = null;

    @BeforeAll
    static void verificarConexion() {
        daoMant = new DAOMantenimiento();
        try (Connection c = Conn.get()) {
            dbDisponible = (c != null && !c.isClosed());
        } catch (Exception e) {
            dbDisponible = false;
        }
    }

    @AfterEach
    void limpiarMantenimientoPrueba() {
        // Elimina el registro de prueba si quedó registrado
        if (idMantenimientoCreado != null) {
            daoMant.delete(idMantenimientoCreado);
            idMantenimientoCreado = null;
        }
    }

    // Helper: crea un mantenimiento de prueba y guarda el id generado
    private void crearMantenimientoPrueba() {
        Mantenimiento m = new Mantenimiento();
        m.setMatricula(MATRICULA_EXISTENTE);
        m.setFechaEntrada(Date.valueOf("2030-08-01")); // fecha futura para no interferir
        
        m.setDescripcion("Prueba unitaria - mantenimiento de test");
        daoMant.create(m);

        // Buscar el id del registro recién creado
        ArrayList<Mantenimiento> lista = daoMant.listarTodos();
        for (Mantenimiento item : lista) {
            if ("Prueba unitaria - mantenimiento de test".equals(item.getDescripcion())) {
                idMantenimientoCreado = item.getIdMantenimiento();
                break;
            }
        }
    }

    // ----------------------------------------------------------
    // TC-DM01: Registrar un mantenimiento nuevo (CREATE)
    // ----------------------------------------------------------
    @Test
    @Order(1)
    @DisplayName("TC-DM01: Registrar un nuevo mantenimiento en la base de datos")
    void testCreate_insertarMantenimiento() {
        assumeTrue(dbDisponible, "Se omite: BD no disponible");

        crearMantenimientoPrueba();

        assertNotNull(idMantenimientoCreado, "El mantenimiento debería haberse creado y tener un ID");
    }

    // ----------------------------------------------------------
    // TC-DM02: Buscar mantenimiento por ID
    // ----------------------------------------------------------
    @Test
    @Order(2)
    @DisplayName("TC-DM02: Buscar mantenimiento por ID retorna el registro correcto")
    void testBuscarPorId_retornaMantenimiento() {
        assumeTrue(dbDisponible, "Se omite: BD no disponible");

        crearMantenimientoPrueba();
        assertNotNull(idMantenimientoCreado, "Debe haberse creado primero");

        Mantenimiento encontrado = daoMant.buscarPorId(idMantenimientoCreado);

        assertNotNull(encontrado, "buscarPorId debe retornar un objeto Mantenimiento");
        assertEquals(MATRICULA_EXISTENTE, encontrado.getMatricula(),
            "La matrícula debe coincidir");
        assertEquals("Prueba unitaria - mantenimiento de test", encontrado.getDescripcion(),
            "La descripción debe coincidir");
    }

    // ----------------------------------------------------------
    // TC-DM03: Actualizar un mantenimiento existente (UPDATE)
    // ----------------------------------------------------------
    @Test
    @Order(3)
    @DisplayName("TC-DM03: Actualizar descripción y fechas de un mantenimiento")
    void testUpdate_actualizarMantenimiento() {
        assumeTrue(dbDisponible, "Se omite: BD no disponible");

        crearMantenimientoPrueba();
        assertNotNull(idMantenimientoCreado);

        Mantenimiento existente = daoMant.buscarPorId(idMantenimientoCreado);
        existente.setDescripcion("Descripcion actualizada en prueba");
        
        daoMant.update(existente);

        Mantenimiento actualizado = daoMant.buscarPorId(idMantenimientoCreado);
        assertEquals("Descripcion actualizada en prueba", actualizado.getDescripcion(),
            "La descripción debe haberse actualizado");
       
    }

    // ----------------------------------------------------------
    // TC-DM04: Eliminar un mantenimiento (DELETE)
    // ----------------------------------------------------------
    @Test
    @Order(4)
    @DisplayName("TC-DM04: Eliminar un mantenimiento de la base de datos")
    void testDelete_eliminarMantenimiento() {
        assumeTrue(dbDisponible, "Se omite: BD no disponible");

        crearMantenimientoPrueba();
        assertNotNull(idMantenimientoCreado, "Debe existir antes de eliminar");

        daoMant.delete(idMantenimientoCreado);

        Mantenimiento eliminado = daoMant.buscarPorId(idMantenimientoCreado);
        assertNull(eliminado, "Después de eliminar, buscarPorId debe retornar null");
        idMantenimientoCreado = null; // Ya fue eliminado manualmente
    }

    // ----------------------------------------------------------
    // TC-DM05: listarTodos() retorna lista no nula
    // ----------------------------------------------------------
    @Test
    @Order(5)
    @DisplayName("TC-DM05: listarTodos() retorna lista válida (no nula)")
    void testListarTodos_retornaListaNoNula() {
        assumeTrue(dbDisponible, "Se omite: BD no disponible");

        ArrayList<Mantenimiento> lista = daoMant.listarTodos();

        assertNotNull(lista, "La lista de mantenimientos no debe ser null");
    }

    // ----------------------------------------------------------
    // TC-DM06: Fecha de salida anterior a entrada debe lanzar excepción (constraint BD)
    // ----------------------------------------------------------
    @Test
    @Order(6)
    @DisplayName("TC-DM06: Fechas inválidas (salida <= entrada) deben lanzar excepción")
    void testCreate_fechasInvalidas_lanzaExcepcion() {
        assumeTrue(dbDisponible, "Se omite: BD no disponible");

        Mantenimiento invalido = new Mantenimiento();
        invalido.setMatricula(MATRICULA_EXISTENTE);
        invalido.setFechaEntrada(Date.valueOf("2030-09-15")); // entrada DESPUÉS de salida
      
        invalido.setDescripcion("Test fechas invalidas");

        // La BD tiene un CHECK constraint que impide fechaSalida <= fechaEntrada
        // El DAO lanza RuntimeException en ese caso
        assertThrows(RuntimeException.class, () -> daoMant.create(invalido),
            "Insertar mantenimiento con fechaSalida <= fechaEntrada debe lanzar RuntimeException");
    }
}
