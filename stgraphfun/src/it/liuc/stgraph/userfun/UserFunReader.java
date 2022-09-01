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
package it.liuc.stgraph.userfun;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;


public class UserFunReader {
	/** The properties. */


	public static File[] getFiles() {
		Properties properties = new Properties();
		InputStream in = null;
		try {
			in = new ResourceLoader().getResource("datafiles/conf.properties").openStream();
			properties.load(in);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { in.close(); } catch (Exception e) { ; }
		}
		String s = (String)properties.getProperty("numFiles"); //$NON-NLS-1$
		if(s == null) { return null; }
		int n;
		try {
			n = Integer.parseInt(s);
		} catch (Exception e) {
			return null;
		}
		if(n <= 0) { return null; }
		File[] result = new File[n];
		for(int i = 0; i < n; i++) {
			String f = (String)properties.getProperty("file" + i); //$NON-NLS-1$
			try {
				result[i] = new File(new ResourceLoader().getResource("datafiles/" + f).toString()); //$NON-NLS-1$
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	
	public static InputStream getFile(final String filename) {
		try {
			return new ResourceLoader().getResource("datafiles/" + filename).openStream(); //$NON-NLS-1$ //$NON-NLS-2$	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	private static class ResourceLoader {

		public URL getResource(final String path) {
			return getClass().getClassLoader().getResource(path);
		}
		
	}

}
