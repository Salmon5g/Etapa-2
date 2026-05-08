package com.mycompany.transporte_mercancia.Logica;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la clase Camion (RF-05).
 * Validan el correcto funcionamiento de getters, setters y la lógica del modelo
 * de manera aislada, sin requerir conexión a base de datos.
 *
 * @author Pruebas Unitarias - Sistema Transporte Mercancía
 */
@DisplayName("Pruebas Unitarias - Clase Camion")
public class CamionTest {

    private Camion camion;

    @BeforeEach
    void setUp() {
        // Se crea un objeto Camion limpio antes de cada prueba
        camion = new Camion();
    }

    // ----------------------------------------------------------
    // TC-C01: Verificar que getters y setters funcionan correctamente
    // ----------------------------------------------------------
    @Test
    @DisplayName("TC-C01: Crear camión y verificar getters/setters")
    void testCrearCamion_gettersYSetters() {
        camion.setIdCamion(1);
        camion.setMarca("Volvo");
        camion.setModelo("FH16");
        camion.setAnio(2020);
        camion.setKilometrajeTotal(15000);
        camion.setMatricula("ABC-123");

        assertAll("Verificación completa de atributos del camión",
            () -> assertEquals(1,       camion.getIdCamion(),         "ID debe ser 1"),
            () -> assertEquals("Volvo", camion.getMarca(),            "Marca debe ser Volvo"),
            () -> assertEquals("FH16",  camion.getModelo(),           "Modelo debe ser FH16"),
            () -> assertEquals(2020,    camion.getAnio(),             "Año debe ser 2020"),
            () -> assertEquals(15000,   camion.getKilometrajeTotal(), "Kilometraje debe ser 15000"),
            () -> assertEquals("ABC-123", camion.getMatricula(),      "Matrícula debe ser ABC-123")
        );
    }

    // ----------------------------------------------------------
    // TC-C02: toString() debe retornar la matrícula del camión
    // ----------------------------------------------------------
    @Test
    @DisplayName("TC-C02: toString() debe retornar la matrícula")
    void testToString_retornaMatricula() {
        camion.setMatricula("XYZ-999");
        assertEquals("XYZ-999", camion.toString(),
            "toString() debería retornar la matrícula del camión");
    }

    // ----------------------------------------------------------
    // TC-C03: Se puede asignar un conductor (Personal) al camión
    // ----------------------------------------------------------
    @Test
    @DisplayName("TC-C03: Asignar conductor al camión")
    void testAsignarConductor_exitoso() {
        Personal conductor = new Personal();
        conductor.setIdPersonal(5);
        conductor.setNombre("Juan Pérez");
        conductor.setRol("conductor");

        camion.setConductor(conductor);

        assertNotNull(camion.getConductor(), "El conductor no debe ser null");
        assertEquals(5,           camion.getConductor().getIdPersonal(), "ID conductor debe ser 5");
        assertEquals("Juan Pérez", camion.getConductor().getNombre(),     "Nombre debe ser Juan Pérez");
    }

    // ----------------------------------------------------------
    // TC-C04: Conductor es null por defecto (sin asignar)
    // ----------------------------------------------------------
    @Test
    @DisplayName("TC-C04: Conductor es null cuando no se asigna")
    void testConductorNuloPorDefecto() {
        assertNull(camion.getConductor(),
            "Sin asignar conductor, el campo debe ser null");
    }

    // ----------------------------------------------------------
    // TC-C05: El kilometraje no debería ser negativo
    // ----------------------------------------------------------
    @Test
    @DisplayName("TC-C05: Kilometraje debe ser mayor o igual a cero")
    void testKilometraje_debeSerNoNegativo() {
        camion.setKilometrajeTotal(0);
        assertTrue(camion.getKilometrajeTotal() >= 0,
            "El kilometraje total no puede ser un valor negativo");

        camion.setKilometrajeTotal(50000);
        assertTrue(camion.getKilometrajeTotal() >= 0,
            "Con 50000 km el valor sigue siendo válido");
    }
}
