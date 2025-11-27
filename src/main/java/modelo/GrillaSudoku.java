package modelo;

/**
 * Clase que representa una grilla de Sudoku de 9x9
 */
public class GrillaSudoku {
    private static final int TAMANO = 9;
    private static final int TAMANO_CAJA = 3;
    
    private int[][] grilla;
    
    /**
     * Constructor que crea una grilla vacía
     */
    public GrillaSudoku() {
        grilla = new int[TAMANO][TAMANO];
    }
    
    /**
     * Constructor que crea una grilla a partir de una matriz existente
     * @param grilla La matriz de valores
     */
    public GrillaSudoku(int[][] grilla) {
        if (grilla == null || grilla.length != TAMANO || grilla[0].length != TAMANO) {
            throw new IllegalArgumentException("La grilla debe ser de 9x9");
        }
        this.grilla = new int[TAMANO][TAMANO];
        for (int i = 0; i < TAMANO; i++) {
            System.arraycopy(grilla[i], 0, this.grilla[i], 0, TAMANO);
        }
    }
    
    /**
     * Constructor de copia
     * @param otra Otra grilla de Sudoku
     */
    public GrillaSudoku(GrillaSudoku otra) {
        this.grilla = new int[TAMANO][TAMANO];
        for (int i = 0; i < TAMANO; i++) {
            System.arraycopy(otra.grilla[i], 0, this.grilla[i], 0, TAMANO);
        }
    }
    
    /**
     * Obtiene el valor en una posición específica
     * @param fila La fila (0-8)
     * @param columna La columna (0-8)
     * @return El valor en la posición, o 0 si está vacía
     */
    public int obtenerValor(int fila, int columna) {
        if (fila < 0 || fila >= TAMANO || columna < 0 || columna >= TAMANO) {
            throw new IllegalArgumentException("Índices fuera de rango");
        }
        return grilla[fila][columna];
    }
    
    /**
     * Establece un valor en una posición específica
     * @param fila La fila (0-8)
     * @param columna La columna (0-8)
     * @param valor El valor a establecer (0-9, donde 0 significa vacío)
     */
    public void establecerValor(int fila, int columna, int valor) {
        if (fila < 0 || fila >= TAMANO || columna < 0 || columna >= TAMANO) {
            throw new IllegalArgumentException("Índices fuera de rango");
        }
        if (valor < 0 || valor > 9) {
            throw new IllegalArgumentException("El valor debe estar entre 0 y 9");
        }
        grilla[fila][columna] = valor;
    }
    
    /**
     * Verifica si una celda está vacía
     * @param fila La fila
     * @param columna La columna
     * @return true si la celda está vacía, false en caso contrario
     */
    public boolean estaVacio(int fila, int columna) {
        return obtenerValor(fila, columna) == 0;
    }
    
    /**
     * Obtiene el tamaño de la grilla
     * @return El tamaño (siempre 9)
     */
    public int obtenerTamano() {
        return TAMANO;
    }
    
    /**
     * Obtiene el tamaño de las cajas
     * @return El tamaño de las cajas (siempre 3)
     */
    public int obtenerTamanoCaja() {
        return TAMANO_CAJA;
    }
    
    /**
     * Crea una copia profunda de la grilla
     * @return Nueva instancia con los mismos valores
     */
    public GrillaSudoku clonar() {
        return new GrillaSudoku(this);
    }
    
    /**
     * Limpia la grilla (pone todos los valores en 0)
     */
    public void limpiar() {
        for (int i = 0; i < TAMANO; i++) {
            for (int j = 0; j < TAMANO; j++) {
                grilla[i][j] = 0;
            }
        }
    }
    
    /**
     * Cuenta cuántas celdas de la grilla están completas (no vacías)
     * @return El número de celdas completas
     */
    public int contarCeldasCompletas() {
        int contador = 0;
        for (int i = 0; i < TAMANO; i++) {
            for (int j = 0; j < TAMANO; j++) {
                if (!estaVacio(i, j)) {
                    contador++;
                }
            }
        }
        return contador;
    }
    
    @Override
    public String toString() {
        StringBuilder constructor = new StringBuilder();
        for (int i = 0; i < TAMANO; i++) {
            if (i > 0 && i % TAMANO_CAJA == 0) {
                constructor.append("------+-------+------\n");
            }
            for (int j = 0; j < TAMANO; j++) {
                if (j > 0 && j % TAMANO_CAJA == 0) {
                    constructor.append("| ");
                }
                constructor.append(grilla[i][j] == 0 ? "." : grilla[i][j]).append(" ");
            }
            constructor.append("\n");
        }
        return constructor.toString();
    }
}

