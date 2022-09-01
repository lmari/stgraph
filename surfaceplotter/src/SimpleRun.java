import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.ericaro.surfaceplotter.JSurfacePanel;
import net.ericaro.surfaceplotter.JSurfacePanelCaller;

public class SimpleRun implements JSurfacePanelCaller {

	public void testSomething() {
		JSurfacePanel jsp = new JSurfacePanel(this, Locale.ENGLISH);
		jsp.fillDefaultSurfaceModel();
		jsp.setTitleText("Hello");
		JFrame jf= new JFrame("test");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.getContentPane().add(jsp, BorderLayout.CENTER);
		jf.pack();
		jf.setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() { new SimpleRun().testSomething(); }
		});
	}

	public void clickingMouse(MouseEvent e) { ; }

	public void pressingMouse(MouseEvent e) { ; }

	public void releasingMouse(MouseEvent e) { ; }

}
