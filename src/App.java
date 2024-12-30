import javax.swing.JFrame;

public class App {
    public static void main(String[] args) throws Exception {

        int rowCount = 21;
        int colCount = 21;
        int tileSize = 32;
        int boardWidth = colCount * tileSize;
        int boardHeight = rowCount * tileSize;

        JFrame window = new JFrame("PacMan!");
        window.setSize(boardWidth, boardHeight);
        window.setLocationRelativeTo(null); // sets window at center on launch
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        PacMan pacmanGame = new PacMan();
        window.add(pacmanGame);
        window.pack();
        pacmanGame.requestFocus();
        window.setVisible(true);
    }
}
