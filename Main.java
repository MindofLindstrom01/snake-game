import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Snake Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            SnakePanel snakepanel = new SnakePanel();
            frame.add(snakepanel);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            Timer timer = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    snakepanel.moveSnake();
                    snakepanel.checkCollisions();
                    snakepanel.repaint();
                }
        });
        timer.start();
        snakepanel.generateFood();
    });
}

static class SnakePanel extends JPanel implements KeyListener {

    private static LinkedList<Point> snake = new LinkedList<>();
    private static final int CELL_SIZE = 15;
    private static final int UP = 1;
    private static final int DOWN = 2;
    private static final int LEFT = 3;
    private static final int RIGHT = 4;
    private static int direction = RIGHT;
    private Point food;
    private JLabel scoreLabel;
    private int score = 0;
    private static boolean gameOver;
    private JButton tryAgain, giveUp;

    public SnakePanel() {
        setPreferredSize(new Dimension(600, 600));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        scoreLabel = new JLabel("Score: " + score);
        scoreLabel.setForeground(Color.WHITE);
        add(scoreLabel);

        generateFood();
        snake.add(new Point(5, 5));

        tryAgain = new JButton("Try Again");
        tryAgain.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetGame();
            }
        });
        giveUp = new JButton("Give Up");
        giveUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!gameOver) {
            for (Point snakePiece : snake) {
                g.setColor(Color.GREEN);
                g.fillRect(snakePiece.x * CELL_SIZE, snakePiece.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                g.setColor(Color.ORANGE);
                g.fillRoundRect(food.x * CELL_SIZE, food.y * CELL_SIZE, CELL_SIZE, CELL_SIZE, CELL_SIZE, CELL_SIZE);

                int labelWidth = scoreLabel.getPreferredSize().width;
                scoreLabel.setBounds(getWidth() - labelWidth - 10, 10, labelWidth, 20);
            }
        } else {
            removeAll();
            repaint();
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("GAME OVER", getWidth() / 2 - 75, getHeight() / 2 - 10);

            int buttonWidth = tryAgain.getPreferredSize().width;
            int buttonHeight = tryAgain.getPreferredSize().height;

            tryAgain.setBounds(getWidth() / 2 - buttonWidth / 2, getHeight() / 2 + 20, buttonWidth, buttonHeight);
            giveUp.setBounds(getWidth() / 2 - buttonWidth / 2, getHeight() / 2 + 20 + buttonHeight + 10, buttonWidth, buttonHeight);

            g.setFont(new Font("Arial", Font.BOLD, 18));
            FontMetrics metrics = g.getFontMetrics();
            String scoreText = "Your Score: " + score;
            int xScore = getWidth() / 2 - metrics.stringWidth(scoreText) / 2;
            int yScore = getHeight() / 2 - 50;
            g.drawString(scoreText, xScore, yScore);

            if (tryAgain.getParent() == null) {
                add(tryAgain);
                add(giveUp);
                revalidate();
            }
        }
    }

    private static void moveSnake() {
        if (!gameOver) {
            Point head = snake.getFirst();
            Point newHead;
            switch (direction) {
                case UP:
                    newHead = new Point(head.x, head.y - 1);
                    break;
                case DOWN:
                    newHead = new Point(head.x, head.y + 1);
                    break;
                case LEFT:
                    newHead = new Point(head.x - 1, head.y);
                    break;
                case RIGHT:
                    newHead = new Point(head.x + 1, head.y);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + direction);
            }

            snake.addFirst(newHead);
            if (snake.size() > 1) {
                snake.removeLast();
            }
        }
    }

    private void generateFood() {
        int x = (int) (Math.random() * (getWidth() / CELL_SIZE));
        int y = (int) (Math.random() * (getHeight() / CELL_SIZE));
        food = new Point(x, y);
    }

    private void checkCollisions() {
        if (!gameOver) {
            Point head = snake.getFirst();
            if (head.equals(food)) {
                switch (direction) {
                    case UP:
                        snake.addLast(new Point(head.x, head.y + 1));
                        break;
                    case DOWN:
                        snake.addLast(new Point(head.x, head.y - 1));
                        break;
                    case LEFT:
                        snake.addLast(new Point(head.x + 1, head.y));
                        break;
                    case RIGHT:
                        snake.addLast(new Point(head.x - 1, head.y));
                        break;
                }
                generateFood();

                score++;
                scoreLabel.setText("Score: " + score);
            }
            // Check collision with boundaries
            if (head.x < 0 || head.y < 0 || head.x >= getWidth() / CELL_SIZE || head.y >= getHeight() / CELL_SIZE) {
                gameOver = true;
            }

            // Check collision with itself
            if (snake.size() > 1 && snake.subList(1, snake.size()).contains(head)) {
                gameOver = true;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        int keyCode = e.getKeyCode();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_UP && direction != DOWN) {
            direction = UP;
        } else if (keyCode == KeyEvent.VK_DOWN && direction != UP) {
            direction = DOWN;
        } else if (keyCode == KeyEvent.VK_LEFT && direction != RIGHT) {
            direction = LEFT;
        } else if (keyCode == KeyEvent.VK_RIGHT && direction != LEFT) {
            direction = RIGHT;
        } else if (keyCode == KeyEvent.VK_S && direction != UP) {
            direction = DOWN;
        } else if (keyCode == KeyEvent.VK_A && direction != RIGHT) {
            direction = LEFT;
        } else if (keyCode == KeyEvent.VK_D && direction != LEFT) {
            direction = RIGHT;
        } else if (keyCode == KeyEvent.VK_W && direction != DOWN) {
            direction = UP;
        }
        
        checkCollisions(); // Check for collisions on key press
        repaint(); // Repaint after key press
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
    }

    private void resetGame() {
    snake.clear();
    snake.add(new Point(5, 5));
    add(scoreLabel);
    direction = RIGHT;
    score = 0;
    scoreLabel.setText("Score: " + score);
    generateFood();
    gameOver = false;

    
    remove(tryAgain);
    remove(giveUp);

    
    repaint();
}
    }
}
