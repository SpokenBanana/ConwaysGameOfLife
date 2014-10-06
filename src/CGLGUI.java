import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;

/*
    This is where the logic for the GUI is at. It uses GCL.java for its JPanel.
    It uses the methods in GCL to manipulate the simulation.
 */
public class CGLGUI extends JFrame implements ChangeListener, KeyListener, ActionListener{
    public static void main(String[] args) {
        CGLGUI g = new CGLGUI();
    }
    private CGL c;
    private String lastdir;
    public CGLGUI() {
        super("Conway's Game of Life");
        c = new CGL();
        JPanel controls = new JPanel();
        controls.add(new JLabel("CellSize"));
        controls.add(createSliderFor("slider", 1, 20, 9, 10));
        controls.add(new JLabel("Frame Rate"));
        controls.add(createSliderFor("framerate", 0, 30, 10, 10));
        controls.add(createButton("blackColor", "Set background color"));
        controls.add(createButton("whiteColor", "Set cell color"));
        this.add(c, BorderLayout.CENTER);
        this.add(controls, BorderLayout.NORTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setFocusable(true);
        c.setFocusable(true);
        c.addKeyListener(this);
        this.addKeyListener(this);
        try {
            if (!getBoardFile())
                c.setBoard(ImageIO.read(this.getClass().getResource("third.png")));
            resizeFrame();
        } catch (Exception e) { System.exit(0); }
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        c.run();
    }
    private JButton createButton(String name, String message) {
        JButton b = new JButton(message);
        b.setName(name);
        b.addActionListener(this);
        return b;
    }
    private JSlider createSliderFor(String name, int min, int max, int intervals, int value) {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, value);
        slider.setName(name);
        slider.addChangeListener(this);
        slider.setMajorTickSpacing(intervals);
        slider.setMinorTickSpacing(1);
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);
        slider.setFocusable(true);
        slider.addKeyListener(this);
        return slider;
    }
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        String name = source.getName();
        if (name.equals("slider") && !source.getValueIsAdjusting()) {
            c.setCellSize(source.getValue());
            resizeFrame();
        }
        else if (name.equals("framerate")){
            if (!source.getValueIsAdjusting())
                c.frameRate = source.getValue();

        }
    }
    public boolean getBoardFile() {
        BufferedImage b;
        try{
            JFileChooser chooser = new JFileChooser(lastdir);
            chooser.setFileFilter(new FileNameExtensionFilter("JPG & GIF Images", "jpg", "gif", "png"));
            int returnVal = chooser.showOpenDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                String path = chooser.getSelectedFile().getPath();
                b = ImageIO.read(new File(path));
                lastdir = path;
                c.setBoard(b);
            }
            else return false;
        } catch (Exception ex) { System.exit(0); }
        return true;
    }
    private void resizeFrame() {
        this.revalidate();
        this.repaint();
        this.pack();
        this.setLocationRelativeTo(null);
    }
    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            getBoardFile();
            resizeFrame();
        }
    }
    private Color getColorForBoard(Color c) {
        Color old = c;
        c = JColorChooser.showDialog(new JPanel(), "Select Color", c);
        if (c == null) return old;
        return c;
    }
    public void keyReleased(KeyEvent e) {}
    public void actionPerformed(ActionEvent e) {
        String name = ((JButton)e.getSource()).getName();
        int oldframe = c.frameRate;
        c.frameRate = 0; // pause the simulation while they are picking a color
        if (name.equals("blackColor"))
            c.DEAD_COLOR = getColorForBoard(c.DEAD_COLOR);
        else if (name.equals("whiteColor"))
            c.LIVE_COLOR = getColorForBoard(c.LIVE_COLOR);
        c.frameRate = oldframe; // restore the frame rate (or resume the game) once they have picked a color
    }
}