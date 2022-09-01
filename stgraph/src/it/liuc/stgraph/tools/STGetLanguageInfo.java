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
import it.liuc.stgraph.node.STNode;


/** Tester class: show how to get language information from a model. */
public final class STGetLanguageInfo {
	/** The file name. */
	private String fileName = "test/minweb.stg"; //$NON-NLS-1$
	STGraphExec model;
	String[][] groups;
	STNode node;

	/** Starter.
	 * @param args the args */
	public static void main(final String[] args) { new STGetLanguageInfo(); }


	/** Class constructor. */
	private STGetLanguageInfo() {
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
		//printLanguageInfo("nonexistinglanguage");
		//printLanguageInfo(null);
		System.out.println();
	
		myApp.close();
		System.out.println("\nOk...");
	}

	/** Helper.
	 * @param lang the language */
	private void printLanguageInfo(final String lang) {
		System.out.println("\n*** Descriptions for language " + lang + " ***");
		System.out.println("[Model description (URL)] " + model.getWebModelDescription(lang));
		groups = model.getWebModelGroupData(lang);
		for(String[] group : groups) {
			System.out.println("[Group data] [title] " + group[0] + " [description (URL)] " + group[1]);
		}
		node = model.getNodeByName("in"); // example of a node in the model  
		System.out.println("[node name] " + node.getCName(lang));
		System.out.println("[node description] " + node.getDescription(lang));
		System.out.println("[node unit name] " + node.getUnit(lang));
		System.out.println("all cprops " + node.getCProperties());
	}

}
