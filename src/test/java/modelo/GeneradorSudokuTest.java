package modelo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la clase GeneradorSudoku
 */
public class GeneradorSudokuTest {
    private GeneradorSudoku generador;
    private ValidadorSudoku validador;
    private ResolvedorSudoku resolvedor;
    
    @BeforeEach
    public void setUp() {
        generador = new GeneradorSudoku();
        validador = new ValidadorSudoku();
        resolvedor = new ResolvedorSudoku(validador);
    }
    
    @Test
    public void testGenerarSudokuResuelto() {
        GrillaSudoku grilla = generador.generarSudokuResuelto();
        
        assertNotNull(grilla);
        assertEquals(81, grilla.contarCeldasCompletas());
        assertTrue(validador.esGrillaValida(grilla));
    }
    
    @Test
    public void testGenerarSudokuConCantidadPrefijados() {
        int cantidadPrefijados = 30;
        GrillaSudoku grilla = generador.generarSudoku(cantidadPrefijados);
        
        assertNotNull(grilla);
        assertEquals(cantidadPrefijados, grilla.contarCeldasCompletas());
        assertTrue(validador.esGrillaValida(grilla));
    }
    
    @Test
    public void testGenerarSudokuCantidadPrefijadosInvalida() {
        assertThrows(IllegalArgumentException.class, () -> generador.generarSudoku(10));
        assertThrows(IllegalArgumentException.class, () -> generador.generarSudoku(100));
    }
    
    @Test
    public void testGeneradoSudokuEsSolucionable() {
        GrillaSudoku grilla = generador.generarSudoku(25);
        
        // Debería ser solucionable
        GrillaSudoku grillaTrabajo = new GrillaSudoku(grilla);
        assertTrue(resolvedor.resolver(grillaTrabajo));
    }
    
    @Test
    public void testGenerarSudokuDiferentesCantidades() {
        for (int cantidad = 17; cantidad <= 40; cantidad += 5) {
            GrillaSudoku grilla = generador.generarSudoku(cantidad);
            assertEquals(cantidad, grilla.contarCeldasCompletas());
            assertTrue(validador.esGrillaValida(grilla));
        }
    }
}

