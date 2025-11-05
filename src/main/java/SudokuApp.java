
import vista.VistaSudoku;
import controlador.ControladorSudoku;
import javax.swing.SwingUtilities;

/**
 * Clase principal de la aplicación Sudoku Solver
 */
public class SudokuApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VistaSudoku vista = new VistaSudoku();
            new ControladorSudoku(vista);
            vista.setVisible(true);
        });
    }
}

