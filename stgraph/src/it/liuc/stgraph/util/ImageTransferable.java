/*******************************************************************************
 * This file is part of STGraph, Copyright 2004-2018, Luca Mari.
 * 
 * STGraph is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2.
 * 
 * STGraph is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with STGraph.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package it.liuc.stgraph.util;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;


/** The Class ImageTransferable. */
public class ImageTransferable implements Transferable {
	/** The image. */
	private Image image;


	/** Class constructor.
	 * @param comp the comp */
	ImageTransferable(final Component comp) { image = toBufferedImage(comp); }


	/** Class constructor.
	 * @param _image the image */
	ImageTransferable(final Image _image) { image = _image; }


	/** Get the transfer data flavors.
	 * @return the transfer data flavors */
	public final DataFlavor[] getTransferDataFlavors() { return new DataFlavor[] { DataFlavor.imageFlavor }; }
	//public final DataFlavor[] getTransferDataFlavors() { return new DataFlavor[] { new DataFlavor("image/png", "PNG Image") }; } //$NON-NLS-1$ //$NON-NLS-2$


	/** Check if is data flavor supported.
	 * @param flavor the flavor
	 * @return true, if is data flavor supported */
	public final boolean isDataFlavorSupported(final DataFlavor flavor) { return DataFlavor.imageFlavor.equals(flavor); }


	/** Get the transfer data.
	 * @param flavor the flavor
	 * @return the transfer data
	 * @throws UnsupportedFlavorException the unsupported flavor exception */
	public final Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException {
		if(!isDataFlavorSupported(flavor)) { throw new UnsupportedFlavorException(flavor); }
		return image;
	}


	/** Draw the component into a buffered image and return that image.
	 * @param comp the comp
	 * @return BufferedImage */
	public static BufferedImage toBufferedImage(final Component comp) {
		BufferedImage tempImage = new BufferedImage(comp.getSize().width, comp.getSize().height, BufferedImage.TYPE_INT_ARGB);
		Graphics tempGraphics = tempImage.getGraphics();
		comp.paint(tempGraphics);
		/* also writing it to a file...
		try {
			File file = new File("temp.png"); //$NON-NLS-1$
			if(file.exists()) { file.delete(); }
			OutputStream out = new FileOutputStream("temp.png"); //$NON-NLS-1$
			ImageIO.write(tempImage, "png", out); //$NON-NLS-1$
			out.flush();
			out.close();
		} catch (Exception e) { ; }
		 */
		return tempImage;
	}

}
