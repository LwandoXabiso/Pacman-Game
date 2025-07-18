import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.HashSet;
import java.util.Random;


public class PacMan extends JPanel implements ActionListener, KeyListener {
    class Block{
        int X;
        int Y;
        int width;
        int height;
        Image image;

        int startX;
        int startY;
        char direction = 'U'; // U = Up, D = Down, L = Left, R = Right
        int velocityX = 0;
        int velocityY = 0;

        Block(Image image, int X, int Y, int width, int height) {
            this.image = image;
            this.X = X;
            this.Y = Y;
            this.width = width;
            this.height = height;
            this.startX = X;
            this.startY = Y;
        }

        void updateDirection(char direction){
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.X += this.velocityX;
            this.Y += this.velocityY;
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    // If collision occurs, revert to previous direction
                    this.direction = prevDirection;
                    updateVelocity();
                    this.X -= this.velocityX;
                    this.Y -= this.velocityY;
                    // break; // Exit the loop after the first collision
                }
            }
        }

        void updateVelocity(){
            if(this.direction == 'U'){
                this.velocityX = 0;
                this.velocityY = -tileSize/4;
            }
            else if(this.direction == 'D'){
                this.velocityX = 0;
                this.velocityY = tileSize/4;
            }
            else if(this.direction == 'L'){
                this.velocityX = -tileSize/4;
                this.velocityY = 0;
            }
            else if(this.direction == 'R'){
                this.velocityX = tileSize/4;
                this.velocityY = 0;
            }

            // else {
            //     this.velocityX = 0;
            //     this.velocityY = 0;
            // }

            // Uncomment this block if you want to use the direction to update velocity
            // switch (this.direction) {
            //     case 'U':
            //         this.velocityX = 0;
            //         this.velocityY = -tileSize/4;
            //         break;
            //     case 'D':
            //         this.velocityX = 0;
            //         this.velocityY = tileSize/4;
            //         break;
            //     case 'L':
            //         this.velocityX = -tileSize/4;
            //         this.velocityY = 0;
            //         break;
            //     case 'R':
            //         this.velocityX = tileSize;
            //         this.velocityY = 0;
            //         break;
            //     default:
            //         this.velocityX = 0;
            //         this.velocityY = 0;
            // }
        }

        void reset(){
            this.X = this.startX;
            this.Y = this.startY;
        }
    }

    private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;

    private Image wallImage;
    private Image blueGhostImage;
    private Image redGhostImage;
    private Image pinkGhostImage;
    private Image orangeGhostImage;

    // private Image pacmanImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;
    private Image pacmanUpImage;
    private Image pacmanDownImage;

    //X = wall, O = skip, P = pac man, ' ' = food
    //Ghosts: b = blue, o = orange, p = pink, r = red
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

    HashSet<Block> walls;
    HashSet<Block> ghosts;
    HashSet<Block> foods;
    Block pacman;

    Timer gameLoop;
    char[] directions = {'U', 'D', 'L', 'R'};
    Random random = new Random();
    int score = 0;
    int lives = 3;
    boolean gameOver = false;

    
    PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);
        

        // Load images
        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();

        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();
        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();

        loadMmap();
        for(Block ghost : ghosts){
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
        gameLoop = new Timer(50, this);
        gameLoop.start();
        // System.out.println(walls.size());
        // System.out.println(ghosts.size());
        // System.out.println(foods.size());
    }

    public void loadMmap(){
        walls = new HashSet<Block>();
        ghosts = new HashSet<Block>();
        foods = new HashSet<Block>();

        for (int row  = 0; row < rowCount; row++){
            for (int col = 0; col < columnCount; col++){
                String rowString = tileMap[row];
                char tileMapChar = rowString.charAt(col);

                int x = col * tileSize;
                int y = row * tileSize;

                if(tileMapChar == 'X'){ // Wall ghost
                    Block wall = new Block(wallImage, x, y, tileSize, tileSize);
                    walls.add(wall);
                }
                else if(tileMapChar == 'b'){ // Blue ghost
                    Block ghost = new Block(blueGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if(tileMapChar == 'o'){ // Orange ghost
                    Block ghost = new Block(orangeGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if(tileMapChar == 'p'){ // Pink ghost
                    Block ghost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if(tileMapChar == 'r'){ // Red ghost
                    Block ghost = new Block(redGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if(tileMapChar == 'P'){ // Pacman
                    pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                }
                else if(tileMapChar == ' '){ // Food (skip)
                    Block food = new Block(null, x + 14, y + 14, 4, 4); // Food doesn't have an image
                    foods.add(food);
                }
            }
        }
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        g.drawImage(pacman.image, pacman.X, pacman.Y, pacman.width, pacman.height, null);

        for (Block ghost : ghosts){
            g.drawImage(ghost.image, ghost.X, ghost.Y, ghost.width, ghost.height, null);
        }

        for (Block wall : walls){
            g.drawImage(wall.image, wall.X, wall.Y, wall.width, wall.height, null);
        }

        g.setColor(Color.WHITE);
        for (Block food : foods){
                g.fillRect(food.X, food.Y, food.width, food.height);
        }

        g.setFont(new Font("Arial", Font.PLAIN, 18));
        if(gameOver){
            g.drawString("Game Over: " + String.valueOf(score), tileSize/2, tileSize/2);
        }
        else {
            g.drawString("X" + String.valueOf(score), tileSize/2, tileSize/2);
        }
    }

    public void move(){
        // Update Pacman's position based on its velocity
        pacman.X += pacman.velocityX;
        pacman.Y += pacman.velocityY; 

        // Check for collisions with walls
        for (Block wall : walls){
            if(collision(pacman, wall)){
                // If collision occurs, revert Pacman's position
                pacman.X -= pacman.velocityX;
                pacman.Y -= pacman.velocityY;
                // pacman.updateVelocity(); // Update velocity to stop movement
                break; // Exit the loop after the first collision
            }
        }

        // Checks for collisions with ghosts
        for (Block ghost : ghosts){
            if(collision(ghost, pacman)){
                lives -= 1; // Decrement lives
                if(lives == 0){
                    gameOver = true; // Set game over flag
                    return;
                }
                resetposition(); // Reset Pacman's position
            }
            if(ghost.Y == tileSize * 9 && ghost.direction != 'U' && ghost.direction != 'D'){
                ghost.updateDirection('U'); // If ghost is at row 9, force it to go up
            }
            ghost.X += ghost.velocityX;
            ghost.Y += ghost.velocityY;
            for(Block wall : walls){
                if(collision(ghost, wall) || ghost.X <= 0 || ghost.X + ghost.width >= boardWidth){
                    ghost.X -= ghost.velocityX;
                    ghost.Y -= ghost.velocityY;
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }
        }
        // Check for collisions with food
        Block foodEaten = null;
        for (Block food : foods){
            if (collision(pacman, food)) {
                foodEaten = food; // Store the food that was eaten
                score += 10; // Increment score
            }
        }
        foods.remove(foodEaten); // Remove the food from the set
    }

    
    public boolean collision(Block a, Block b){
        return a.X < b.X + b.width &&
               a.X + a.width > b.X &&
               a.Y < b.Y + b.height &&
               a.Y + a.height > b.Y;
    }
    public void resetposition(){
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        for (Block ghost: ghosts){
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop(); // Stop the game loop if the game is over
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        // System.out.println("KeyEvent: " + e.getKeyCode());
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            pacman.updateDirection('U');
        } 
        else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            pacman.updateDirection('D');
        } 
        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            pacman.updateDirection('L');
        } 
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacman.updateDirection('R');
        } 
        
        // else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
        //     // Reset Pacman position
        //     pacman.X = pacman.startX;
        //     pacman.Y = pacman.startY;
        //     pacman.updateDirection('R'); // Reset direction to right
        // }

        if (pacman.direction == 'U') {
            pacman.image  = pacmanUpImage;
        }
        else if (pacman.direction == 'D'){
            pacman.image  = pacmanDownImage;
        }
        else if (pacman.direction == 'L'){
            pacman.image  = pacmanLeftImage;
        }
        else if (pacman.direction == 'R'){
            pacman.image  = pacmanRightImage;
        }
    }
}
