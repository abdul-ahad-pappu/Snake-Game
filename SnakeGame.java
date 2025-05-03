package SnakeGame;
import javax.swing.*;

public class SnakeGame extends JFrame {

    public SnakeGame() {
        add(new GameBoard());
        setTitle("Snake Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null); // center the window
        setVisible(true);
    }

    public static void main(String[] args) {
        new SnakeGame();
    }
}
