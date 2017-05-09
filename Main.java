import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

class PongPanel extends JPanel {

    private Point ballPoint = new Point(100, 100);
    
    final int ballDiameter = 20;

    int dx = 1, dy = 1;

    int left, right, top, bottom;

    final int padding = 20;
    private int score = 0;

    protected Timer ballUpdater;

    private Dimension paddleDimension = new Dimension(10, 50);

    private final int paddleHorizontalShift = 20;

    private final int paddleX = padding + paddleHorizontalShift;

    private int paddleY = padding;
    private boolean gameover = false;

    /*  HW
·       After game, display top 10 scores descending with initials of the players
·       If user score, is in the top 10 prompt for initials and then store his score and initials in the database.
     */

    PongPanel() {

        System.out.println(left + "," + right + "," + top + "," + bottom);
        setBackground(Color.BLACK);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                left = padding;
                right = PongPanel.this.getWidth() - padding;
                top = padding;
                bottom = getHeight() - padding;
            }
        }
        );

        ballUpdater = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                drawBall();               
            }
        }
        );
        ballUpdater.start();

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent mwe) {
                if (!gameover) {
                    paddleY += mwe.getWheelRotation() * 5;
                    repaint();
                }
            }
        }
        );

    }
    public boolean getGameover(){
    return gameover;
    }
    
    public Timer getTimer(){
    return ballUpdater;
    }
    
    public int getScore(){
    return score;
    }
    
    public boolean hitPaddle() {
        return ballPoint.x - (padding / 2) == paddleX
                && checkYValue();
    }

    public boolean outOfBounds() {
        return ballPoint.x <= left;
    }

    public boolean hitRightBounds() {
        return ballPoint.x + ballDiameter >= right;
    }

    public boolean hitHorizontalBounds() {
        return ballPoint.y <= top || ballPoint.y + ballDiameter >= bottom;
    }

    public void updateBallPosition() {
        if (hitPaddle()) {
            score++;
            dx = -dx;
        }

        if (hitRightBounds()) {
            dx = -dx;
            ballUpdater.setDelay(ballUpdater.getDelay() / 2);
        }

        if (hitHorizontalBounds()) {
            dy = -dy;
        }

        if (outOfBounds()) {
            gameover = true;
            ballUpdater.stop();
        }

        if (!gameover) {
            ballPoint.translate(dx, dy);
        }
    }

    public void drawBall() {
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        updateBallPosition();

        g.setColor(Color.WHITE);
        
        g.drawRect(top, left, right - padding, bottom - padding);
        
        g.drawString("Your Current Score is: " + String.valueOf(score), 20, 15);
        
        g.fillOval(ballPoint.x, ballPoint.y,
                ballDiameter, ballDiameter);

        g.fillRect(paddleX, paddleY,
                paddleDimension.width, paddleDimension.height);    
    }
    
    private boolean checkYValue() {
        int paddleDepth = (int) (paddleY + paddleDimension.getHeight());
        return ballPoint.y >= paddleY && ballPoint.y < paddleDepth;
    }
}

class PongApp extends JFrame {
        PongPanel pp = new PongPanel();

    PongApp() {
        add(pp);
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}

public class Main {    
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(1);  // try with multiple pools        
        GuiThread gt = new GuiThread();
        Runnable workerThread = gt;
        Future future = executor.submit(workerThread);
        processmessage();
            while(!gt.pa.pp.getGameover()){
                System.out.println("game running");
            }
            gt.pa.setTitle("Game Over");
            future.cancel(true);
            Database dbc = new Database();  
            dbc.getScores();
            dbc.checkForWinningScore(gt.pa.pp.getScore());
            System.exit(0);
    }
        private static void processmessage() {
        try {  
        Thread.sleep((int) (1000)); 
            for (int i = 0; i < 1000; i++) {
                System.out.println("thread processing "+i);
            }
        }
        catch (InterruptedException e) { 
            e.printStackTrace(); }
    }
}

class GuiThread implements Runnable{
PongApp pa;
    
    @Override
    public void run() {
    pa = new PongApp();
    }        
}
