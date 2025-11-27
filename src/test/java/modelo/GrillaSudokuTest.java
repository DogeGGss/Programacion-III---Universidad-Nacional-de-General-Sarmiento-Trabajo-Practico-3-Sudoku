package modelo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la clase GrillaSudoku
 */
public class GrillaSudokuTest {
    private GrillaSudoku grilla;
    
    @BeforeEach
    public void setUp() {
        grilla = new GrillaSudoku();
    }
    
    @Test
    public void testCrearGrillaVacia() {
        assertEquals(9, grilla.obtenerTamano());
        assertEquals(0, grilla.contarCeldasCompletas());
        assertTrue(grilla.estaVacio(0, 0));
    }
    
    @Test
    public void testEstablecerYObtenerValor() {
        grilla.establecerValor(0, 0, 5);
        assertEquals(5, grilla.obtenerValor(0, 0));
        assertFalse(grilla.estaVacio(0, 0));
    }
    
    @Test
    public void testEstablecerValorACero() {
        grilla.establecerValor(0, 0, 5);
        grilla.establecerValor(0, 0, 0);
        assertEquals(0, grilla.obtenerValor(0, 0));
        assertTrue(grilla.estaVacio(0, 0));
    }
    
    @Test
    public void testIndicesInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> grilla.obtenerValor(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> grilla.obtenerValor(0, -1));
        assertThrows(IllegalArgumentException.class, () -> grilla.obtenerValor(9, 0));
        assertThrows(IllegalArgumentException.class, () -> grilla.obtenerValor(0, 9));
    }
    
    @Test
    public void testValorInvalido() {
        assertThrows(IllegalArgumentException.class, () -> grilla.establecerValor(0, 0, -1));
        assertThrows(IllegalArgumentException.class, () -> grilla.establecerValor(0, 0, 10));
    }
    
    @Test
    public void testConstructorCopia() {
        grilla.establecerValor(0, 0, 5);
        grilla.establecerValor(1, 1, 7);
        
        GrillaSudoku copia = new GrillaSudoku(grilla);
        assertEquals(5, copia.obtenerValor(0, 0));
        assertEquals(7, copia.obtenerValor(1, 1));
        
        // Modificar la copia no debe afectar el original
        copia.establecerValor(0, 0, 9);
        assertEquals(5, grilla.obtenerValor(0, 0));
        assertEquals(9, copia.obtenerValor(0, 0));
    }
    
    @Test
    public void testObtenerCantidadPrefijados() {
        assertEquals(0, grilla.contarCeldasCompletas());
        grilla.establecerValor(0, 0, 1);
        assertEquals(1, grilla.contarCeldasCompletas());
        grilla.establecerValor(0, 1, 2);
        assertEquals(2, grilla.contarCeldasCompletas());
    }
    
    @Test
    public void testLimpiar() {
        grilla.establecerValor(0, 0, 5);
        grilla.establecerValor(1, 1, 7);
        grilla.limpiar();
        assertEquals(0, grilla.contarCeldasCompletas());
        assertTrue(grilla.estaVacio(0, 0));
        assertTrue(grilla.estaVacio(1, 1));
    }
}

