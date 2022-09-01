package net.ericaro.surfaceplotter;

import java.awt.event.MouseEvent;

/**
 * //LM// added
 */
public interface JSurfacePanelCaller {
	public void clickingMouse(MouseEvent e);
	public void pressingMouse(MouseEvent e);
	public void releasingMouse(MouseEvent e);
}
