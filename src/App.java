import javax.swing.JFrame;


public class App {
    public static void main(String[] args) throws Exception {
        int rowCount = 21;
        int columnCount = 19;
        int tileSize = 32;
        int boardWidth = columnCount * tileSize;
        int boardHeight = rowCount * tileSize;

        JFrame frame = new JFrame("Pacman Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(boardWidth, boardHeight);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        // frame.setVisible(true);

        PacMan pacman = new PacMan();
        frame.add(pacman);
        frame.pack();
        pacman.requestFocus();
        frame.setVisible(true);
    }
}
