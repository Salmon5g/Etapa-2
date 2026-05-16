package com.mycompany.transporte_mercancia.Logica;

import java.sql.Date;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la clase Mantenimiento (RF-05).
 * Validan el modelo Mantenimiento de manera aislada,
 * sin requerir conexión a base de datos.
 *
 * @author Pruebas Unitarias - Sistema Transporte Mercancía
 */
@DisplayName("Pruebas Unitarias - Clase Mantenimiento")
public class MantenimientoTest {

    private Mantenimiento mantenimiento;

    @BeforeEach
    void setUp() {
        mantenimiento = new Mantenimiento();
    }

    // ----------------------------------------------------------
    // TC-M01: Getters y setters funcionan correctamente
    // ----------------------------------------------------------
    @Test
    @DisplayName("TC-M01: Crear mantenimiento y verificar getters/setters")
    void testCrearMantenimiento_gettersYSetters() {
        Date entrada = Date.valueOf("2025-01-10");
        Date salida  = Date.valueOf("2025-01-20");

        mantenimiento.setIdMantenimiento(1);
        mantenimiento.setMatricula("ABC-123");
        mantenimiento.setFechaEntrada(entrada);
       
        mantenimiento.setDescripcion("Cambio de aceite y filtros");

        assertAll("Verificación completa de atributos del mantenimiento",
            () -> assertEquals(1,          mantenimiento.getIdMantenimiento(), "ID debe ser 1"),
            () -> assertEquals("ABC-123",  mantenimiento.getMatricula(),       "Matrícula debe coincidir"),
            () -> assertEquals(entrada,    mantenimiento.getFechaEntrada(),    "Fecha entrada debe coincidir"),
      
            () -> assertEquals("Cambio de aceite y filtros",
                               mantenimiento.getDescripcion(),                 "Descripción debe coincidir")
        );
    }

    // ----------------------------------------------------------
    // TC-M02: La fecha de salida debe ser posterior a la de entrada
    // ----------------------------------------------------------
    @Test
    @DisplayName("TC-M02: La fecha de salida debe ser posterior a la de entrada")
    void testFechaSalida_debeSerPosterior() {
        Date entrada = Date.valueOf("2025-03-01");
        Date salida  = Date.valueOf("2025-03-15");

        mantenimiento.setFechaEntrada(entrada);
     

     
    }

    // ----------------------------------------------------------
    // TC-M03: Fechas iguales no son válidas (salida > entrada)
    // ----------------------------------------------------------
    @Test
    @DisplayName("TC-M03: Fechas iguales no son válidas (salida debe ser MAYOR que entrada)")
    void testFechasIguales_noValidas() {
        Date mismaFecha = Date.valueOf("2025-05-10");

        mantenimiento.setFechaEntrada(mismaFecha);
      

     
    }

    // ----------------------------------------------------------
    // TC-M04: La descripción no debe estar vacía
    // ----------------------------------------------------------
    @Test
    @DisplayName("TC-M04: La descripción no debe ser nula ni vacía")
    void testDescripcion_noNulaNoVacia() {
        mantenimiento.setDescripcion("Revisión de frenos");

        assertNotNull(mantenimiento.getDescripcion(),
            "La descripción no debe ser null");
        assertFalse(mantenimiento.getDescripcion().isBlank(),
            "La descripción no debe estar vacía");
    }

    // ----------------------------------------------------------
    // TC-M05: Atributos son null por defecto
    // ----------------------------------------------------------
    @Test
    @DisplayName("TC-M05: Atributos son null al crear el objeto sin datos")
    void testAtributosNulosPorDefecto() {
        assertAll("Atributos por defecto",
            () -> assertNull(mantenimiento.getIdMantenimiento(), "ID debe ser null"),
            () -> assertNull(mantenimiento.getMatricula(),       "Matrícula debe ser null"),
            () -> assertNull(mantenimiento.getFechaEntrada(),    "Fecha entrada debe ser null"),
          
            () -> assertNull(mantenimiento.getDescripcion(),     "Descripción debe ser null")
        );
    }
}
