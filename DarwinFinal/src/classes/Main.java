package classes;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.Border;

/**
 * Main.java - creates the JFrame to put the game panel
 *
 * @author d.ross2
 */
public class Main {
    // components instances
    static JFrame frame;
    static JButton play;
    static JButton instructions;
    static Font font;
    static Border border;
    static JLabel background;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // initializing objects
        frame = new JFrame();
        play = new JButton("Start");
        instructions = new JButton("Instructions");
        font = new Font("Monospaced", Font.PLAIN, 40);
        border = BorderFactory.createLineBorder(Color.WHITE);
        background = new JLabel(new javax.swing.ImageIcon(Main.class.getResource("/imgs/title.jpg")));

        // setting container properties
        frame.setSize(1800, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        // add the components to the jframe
        addComponents();

        // make the jframe visible
        frame.setVisible(true);
    }

    /**
     * add all the components to the JFrame
     */
    private static void addComponents() {
        // adding controls to container
        frame.add(play);
        frame.add(instructions);
        frame.add(background);
        // background label covers the entire frame
        background.setBounds(0, 0, 1800, 1000);
        // play button properties are set
        play.setBounds(700, 450, 400, 100);
        play.setFont(font);
        play.setBackground(Color.BLACK);
        play.setForeground(Color.WHITE);
        play.setBorder(border);
        // pressing the play button makes all the 
        // components invisible and creates a game panel
        play.addActionListener((ae) -> {
            play.setVisible(false);
            instructions.setVisible(false);
            background.setVisible(false);
            frame.add(new GamePanel());
        });
        // set instructions button properties
        instructions.setBounds(700, 600, 400, 100);
        instructions.setFont(font);
        instructions.setBackground(Color.BLACK);
        instructions.setForeground(Color.WHITE);
        instructions.setBorder(border);
        // pressing the instructions button shows controls
        instructions.addActionListener((ae) -> {
            JOptionPane.showMessageDialog(frame, 
                    "Defend Trump city!\n\nControls:"
                            + "\nW - Set throttle to max"
                            + "\nS - Set throttle to zero"
                            + "\nA - Tilt drone left"
                            + "\nD - Tilt drone right"
                            + "\n← - shoot a laser left"
                            + "\n→ - Shoot a laser right");
        });
    }

}
