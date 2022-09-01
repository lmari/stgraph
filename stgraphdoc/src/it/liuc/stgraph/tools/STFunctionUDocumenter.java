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

import it.liuc.stgraph.userfun.UserFunReader;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.lowagie.text.Chapter;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfWriter;

/**
 * User function documenter class.
 */
public class STFunctionUDocumenter {
	/** The DOC_VERSION. */
	private static final String DOC_VERSION = "23.2.16";

	/** The language prefix. */
	private static final String[] langPref = new String[] { "_en", "_it" };
	/** The file prefix 3. */
	private static final String filePrefix3 = "../stgraph/docs/";
	/** The file postfix 2 pdf. */
	private static final String filePostfix2Pdf = ".pdf";
	/** The document. */
	private static Document document;
	/** Plain font. */
	private static Font plainFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.NORMAL);
	/** Title1 font. */
	private static Font h1Font = FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD);
	/** Title2 font. */
	private static Font h2Font = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
	/** Code font. */
	private static Font codeFont = FontFactory.getFont(FontFactory.COURIER, 10);

	/** Version header. */
	String[] versionHeader = new String[] { "Version", "Versione" };
	/** Legend header. */
	String[] legendHeader = new String[] {
			"Legend: A=array (or specifically: S=scalar; V=vector; M=matrix); E=expression",
			"Legenda: A=array (o specificamente: S=scalare; V=vettore; M=matrice); E=espressione" };
	/** User-defined functions. */
	String[] ufunctions = new String[] { "Functions2", "Funzioni2" };
	/** Organized functions. */
	String[] orgFunctions = new String[] {
			"Functions listed by functional categories",
			"Funzioni elencate per categorie funzionali" };

	/** User def functions. */
	String[] usrDefFun = new String[] { "User-defined functions",
			"Funzioni definite dall'utente" };
	/** User def header. */
	String[] usrDefHeader = new String[] {
			"These functions are written in STEL and are stored in the files datafiles/*.stf in the archive stgraphfun.jar",
			"Queste funzioni sono scritte in STEL e sono memorizzate nei file datafiles/*.stf nell'archivio stgraphfun.jar" };

	/** Number of supported languages. */
	private static final int numLocales = 2;
	/** The language locale. */
	private static final String[] locale = new String[] { "en", "it" }; //actually unused: descriptions are assumed there, in correct order, and started with a two character locale id
	/**
	 * The descriptions of the custom functions: a complicated structure,
	 * locales x chapters x functions, array of maps of maps, as follows: c[0] :
	 * en data c[0].get("c1") : en chapter c1 c[0].get("c1").get("f1") : en
	 * chapter c1 function f1
	 */
	@SuppressWarnings("unchecked")
	private static transient HashMap<String, HashMap<String, String>> customFunDescr[] = new HashMap[numLocales];

	private static final String BLANK = "";
	private static final String SPACE = " ";
	private static final String NEWLINE = "\n";


	/**
	 * Starter method.
	 *
	 * @param args the args
	 */
	public static void main(final String[] args) {
		new STFunctionUDocumenter();
	}


	/**
	 * Class constructor.
	 */
	public STFunctionUDocumenter() {
		System.out.print("\n*** STGraph ***");
		System.out.print("\n*** Generator of documentation for user-defined functions ***");
		prepareCustomFunctions();
		for(int i = 0; i < numLocales; i++) {
			document = new Document();
			try {
				PdfWriter.getInstance(document, new FileOutputStream(filePrefix3 + ufunctions[i] + langPref[i] + filePostfix2Pdf));
			} catch (Exception e) {
				System.err.println("2b: " + e.getMessage());
			}
			document.open();
			makeUFunctionFiles(i);
			document.close();
		}
		System.out.println("\nOk");
	}


	/**
	 * Generate the files documenting each user-defined function.
	 *
	 * @param lang language pointer
	 */
	private void makeUFunctionFiles(final int lang) {
		System.out.print("\nGenerating the file for " + locale[lang]);
		Paragraph p;
		try {
			Chapter c = new Chapter(new Paragraph("STGraph - " + usrDefFun[lang], h1Font), 1);
			c.setNumberDepth(0);
			c.add(new Paragraph("[" + versionHeader[lang] + SPACE + DOC_VERSION + "]", plainFont));
			c.add(new Paragraph(usrDefHeader[lang], plainFont));
			c.add(Chunk.NEWLINE);
			c.add(Chunk.NEWLINE);
			c.add(Chunk.NEWLINE);

			ArrayList<String> chapters = new ArrayList<String>();
			for(String s : customFunDescr[lang].keySet()) { chapters.add(s); }
			Collections.sort(chapters);
			for(String chapter : chapters) {
				p = new Paragraph(BLANK, plainFont);
				p.add(new Chunk(chapter, h2Font));
				c.add(p);
				HashMap<String, String> thechapter = customFunDescr[lang].get(chapter);
				ArrayList<String> functions = new ArrayList<String>();
				for(String s : thechapter.keySet()) { functions.add(s); }
				Collections.sort(functions);
				for(String function : functions) {
					p = new Paragraph(BLANK, plainFont);
					p.add(new Paragraph(setParagraphForPDF2(new Phrase(thechapter.get(function)))));
					c.add(p);
					c.add(Chunk.NEWLINE);
				}
				c.add(Chunk.NEWLINE);
				c.add(Chunk.NEWLINE);
				c.add(Chunk.NEWLINE);
			}
			document.add(c);
		} catch (Exception e) {
			System.err.println("8: " + e.getMessage());
			return;
		}
	}


	/**
	 * Helper method: take a phrase, scan its chunks and convert the tags in their text
	 * into suitable chunks; finally return the modified phrase.
	 *
	 * @param in the phrase to be dealt with
	 *
	 * @return the generated paragraph
	 */
	private static Phrase setParagraphForPDF2(final Phrase in) {
		String c1 = "<code>";
		String c2 = "</code>";
		Chunk c;
		String t;
		int i;
		Phrase f = in;
		Phrase out = new Phrase(BLANK, plainFont);
		for(Object o : f.getChunks()) {
			c = (Chunk) o;
			t = c.getContent();
			if((i = t.indexOf(c1)) == -1) {
				out.add(t);
			} else {
				int i2 = 0;
				String tt = t;
				while (i != -1) {
					i2 = tt.indexOf(c2, i + 6);
					out.add(tt.substring(0, i));
					out.add(new Chunk(tt.substring(i + 6, i2), codeFont));
					tt = tt.substring(i2 + 7);
					i = tt.indexOf(c1);
				}
				out.add(tt);
			}
		}
		// finally...
		return out;
	}


	/**
	 * Read the definition of the custom functions from the corresponding files,
	 * and generate the related documentation.
	 */
	private final void prepareCustomFunctions() {
		File[] files = UserFunReader.getFiles();
		if(files == null || files.length == 0) { return; }
		for(int i = 0; i < numLocales; i++) { customFunDescr[i] = new HashMap<String, HashMap<String, String>>(files.length); }
		for(File file : files) { setCustomFunctionsHelper(file.getName()); }
	}


	/**
	 * Helper for the <code>setCustomFunctions</code> method.
	 */
	private final void setCustomFunctionsHelper(final String fileName) {
		Properties functionProps = new Properties();
		try {
			InputStream is = UserFunReader.getFile(fileName);
			String s = stream2String(is);
			s = s.replaceAll("\\&", "\\&amp;");
			s = s.replaceAll("\\&amp;lt;", "\\&lt;");
			is = string2Stream(s);
			functionProps.loadFromXML(is);
		} catch (Exception e) {
			System.err.println("Error in reading functions from " + fileName + ": " + e.getMessage());
			return;
		}
		if(functionProps.size() == 0) { return; }
		String locchap[];
		String chap;
		String id;
		String def;
		int pos;
		String locdesc[];
		String desc;
		for(Map.Entry<Object, Object> entry : functionProps.entrySet()) {
			String s = (String) entry.getKey();
			int i = s.indexOf("|");
			if(i == -1) {
				System.err.println("\nError in reading function " + s + " from " + fileName);
				return;
			}
			locchap = s.substring(0, i).trim().split("__");
			if(locchap.length != numLocales) {
				System.err.println("\nError in reading function " + s + " from " + fileName + ": wrong number of localized chapter titles");
				return;
			}
			id = s.substring(i + 1).trim();
			def = (String) entry.getValue();
			if((pos = def.indexOf("//")) == -1) {
				System.err.println("\nError in reading function " + s + " from " + fileName + ": no documentation");
				return;
			}
			locdesc = def.substring(pos + 2).trim().split("__");
			if(locdesc.length != numLocales) {
				System.err.println("\nError in reading function " + s + " from " + fileName + ": wrong number of localized descriptions");
				return;
			}
			for(int j = 0; j < numLocales; j++) {
				chap = locchap[j].trim().substring(3); // locale ids are assumed to be 3 char (e.g.: "en:") long...
				if(!customFunDescr[j].containsKey(chap)) { customFunDescr[j].put(chap, new HashMap<String, String>()); }
				desc = locdesc[j].trim().substring(3); // as above
				customFunDescr[j].get(chap).put(id, desc);
			}
		}
	}


	/**
	 * Convert the specified stream to a string.
	 *
	 * @param is the stream to be read
	 *
	 * @return string
	 *
	 * @throws IOException if the stream cannot be read
	 */
	public static String stream2String(InputStream is) throws IOException {
		if(is == null) { return BLANK; }
		StringBuilder sb = new StringBuilder();
		String l;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			while ((l = br.readLine()) != null) { sb.append(l).append(NEWLINE); }
		} finally {
			is.close();
		}
		return sb.toString();
	}


	/**
	 * Convert the specified string to a stream.
	 *
	 * @param s the string to be read
	 *
	 * @return stream
	 *
	 * @throws Exception if the stream cannot be encoded
	 */
	public static InputStream string2Stream(String s) throws Exception {
		return new ByteArrayInputStream(s.getBytes());
	}

}
