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

import java.util.ArrayList;

import org.nfunk.jep.type.Tensor;


/** Tester class. */
public final class STTester {
	/** The ap. */
	private ArrayList<AppProfile> ap = new ArrayList<AppProfile>();


	/** Set the profile data. */
	private void setAp() {
		// general models
		ap.add(new AppProfile("double.stg", "simple combinatorial model", "out", "20.0"));
		ap.add(new AppProfile("lorenz.stg", "moderately complex Euler sequential model", "y", "-6.077956280174972"));
		ap.add(new AppProfile("rlc.stg", "moderately complex RK2 sequential model", "I", "0.0073393031153681215"));
		// models with submodels
		ap.add(new AppProfile("super1.stg", "sub: input --> [stateless] --> output", "y", "20.0"));
		ap.add(new AppProfile("super2.stg", "sub: input --> aux --> [stateless] --> output", "y", "20.0"));
		ap.add(new AppProfile("super3.stg", "sub: state --> [stateless] --> output", "y", "y2"));
		ap.add(new AppProfile("super4.stg", "sub: state -> aux --> [stateless] --> output", "y", "y2"));
		ap.add(new AppProfile("super1b.stg", "sub: input --> [stateful] --> output", "y", "y2"));
		ap.add(new AppProfile("super2b.stg", "sub: aux --> [stateful] --> output", "y", "y2"));
		ap.add(new AppProfile("super3b.stg", "sub: state --> [stateful] --> output", "y", "y2"));
		ap.add(new AppProfile("super4b.stg", "sub: state -> aux --> [stateful] --> output", "y", "y2"));
		ap.add(new AppProfile("supersuper.stg", "sub-sub", "superout", "30.0"));
		ap.add(new AppProfile("super1-1.stg", "sub: [stateless] --> [stateless]", "y2", "40.0"));
		ap.add(new AppProfile("super1-1b.stg", "sub: [stateful] --> [stateful]", "y2", "y3"));
		// models with custom functions
		ap.add(new AppProfile("simplecustfun.stg", "simple custom function", "out", "9.0"));
		ap.add(new AppProfile("lesssimplecustfun.stg", "vectorial custom function with the iter meta-function", "out", "120.0"));
		ap.add(new AppProfile("recursivecustfun.stg", "recursive custom function", "out", "125.0"));
	}


	/** Starter.
	 * @param args the args */
	public static void main(final String[] args) { new STTester(); }


	/** Class constructor. */
	private STTester() {
		setAp();
		STGraph myApp = new STGraph(false, null, null, false);
		for(AppProfile x : ap) {
			System.out.println("Loading " + x.fileName + " (" + x.desc + ")");
			myApp.open("test/" + x.fileName);
			if(!myApp.isLoaded()) {
				System.out.println("The model cannot be loaded\n");
				return;
			}
			myApp.exec();
			double d1;
			double d2;
			try {
				d1 = Double.valueOf(x.node1).doubleValue();
			} catch (Exception e) {
				d1 = ((Tensor)myApp.getNodeRef(x.node1).getValue()).getValue();
			}
			try {
				d2 = Double.valueOf(x.node2).doubleValue();
			} catch (Exception e) {
				d2 = ((Tensor)myApp.getNodeRef(x.node2).getValue()).getValue();
			}
			if(d1 == d2) {
				System.out.println("OK: " + d1 + "=" + d2);
			} else {
				System.out.println("ERROR: " + d1 + " --- " + d2);
				return;
			}
			myApp.close();
			System.out.println();
		}
		System.out.println("All tests performed correctly!");
	}


	/** Data profiling the application. */
	class AppProfile {
		/** The file name. */
		private String fileName;
		/** The description. */
		private String desc;
		/** The node1. */
		private String node1;
		/** The node2. */
		private String node2;


		/** Class constructor.
		 * @param fileName the file name
		 * @param desc the description
		 * @param node1 the node1
		 * @param node2 the node2 */
		AppProfile(final String fileName, final String desc, final String node1, final String node2) {
			this.fileName = fileName;
			this.desc = desc;
			this.node1 = node1;
			this.node2 = node2;
		}
	}

}
