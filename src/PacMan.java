import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;

public class PacMan extends JPanel implements ActionListener, KeyListener {
    class Block {
        int x;
        int y;
        int height;
        int width;
        Image image;

        int startX;
        int startY;
        char dir = 'U';
        int velX = 0;
        int velY = 0;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char dir) {
            char prevDir = this.dir;
            this.dir = dir;
            updateVelocity();
            this.x += this.velX;
            this.y += this.velY;
            for (Block wall: walls) {
                if (collision(this, wall)) {
                    this.x -= this.velX;
                    this.y -= this.velY;
                    this.dir = prevDir;
                    updateVelocity();
                }
            }
        }
        void updateVelocity() {
            if (this.dir == 'U') {
                this.velX = 0;
                this.velY = -tileSize/4;
            } else if (this.dir == 'D') {
                this.velX = 0;
                this.velY = tileSize/4;
            } else if (this.dir == 'L') {
                this.velY = 0;
                this.velX = -tileSize/4;
            } else if (this.dir == 'R') {
                this.velY = 0;
                this.velX = tileSize/4;
            }
        }
        
        void reset() {
            this.x = startX;
            this.y = startY;
        }
    }
    private int rowCount = 21;
    private int colCount = 19;
    private int tileSize = 32;
    private int boardWidth = colCount * tileSize;
    private int boardHeight = rowCount * tileSize;

    private Image wallImage;
    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image pinkGhostImage;
    private Image redGhostImage;

    private Image pacmanRightImage;
    private Image pacmanLeftImage;
    private Image pacmanUpImage;
    private Image pacmanDownImage;

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;

    Timer gameLoop;
    char[] dirs = {'U','D','L','R'};
    Random random = new Random();

    private String[] tileMap = {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X                 X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXrXX X XXXX",
        "O       bpo       O",
        "XXXX X XXXXX X XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXXXX X XXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X  X     P     X  X",
        "XX X X XXXXX X X XX",
        "X    X   X   X    X",
        "X XXXXXX X XXXXXX X",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX" 
    };


    int score = 0;
    int lives = 3;
    boolean gameOver = false;

    PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        //load images
        wallImage = new ImageIcon(getClass().getResource("./images/wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./images/blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./images/orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./images/pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./images/redGhost.png")).getImage();

        pacmanUpImage = new ImageIcon(getClass().getResource("./images/pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./images/pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./images/pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./images/pacmanRight.png")).getImage();

        loadMap();

        for(Block ghost: ghosts) {
            char newDir = dirs[random.nextInt(4)];
            ghost.updateDirection(newDir);
        }

        gameLoop = new Timer(50, this);
        gameLoop.start();
    }

    public void loadMap() {
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();
        
        for(int r = 0; r < rowCount; r++) {
            for(int c = 0; c < colCount; c++) {
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);

                int x = c * tileSize;
                int y = r * tileSize;

                if (tileMapChar == 'X') { // wall
                    Block wall = new Block(wallImage, x, y, tileSize, tileSize);
                    walls.add(wall);
                } else if (tileMapChar == 'b') { // blue ghost
                    Block ghost = new Block(blueGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }else if (tileMapChar == 'o') { // orange ghost
                    Block ghost = new Block(orangeGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }else if (tileMapChar == 'p') { // pink ghost
                    Block ghost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }else if (tileMapChar == 'r') { // red ghost
                    Block ghost = new Block(redGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                } else if (tileMapChar == 'P') { // blue ghost
                    pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                } else if (tileMapChar == ' ') { // food block
                    Block food = new Block(null, x + 14, y + 14, 4, 4);
                    foods.add(food);
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        for (Block ghost: ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        for (Block wall: walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

        g.setColor(Color.white);
        for (Block food: foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }

        g.setFont(new Font("Arial",Font.PLAIN, 18));
        if(gameOver) {
            g.drawString("Game Over: " + String.valueOf(score), tileSize/2, tileSize/2);
        } else {
            g.drawString("x " + String.valueOf(lives) + " Score: " + String.valueOf(score), tileSize/2, tileSize/2);
        }
    }

    public void move() {
        pacman.x += pacman.velX;
        pacman.y += pacman.velY;

        for(Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velX;
                pacman.y -= pacman.velY;
                break;
            }
        }

        for(Block ghost: ghosts) {
            if(collision(ghost, pacman)) {
                --lives;

                if (lives == 0) {
                   gameOver = true;
                   return; 
                }
                resetPositions();
            }
        }

        for(Block ghost: ghosts) {
            if (ghost.y == tileSize*9 && ghost.dir != 'U' && ghost.dir != 'D') {
                ghost.updateDirection('U');
            }
            ghost.x += ghost.velX;
            ghost.y += ghost.velY;

            for(Block wall : walls) {
                if (collision(ghost, wall) || ghost.x <= 0 || ghost.x + ghost.width >= boardWidth) {
                    ghost.x -= ghost.velX;
                    ghost.y -= ghost.velY;
                    char newDir = dirs[random.nextInt(4)];
                    ghost.updateDirection(newDir);
                }
            }
        }

        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(pacman, food)) {
                foodEaten = food;
                score += 10;
            }
        }
        foods.remove(foodEaten);

        if (foods.isEmpty()) {
            loadMap();
            resetPositions();
        }
    }

    public boolean collision(Block a, Block b) {
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
    }

    public void resetPositions() {
        pacman.reset();
        pacman.velX = 0;
        pacman.velY = 0;

        for(Block ghost: ghosts) {
            ghost.reset();
            char newDir = dirs[random.nextInt(4)];
            ghost.updateDirection(newDir);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(gameOver) {
           gameLoop.stop(); 
        }
        move();
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {

        if(gameOver) {
            loadMap();
            resetPositions();
            lives = 3;
            score = 0;
            gameOver = false;
            gameLoop.start();
        }

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            pacman.updateDirection('U');
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            pacman.updateDirection('D');
        }  else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            pacman.updateDirection('L');
        }  else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacman.updateDirection('R');
        }
     
        if (pacman.dir == 'U') {
            pacman.image = pacmanUpImage;
        }
        else if (pacman.dir == 'D') {
            pacman.image = pacmanDownImage;
        }
        else if (pacman.dir == 'L') {
            pacman.image = pacmanLeftImage;
        }
        else if (pacman.dir == 'R') {
            pacman.image = pacmanRightImage;
        }
    }
}
