import java.net.URL;

import javax.swing.ImageIcon;
import java.awt.Image;

import java.io.InputStream;

class Res {
    public static URL get(String path) {
        return Res.class.getResource(path);
    }
    public static ImageIcon getAsImageIcon(String path) {
        return new ImageIcon(get(path));
    }
    public static Image getAsImage(String path) {
        return getAsImageIcon(path).getImage();
    }
    public static InputStream getAsStream(String path) {
        return Res.class.getResourceAsStream(path);
    }
    public static String getAsString(String path) {
        InputStream stream = getAsStream(path);
        int i;
        String str = "";
        try {
            while ((i = stream.read()) != -1) {
                str += (char) i;
            }
        } catch (Exception e) {
            e.printStackTrace();
            str = null;
        }
        return str;
    }
}