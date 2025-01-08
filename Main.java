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
    
    private static int zoom = 1;
    
    private static void frame() {
        int frameCap = (int) ((double) 1000 / Integer.parseInt(Settings.get("Frame_Cap")));
        int frameDelta = (int) (System.currentTimeMillis() - (lastFrame + frameCap));
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
        f.setIconImage(Res.getAsImage("icon-sml.png"));
        
        JToolBar tb = new JToolBar("Controls");
        f.add(tb, BorderLayout.PAGE_START);
        
        JButton settingsB = new JButton("Settings");
        tb.add(settingsB);
        settingsB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Settings.show(f);
            }
        });
        
        JButton helpB = new JButton("Help");
        tb.add(helpB);
        helpB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(f, "JGlass v1.0.", "About", JOptionPane.PLAIN_MESSAGE, new ImageIcon(f.getIconImage()));
            }
        });
        
        cursor = Res.getAsImage("left_ptr.png");
        
        f.add(new JComponent() {
            public void paintComponent(Graphics g) {
                int scaleMode = Settings.get("Scale_Mode").equals("Smooth") ? Image.SCALE_SMOOTH : Image.SCALE_FAST;
                Image scaled = buf.getScaledInstance(getWidth(), getHeight(), scaleMode);
                g.drawImage(scaled, 0, 0, null);
            }
        }, BorderLayout.CENTER);

        try {
            r = new Robot() {
                public BufferedImage createScreenCapture(Rectangle r) {
                    BufferedImage b = super.createScreenCapture(r);
                    
                    if (Settings.get("Cursor").equals("Show")) {
                        Graphics g = b.getGraphics();
                    
                        Point m = MouseInfo.getPointerInfo().getLocation();
                        g.drawImage(cursor, (int) m.getX() - 6, (int) m.getY() - 2, null);
                    }
                    
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