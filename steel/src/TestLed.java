import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import eu.hansolo.steelseries.extras.Led;
import eu.hansolo.steelseries.tools.LedColor;


public class TestLed {
	static boolean state = false;
	
    private static void createAndShowUI() {
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationByPlatform(true);

        JPanel panel = new JPanel() {
            @Override 
            public Dimension getPreferredSize() {
                return new Dimension(300, 300);
            }
        };

        final Led x = new Led();
        x.setLedColor(LedColor.GREEN);

        panel.setLayout(new BorderLayout());
        panel.add(x, BorderLayout.CENTER);
        frame.add(panel);

        JPanel buttonsPanel = new JPanel();

        JButton button = new JButton("Set");
        button.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
            	state = state ? false : true;
            	x.setLedOn(state);
            }
        });

        buttonsPanel.add(button);

        frame.add(buttonsPanel, BorderLayout.NORTH);

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowUI();
            }
        });
    }
}
