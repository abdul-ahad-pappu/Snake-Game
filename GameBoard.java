package SnakeGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GameBoard extends JPanel implements ActionListener {

    private final int B_WIDTH = 300;
    private final int B_HEIGHT = 300;
    private final int DOT_SIZE = 10;
    private final int ALL_DOTS = 900;
    private final int RAND_POS = 29;
    private final int DELAY = 140;

    private final int[] x = new int[ALL_DOTS];
    private final int[] y = new int[ALL_DOTS];

    private int dots;
    private int apple_x;
    private int apple_y;
    private int score = 0;
    private static int highScore = 0;
    private static String highScoreName = "None";

    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = true;
    private boolean dialogShown = false;
    private boolean paused = false;

    private String playerName = "Player";
    private Timer timer;

    public GameBoard() {
        playerName = JOptionPane.showInputDialog(null, "Enter your name:", "Player Name", JOptionPane.PLAIN_MESSAGE);
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Player";
        }
        initBoard();
    }

    private void initBoard() {
        setBackground(Color.black);
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        setFocusable(true);
        addKeyListener(new TAdapter());
        initGame();
    }

    private void initGame() {
        dots = 3;
        int startX = B_WIDTH / 2;
        int startY = B_HEIGHT / 2;

        for (int i = 0; i < dots; i++) {
            x[i] = startX - i * DOT_SIZE;
            y[i] = startY;
        }

        locateApple();
        timer = new Timer(DELAY, this);
        timer.start();
        dialogShown = false;
        score = 0;
    }

    private void locateApple() {
        Random rand = new Random();
        apple_x = rand.nextInt(RAND_POS) * DOT_SIZE;
        apple_y = rand.nextInt(RAND_POS) * DOT_SIZE;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
        if (inGame) {
            g.setColor(Color.red);
            g.fillOval(apple_x, apple_y, DOT_SIZE, DOT_SIZE);

            for (int i = 0; i < dots; i++) {
                g.setColor(i == 0 ? Color.green : Color.yellow);
                g.fillRect(x[i], y[i], DOT_SIZE, DOT_SIZE);
            }

            g.setColor(Color.white);
            g.setFont(new Font("Helvetica", Font.PLAIN, 12));
            g.drawString("Score: " + score, 5, 15);
            g.drawString("High Score: " + highScore + " (" + highScoreName + ")", 5, 30);

            if (paused) {
                g.setFont(new Font("Helvetica", Font.BOLD, 16));
                g.drawString("PAUSED", (B_WIDTH - g.getFontMetrics().stringWidth("PAUSED")) / 2, B_HEIGHT / 2);
            }

            Toolkit.getDefaultToolkit().sync();
        } else {
            g.setColor(Color.white);
            g.setFont(new Font("Helvetica", Font.BOLD, 14));
            FontMetrics metr = getFontMetrics(g.getFont());
            g.drawString("Game Over", (B_WIDTH - metr.stringWidth("Game Over")) / 2, B_HEIGHT / 2);

            if (!dialogShown) {
                dialogShown = true;
                Timer delayTimer = new Timer(500, e -> showGameOverDialog());
                delayTimer.setRepeats(false);
                delayTimer.start();
            }
        }
    }

    private void showGameOverDialog() {
        if (score > highScore) {
            highScore = score;
            highScoreName = playerName;
        }

        int choice = JOptionPane.showOptionDialog(
            this,
            "Game Over!\nYour Score: " + score + "\nHigh Score: " + highScore + " (" + highScoreName + ")\nPlay again?",
            "Snake Game",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            new String[]{"Play Again", "Exit"},
            "Play Again"
        );

        if (choice == JOptionPane.YES_OPTION) {
            restartGame();
        } else {
            System.exit(0);
        }
    }

    private void restartGame() {
        timer.stop();
        leftDirection = false;
        rightDirection = true;
        upDirection = false;
        downDirection = false;
        inGame = true;
        paused = false;
        dialogShown = false;
        initGame();
        repaint();
    }

    private void checkApple() {
        if (x[0] == apple_x && y[0] == apple_y) {
            dots++;
            score++;
            locateApple();
        }
    }

    private void move() {
        for (int i = dots; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        if (leftDirection) x[0] -= DOT_SIZE;
        if (rightDirection) x[0] += DOT_SIZE;
        if (upDirection) y[0] -= DOT_SIZE;
        if (downDirection) y[0] += DOT_SIZE;
    }

    private void checkCollision() {
        for (int i = dots; i > 0; i--) {
            if (i > 4 && x[0] == x[i] && y[0] == y[i]) {
                inGame = false;
            }
        }

        if (x[0] >= B_WIDTH) x[0] = 0;
        else if (x[0] < 0) x[0] = B_WIDTH - DOT_SIZE;

        if (y[0] >= B_HEIGHT) y[0] = 0;
        else if (y[0] < 0) y[0] = B_HEIGHT - DOT_SIZE;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame && !paused) {
            checkApple();
            move();
            checkCollision();
        }
        repaint();
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true;
                leftDirection = false;
                rightDirection = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true;
                leftDirection = false;
                rightDirection = false;
            }

            if (key == KeyEvent.VK_P) {
                paused = !paused;
                repaint();
            }
        }
    }
}
