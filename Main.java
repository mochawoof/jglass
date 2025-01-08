import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

class Main {
    private static JFrame f;
    private static BufferedImage buf;
    private static Robot r;
    private static DisplayMode dp;
    private static long lastFrame;
    private static Image cursor;
    private static JButton zoomL;
    private static DecimalFormat df;
    
    private static Point dLLoc; // Delta last mouse position
    
    private static double zoom;
    private static int x;
    private static int y;
    
    private static void error(String msg) {
        JOptionPane.showMessageDialog(f, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private static double clamp(double n, double min, double max) {
        if (n < min) {
            return min;
        } else if (n > max) {
            return max;
        } else {
            return n;
        }
    }
    
    private static int clamp(int n, int min, int max) {
        return (int) clamp((double) n, (double) min, (double) max);
    }
    
    private static void frame() {
        int fpsCap = Integer.parseInt(Settings.get("Frame_Cap"));
        fpsCap = clamp(fpsCap, 1, 240);
        int frameCap = (int) ((double) 1000 / fpsCap);
        
        int frameDelta = (int) (System.currentTimeMillis() - (lastFrame + frameCap));
        try {
            if (frameDelta < 0) {
                Thread.sleep(Math.abs(frameDelta));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        int w = (int) ((double) dp.getWidth() / zoom);
        int h = (int) ((double) dp.getHeight() / zoom);
        buf = r.createScreenCapture(new Rectangle(x, y, w, h));
        f.repaint();
        
        lastFrame = System.currentTimeMillis();   
        frame();
    }
    
    private static void updateZoom(double z) {
        zoom = z;
        zoom = clamp(zoom, 1.0, 15.0);
        zoomL.setText(df.format(zoom) + "x");
    }
    
    public static void main(String[] args) {
        lastFrame = 0;
        zoom = 2.0;
        x = 0;
        y = 0;
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
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
        
        df = new DecimalFormat("0.0");
        
        zoomL = new JButton(df.format(zoom) + "x");
        tb.add(zoomL);
        zoomL.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateZoom(2.0);
                x = 0;
                y = 0;
                f.repaint();
            }
        });
        
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
        
        JComponent c = new JComponent() {
            public void paintComponent(Graphics g) {
                int scaleMode = Settings.get("Scale_Mode").equals("Smooth") ? Image.SCALE_SMOOTH : Image.SCALE_FAST;
                Image scaled = buf.getScaledInstance(getWidth(), getHeight(), scaleMode);
                g.drawImage(scaled, 0, 0, null);
            }
        };
        f.add(c, BorderLayout.CENTER);
        c.addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                updateZoom(zoom - e.getPreciseWheelRotation());
            }
        });
        
        c.addMouseListener(new MouseListener() {
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {
                dLLoc = e.getPoint();
            }
            public void mouseReleased(MouseEvent e) {}
            public void mouseClicked(MouseEvent e) {}
        });
        
        c.addMouseMotionListener(new MouseMotionListener() {
            public void mouseMoved(MouseEvent e) {}
            public void mouseDragged(MouseEvent e) {
                Point dCLoc = e.getPoint();
                
                int mx = (int) ((double) dLLoc.getX() - dCLoc.getX());
                int my = (int) ((double) dLLoc.getY() - dCLoc.getY());
                
                x += mx * 2;
                y += my * 2;
                
                dLLoc = dCLoc;
            }
        });

        try {
            r = new Robot() {
                public BufferedImage createScreenCapture(Rectangle r) {
                    BufferedImage b = super.createScreenCapture(r);
                    
                    if (Settings.get("Cursor").equals("Show")) {
                        Graphics g = b.getGraphics();
                    
                        Point m = MouseInfo.getPointerInfo().getLocation();
                        g.drawImage(cursor, (int) m.getX() - 6 - x, (int) m.getY() - 2 - y, null);
                    }
                    
                    return b;
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
            error("Failed to start JGlass. Please try again.");
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