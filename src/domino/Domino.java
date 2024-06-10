package domino;
import javax.swing.SwingUtilities;

public class Domino {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DominoGUI::new);
    }
}
