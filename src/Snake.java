import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Snake extends JPanel{
    //ratio to increase window size
    private final static int RATIO = 1;

    //variables for Window
    private final static int W_HEIGHT = 700*RATIO;
    private final static int B_DIMEN = 600*RATIO;

    //game setting
    private final static int DELAY = 100;
    private final static int BLOCKSIZE = 40*RATIO;
    private final static int INITLOCIX = BLOCKSIZE*3;
    private final static int INITLOCIY = BLOCKSIZE*3;
    private final static int POSITIONS = B_DIMEN/BLOCKSIZE;

    // for Snake
    private static int x[] = new int[POSITIONS*POSITIONS];
    private static int y[] = new int[POSITIONS*POSITIONS];

    // for apple
    private static int appleX;
    private static int appleY;

    //in game stuff
    private static boolean gameCont = true;
    private static Integer score = 0;
    private static String sscore;
    private static int snakeLen = 3;
    private static boolean pause = false;

    // directions
    private static boolean up = false;
    private static boolean down = false;
    private static boolean left = false;
    private static boolean right = true;
    private static boolean pressed = true;



    @Override
    protected void paintComponent(Graphics g){

        // refresh board
        super.paintComponent(g);
        setBackground(Color.BLACK);

        // set font
        Font font = new Font("Helvetica", Font.BOLD, 50);
        g.setFont(font);
        FontMetrics metr = getFontMetrics(font);

        //set message
        sscore = "SCORE: " + score.toString();


        if (gameCont){
            // draw border
            g.setColor(Color.GREEN);
            g.drawRect(0,0,B_DIMEN, B_DIMEN);

            // draw score
            g.drawString(sscore,  10, B_DIMEN + font.getSize());

            // draw apple
            g.setColor(Color.RED);
            g.fillRect(appleX, appleY, BLOCKSIZE, BLOCKSIZE);

            // draw snake
            for (int z = 0; z < snakeLen; z++) {
                if (z == 0) {
                    g.setColor(Color.GREEN);
                    g.fillRect(x[z], y[z], BLOCKSIZE,BLOCKSIZE);
                } else {
                    g.setColor(Color.GRAY);
                    g.fillRect( x[z], y[z], BLOCKSIZE,BLOCKSIZE);
                }
            }

        }else{

            // game over
            String message = "GAME OVER";
            g.setColor(Color.GREEN);
            g.drawString(message, (B_DIMEN - metr.stringWidth(message))/2, W_HEIGHT/2 - font.getSize());

            // score
            g.drawString(sscore, (B_DIMEN-metr.stringWidth(sscore)) /2, W_HEIGHT/2 );

            // restart
            String restart = "ENTER to restart";
            g.drawString(restart, (B_DIMEN-metr.stringWidth(restart))/2, W_HEIGHT/2 + font.getSize());

        }


    }

    public Snake(JFrame window){
        window.addKeyListener(new ArrowAdapter());
        window.setSize(B_DIMEN + 20,W_HEIGHT);

    }

    public static void main(String args[]) throws InterruptedException, FileNotFoundException, UnsupportedEncodingException{
        startGame();
    }

    public static void startGame() throws InterruptedException{
        // initialize window
        JFrame window = new JFrame();
        window.setTitle("Snake3");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);

        // initialize game
        reset();
        Snake snake = new Snake(window);
        window.add(snake);

        while(true){
            while(gameCont){

                move();

                checkCollisions();
                //generateMap();
                snake.repaint();

                if (pause){
                    while(pause){
                        Thread.sleep(DELAY);
                    }
                }

                Thread.sleep(DELAY);
            }
            snake.repaint();

            System.out.println("game is over");
            pressed = false;
            while(!pressed){
                Thread.sleep(DELAY);
            }

            if(gameCont){
                System.out.println("we are continuing game");
                reset();
            }else{
                break;
            }
        }

        // display end game
        snake.repaint();

        window.dispose();
    }

    private static void reset() {
        snakeLen = 3;
        score = 0;
        makeSnake();
        spawnApple();
        left = false;
        right = true;
        up = false;
        down = false;
        pressed = true;
    }

    private static void makeSnake() {
        // TODO Auto-generated method stub
        for (int z = 0; z < snakeLen; z++) {
            x[z] = INITLOCIX - z * BLOCKSIZE;
            y[z] = INITLOCIY;
        }
    }

    private static void spawnApple(){

        int r = (int) (Math.random() * POSITIONS);
        appleX = r * BLOCKSIZE;

        r = (int) (Math.random() * POSITIONS);
        appleY = r * BLOCKSIZE;

    }

    private static void move(){
        for (int z = snakeLen; z > 0; z--) {
            x[z] = x[(z - 1)];
            y[z] = y[(z - 1)];
        }

        if (left) {
            x[0] -= BLOCKSIZE;
        }

        if (right) {
            x[0] += BLOCKSIZE;
        }

        if (up) {
            y[0] -= BLOCKSIZE;
        }

        if (down) {
            y[0] += BLOCKSIZE;
        }
    }

    private static void checkCollisions(){
        // if snake hits itself
        for (int z = snakeLen; z > 0; z--) {
            if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
                gameCont = false;
            }
        }
        // check if snake hits wall
        if (y[0] >= B_DIMEN) {
            gameCont = false;
        }

        if (y[0] < 0) {
            gameCont = false;
        }

        if (x[0] >= B_DIMEN) {
            gameCont = false;
        }

        if (x[0] < 0) {
            gameCont = false;
        }
        // if snake hits apple
        if ((x[0] == appleX) && (y[0] == appleY)) {
            snakeLen++;
            score++;
            spawnApple();
        }
    }

    private class ArrowAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) && (!right)) {
                left = true;
                up = false;
                down = false;
            }

            if ((key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) && (!left)) {
                right = true;
                up = false;
                down = false;
            }

            if ((key == KeyEvent.VK_UP || key == KeyEvent.VK_W) && (!down)) {
                up = true;
                right = false;
                left = false;
            }

            if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) && (!up)) {
                down = true;
                right = false;
                left = false;
            }

            if (key == KeyEvent.VK_P){
                pause = !pause;
            }
            if (key == KeyEvent.VK_ENTER){
                if(!pressed){
                    gameCont = true;
                    pressed = true;
                }else{
                    reset();
                }

            }
            if(key == KeyEvent.VK_ESCAPE){
                gameCont = false;
                pressed = true;
            }

        }
    }

}

