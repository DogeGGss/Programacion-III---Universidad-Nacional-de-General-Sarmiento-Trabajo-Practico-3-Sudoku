package modelo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la clase ValidadorSudoku
 */
public class ValidadorSudokuTest {
    private ValidadorSudoku validador;
    private GrillaSudoku grilla;
    
    @BeforeEach
    public void setUp() {
        validador = new ValidadorSudoku();
        grilla = new GrillaSudoku();
    }
    
    @Test
    public void testColocacionValidaEnGrillaVacia() {
        assertTrue(validador.esColocacionValida(grilla, 0, 0, 1));
        assertTrue(validador.esColocacionValida(grilla, 0, 0, 9));
    }
    
    @Test
    public void testColocacionInvalidaMismaFila() {
        grilla.establecerValor(0, 0, 5);
        assertFalse(validador.esColocacionValida(grilla, 0, 1, 5));
        assertTrue(validador.esColocacionValida(grilla, 0, 1, 6));
    }
    
    @Test
    public void testColocacionInvalidaMismaColumna() {
        grilla.establecerValor(0, 0, 5);
        assertFalse(validador.esColocacionValida(grilla, 1, 0, 5));
        assertTrue(validador.esColocacionValida(grilla, 1, 0, 6));
    }
    
    @Test
    public void testColocacionInvalidaMismaCaja() {
        grilla.establecerValor(0, 0, 5);
        assertFalse(validador.esColocacionValida(grilla, 1, 1, 5));
        assertTrue(validador.esColocacionValida(grilla, 1, 1, 6));
    }
    
    @Test
    public void testColocacionValidaCajaDiferente() {
        grilla.establecerValor(0, 0, 5);
        assertTrue(validador.esColocacionValida(grilla, 0, 3, 5));
    }
    
    @Test
    public void testValorInvalido() {
        assertFalse(validador.esColocacionValida(grilla, 0, 0, 0));
        assertFalse(validador.esColocacionValida(grilla, 0, 0, 10));
    }
    
    @Test
    public void testGrillaValida() {
        // Grilla vacía es válida
        assertTrue(validador.esGrillaValida(grilla));
        
        // Agregar valores válidos
        grilla.establecerValor(0, 0, 1);
        grilla.establecerValor(0, 1, 2);
        assertTrue(validador.esGrillaValida(grilla));
    }
    
    @Test
    public void testGrillaInvalidaDuplicadoEnFila() {
        grilla.establecerValor(0, 0, 5);
        grilla.establecerValor(0, 1, 5);
        assertFalse(validador.esGrillaValida(grilla));
        assertTrue(validador.tieneConflictos(grilla));
    }
    
    @Test
    public void testGrillaInvalidaDuplicadoEnColumna() {
        grilla.establecerValor(0, 0, 5);
        grilla.establecerValor(1, 0, 5);
        assertFalse(validador.esGrillaValida(grilla));
        assertTrue(validador.tieneConflictos(grilla));
    }
    
    @Test
    public void testGrillaInvalidaDuplicadoEnCaja() {
        grilla.establecerValor(0, 0, 5);
        grilla.establecerValor(1, 1, 5);
        assertFalse(validador.esGrillaValida(grilla));
        assertTrue(validador.tieneConflictos(grilla));
    }
}

