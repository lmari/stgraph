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

import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphExec;


/** Tester class: show how to get group information from a web model for each language. */
public final class STGetWebGroupInfo {
	/** The file name. */
	private String fileName = "test/minweb.stg"; //$NON-NLS-1$
	STGraphExec model;
	String[][] groups;

	/** Starter.
	 * @param args the args */
	public static void main(final String[] args) { new STGetWebGroupInfo(); }


	/** Class constructor. */
	private STGetWebGroupInfo() {
		STGraph myApp = new STGraph(false, null, null, false);
		myApp.open(fileName);
		if(!myApp.isLoaded()) {
			System.out.println("The model cannot be loaded\n");
			System.exit(-1);
		}
		model = STGraph.getSTC().getCurrentGraph();

		// get the array of strings of languages in the model 
		String[] modelLanguages = model.getModelLanguages();

		// for each language...
		for(String lang : modelLanguages) { printLanguageInfo(lang); }
		System.out.println();	
		myApp.close();
		System.out.println("Ok...");
	}

	/** Helper.
	 * @param lang the language */
	private void printLanguageInfo(final String lang) {
		System.out.println("\n*** Descriptions for language " + lang + " ***");
		System.out.println("model description (URL)=" + model.getWebModelDescription(lang));
		groups = model.getWebModelGroupData(lang);
		int i = 1;
		for(String[] group : groups) {
			System.out.println("[Group " + i++ + " data] name=" + group[0] +
					"; description (URL)=" + group[1] +
					"; image=" + group[2] + // chosen from the list provided by the lib bgicons
					"; type=" + group[3]); // an integer from STGraphImpl.WEBMODEL_GROUPTYPE_STANDARD = 0;
										   //							  WEBMODEL_GROUPTYPE_ASSESSMENT = 1;
										   //                             WEBMODEL_GROUPTYPE_SUGGESTIONS = 2;

		}
	}

}
