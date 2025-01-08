import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

class Main {
    private static JFrame f;
    private static BufferedImage buf;
    private static Robot r;
    private static DisplayMode dp;
    private static long lastFrame = 0;
    private static Image cursor;
    
    private static final int FRAME_LIMIT = (int) ((double) 1000 / 60);
    
    private static void frame() {
        int frameDelta = (int) (System.currentTimeMillis() - (lastFrame + FRAME_LIMIT));
        try {
            if (frameDelta < 0) {
                Thread.sleep(Math.abs(frameDelta));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        buf = r.createScreenCapture(new Rectangle(0, 0, dp.getWidth(), dp.getHeight()));
        f.repaint();
        
        lastFrame = System.currentTimeMillis();   
        frame();
    }
    
    public static void main(String[] args) {
        f = new JFrame("JGlass");
        f.setSize(300, 200);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);
        f.setAlwaysOnTop(true);
        
        f.setLayout(new BorderLayout());
        f.getContentPane().setBackground(Color.BLACK);
        f.setIconImage(Res.getAsImage("icon.png"));
        
        //JToolBar tb = new JToolBar("Controls"); f.add(tb, BorderLayout.PAGE_START);
        
        cursor = Res.getAsImage("left_ptr.png");
        
        f.add(new JComponent() {
            public void paintComponent(Graphics g) {
                g.drawImage(buf.getScaledInstance(getWidth(), getHeight(), Image.SCALE_FAST), 0, 0, null);
            }
        }, BorderLayout.CENTER);

        try {
            r = new Robot() {
                public BufferedImage createScreenCapture(Rectangle r) {
                    BufferedImage b = super.createScreenCapture(r);
                    Graphics g = b.getGraphics();
                    
                    Point m = MouseInfo.getPointerInfo().getLocation();
                    g.drawImage(cursor, (int) m.getX() - 6, (int) m.getY() - 2, null);
                    //g.setColor(Color.WHITE); g.fillRect((int) m.getX(), (int) m.getY(), 10, 10);
                    return b;
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        
        dp = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
        
        new Thread() {
            public void run() {
                frame();
            }
        }.start();
        
        f.setVisible(true);
    }
}