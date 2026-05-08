package com.mycompany.transporte_mercancia.Logica;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la clase Personal (RF-05).
 * Validan el correcto funcionamiento del modelo Personal de manera aislada,
 * sin requerir conexión a base de datos.
 *
 * @author Pruebas Unitarias - Sistema Transporte Mercancía
 */
@DisplayName("Pruebas Unitarias - Clase Personal")
public class PersonalTest {

    private Personal personal;

    @BeforeEach
    void setUp() {
        personal = new Personal();
    }

    // ----------------------------------------------------------
    // TC-P01: Getters y setters funcionan correctamente
    // ----------------------------------------------------------
    @Test
    @DisplayName("TC-P01: Crear personal y verificar getters/setters")
    void testCrearPersonal_gettersYSetters() {
        personal.setIdPersonal(10);
        personal.setNombre("María González");
        personal.setRut("98765432-1");
        personal.setContrasena("clave123");
        personal.setRol("conductor");

        assertAll("Verificación completa de atributos del personal",
            () -> assertEquals(10,               personal.getIdPersonal(),  "ID debe ser 10"),
            () -> assertEquals("María González", personal.getNombre(),       "Nombre debe coincidir"),
            () -> assertEquals("98765432-1",     personal.getRut(),          "RUT debe coincidir"),
            () -> assertEquals("clave123",       personal.getContrasena(),   "Contraseña debe coincidir"),
            () -> assertEquals("conductor",      personal.getRol(),          "Rol debe ser conductor")
        );
    }

    // ----------------------------------------------------------
    // TC-P02: toString() retorna el nombre del personal
    // ----------------------------------------------------------
    @Test
    @DisplayName("TC-P02: toString() debe retornar el nombre")
    void testToString_retornaNombre() {
        personal.setNombre("Carlos Soto");
        assertEquals("Carlos Soto", personal.toString(),
            "toString() debe retornar el nombre del personal");
    }

    // ----------------------------------------------------------
    // TC-P03: Los roles válidos del sistema pueden asignarse
    // ----------------------------------------------------------
    @Test
    @DisplayName("TC-P03: Verificar asignación de roles válidos del sistema")
    void testRoles_asignacionValida() {
        String[] rolesValidos = {"jefe_flota", "admin_mantenimiento", "admin_jefe", "conductor"};

        for (String rol : rolesValidos) {
            personal.setRol(rol);
            assertEquals(rol, personal.getRol(),
                "Debe poder asignarse el rol: " + rol);
        }
    }

    // ----------------------------------------------------------
    // TC-P04: Nombre nulo por defecto (objeto recién creado)
    // ----------------------------------------------------------
    @Test
    @DisplayName("TC-P04: Atributos son null por defecto al crear el objeto")
    void testAtributosNulosPorDefecto() {
        assertAll("Todos los campos deben ser null al inicializar",
            () -> assertNull(personal.getIdPersonal(),  "ID debe ser null por defecto"),
            () -> assertNull(personal.getNombre(),      "Nombre debe ser null por defecto"),
            () -> assertNull(personal.getRut(),         "RUT debe ser null por defecto"),
            () -> assertNull(personal.getContrasena(),  "Contraseña debe ser null por defecto"),
            () -> assertNull(personal.getRol(),         "Rol debe ser null por defecto")
        );
    }

    // ----------------------------------------------------------
    // TC-P05: Modificar atributos de un personal existente
    // ----------------------------------------------------------
    @Test
    @DisplayName("TC-P05: Modificar atributos de un personal ya creado")
    void testModificarAtributos() {
        personal.setNombre("Ana Rojas");
        personal.setRol("conductor");

        // Se modifican los datos
        personal.setNombre("Ana Rojas Actualizada");
        personal.setRol("jefe_flota");

        assertEquals("Ana Rojas Actualizada", personal.getNombre(),
            "El nombre debe haber sido actualizado");
        assertEquals("jefe_flota", personal.getRol(),
            "El rol debe haber sido actualizado");
    }
}
