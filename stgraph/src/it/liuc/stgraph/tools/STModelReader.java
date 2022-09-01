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
package it.liuc.stgraph.tools;

import it.liuc.stgraph.STConfigurator;
import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.util.STTools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipInputStream;

import net.n3.nanoxml.IXMLParser;
import net.n3.nanoxml.StdXMLReader;
import net.n3.nanoxml.XMLElement;
import net.n3.nanoxml.XMLParserFactory;


/** Helper class for reading a model file without loading it into the execution environment,
 * typically for getting information from its header section. */
public class STModelReader {
	private HashMap<String, String> props = new HashMap<String, String>();


	/** Just for testing purposes.
	 * @param args filename */
	public static void main(String[] args) {
		try {
			String fileName = "/home/luca/workspace/stgraph/test/minweb.stg"; //$NON-NLS-1$

			STModelReader modelReader = new STModelReader(new FileInputStream(fileName), fileName);
			System.out.println(modelReader.getProps()); // just an example
			for(String lang : modelReader.getModelLanguages()) { // just an example
				System.out.println(lang);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}


	/** Class constructor.
	 * @param fileName the file name */
	@SuppressWarnings("rawtypes")
	public STModelReader(final String fileName) throws Exception {
		if(STTools.isEmpty(fileName)) { throw new FileNotFoundException("A filename must be specified"); } //$NON-NLS-1$
		if(!(new File(fileName)).exists()) { throw new FileNotFoundException("File not found"); } //$NON-NLS-1$
		String p;
		IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
		parser.setReader(new StdXMLReader(getSpecificStream(new FileInputStream(fileName), fileName)));
		XMLElement root = (XMLElement)parser.parse();
		Enumeration theEnum = root.enumerateChildren();

		XMLElement head = (XMLElement)theEnum.nextElement(); // <head>
		theEnum = head.enumerateAttributeNames();

		while(theEnum.hasMoreElements()) {
			p = (String)theEnum.nextElement();
			props.put(p, head.getAttribute(p, null));
		}
	}


	/** Class constructor.
	 * @param stream the input stream
	 * @param fileName the file name */
	@SuppressWarnings("rawtypes")
	public STModelReader(final InputStream stream, final String fileName) throws Exception {
		String p;
		IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
		parser.setReader(new StdXMLReader(getSpecificStream(stream, fileName)));
		XMLElement root = (XMLElement)parser.parse();
		Enumeration theEnum = root.enumerateChildren();

		XMLElement head = (XMLElement)theEnum.nextElement(); // <head>
		theEnum = head.enumerateAttributeNames();

		while(theEnum.hasMoreElements()) {
			p = (String)theEnum.nextElement();
			props.put(p, head.getAttribute(p, null));
		}
	}


	private InputStream getSpecificStream(final InputStream stream, final String fileName) {
		if(!fileName.endsWith(".stz")) { return stream; } //$NON-NLS-1$
		try {
			ZipInputStream s = new ZipInputStream(stream);
			while(!s.getNextEntry().getName().equals("base")) { ; } //$NON-NLS-1$
			return s;
		} catch (Exception ex) {
			STTools.messenger(STTools.MESSAGETYPE_ERR, STGraphC.getMessage("ERR.FILE_NOT_FOUND") + " (" + fileName + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			return null;
		}
	}


	/** Get the map of all the header properties.
	 * @return the props */
	public HashMap<String, String> getProps() { return props; }


	/** Get the model name.
	 * @return value */
	public String getModelName() { return props.get("systemName"); } //$NON-NLS-1$


	/** Get the model description.
	 * @return value */
	public String getModelDescription() { return props.get("description"); } //$NON-NLS-1$


	/** Get the time unit description.
	 * @return value */
	public String getTimeUnitDescription() { return props.get("timeUnitDescription"); } //$NON-NLS-1$


	/** Get the number of iterations
	 * (one less than actual number, to keep into account the initial step).
	 * @return value */
	public int getNumberOfIterations() { return (int)Double.parseDouble(props.get("time1")) - (int)Double.parseDouble(props.get("time0")); } //$NON-NLS-1$ //$NON-NLS-2$


	/** Get array of model languages.
	 * @return array */
	public String[] getModelLanguages() {
		String s = props.get("webModelLanguages"); //$NON-NLS-1$
		if(s != null) { return s.split(STTools.COMMA);}
		return new String[] {STConfigurator.read().getProperty("MODEL.LANGUAGES.DEFAULT")}; //$NON-NLS-1$
	}

}
