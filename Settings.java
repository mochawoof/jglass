import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;

import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;

class Settings {
    public static Properties props;
    private static String filename = ".settings";
    private static final boolean DEBUG = false;
    
    // Consts for show
    public static final int OK = 0;
    public static final int CANCEL = 1;
    public static final int RESET = 2;
    
    // Settings with a . preceding their names will not be shown to the user
    // Underscores will be shown to the user as spaces
    public static HashMap<String, String[]> defaults = new HashMap<String, String[]>() {{
        put("Frame_Cap", new String[] {"60", "30", "15"});
        put("Scale_Mode", new String[] {"Smooth", "Fast"});
        put("Cursor", new String[] {"Show", "Hide"});
    }};
    
    private static void applyDefaults() {
        for (HashMap.Entry<String, String[]> e : defaults.entrySet()) {
            props.put(e.getKey(), e.getValue()[0]);
        }
    }
    
    static {
        props = new Properties();
        applyDefaults();
        
        try {
            FileInputStream in = new FileInputStream(filename);
            props.load(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String get(String key) {
        return (String) props.get(key);
    }
    
    public static void reset() {
        props = new Properties();
        applyDefaults();
        save();
    }
    
    private static void save() {
        try {
            FileOutputStream out = new FileOutputStream(filename);
            props.store(out, "");
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void set(String key, String value) {
        props.put(key, value);
        save();
    }
    
    public static int show(JFrame parent) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2));
        
        Enumeration en = Settings.props.propertyNames();
        
        HashMap<String, JComboBox> boxes = new HashMap<String, JComboBox>();
        
        while (en.hasMoreElements()) {
            String key = (String) en.nextElement();
            
            String[] defaultVals = defaults.get(key);
            
            // Make sure default values exist
            if (defaultVals != null && (!key.startsWith(".") || DEBUG)) {
                panel.add(new JLabel(key.replace("_", " ")));
                            
                JComboBox comboBox = new JComboBox(defaultVals);
                comboBox.setEditable(true);
                comboBox.setSelectedItem(Settings.get(key));
                
                panel.add(comboBox);
                boxes.put(key, comboBox);
            }
        }
        
        int opt = JOptionPane.showOptionDialog(parent, new JScrollPane(panel), "Settings", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[] {"OK", "Cancel", "Reset"}, "OK");

        if (opt == OK) {
            for (HashMap.Entry<String, JComboBox> e : boxes.entrySet()) {
                set(e.getKey().replace(" ", "_"), (String) e.getValue().getSelectedItem());
            }
        } else if (opt == RESET) {
            reset();
        }
        
        return opt;
    }
}