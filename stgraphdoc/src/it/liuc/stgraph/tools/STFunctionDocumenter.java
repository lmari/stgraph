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

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;

import com.lowagie.text.Anchor;
import com.lowagie.text.Chapter;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Section;
import com.lowagie.text.pdf.PdfWriter;

import jxl.Sheet;
import jxl.Workbook;


/**
 * Function documenter class.
 */
public class STFunctionDocumenter {
	/** Last row. */
	private static final int lastRow = 93; // in the spreadsheet, last index row - 1
	/** The file name. */
	private static final String fileName = "functions.xls";
	/** The DOC_VERSION. */
	private static final String DOC_VERSION = "23.2.16";

	/** The language prefix. */
	private static final String[] langPref = new String[] {"_en", "_it"};
	/** The workbook. */
	private static Workbook workbook;
	/** The file prefix 0. */
	private static final String filePrefix0 = "../stgraph/doc/templates/";
	/** The file prefix 1. */
	private static final String filePrefix1 = "../stgraph/src/datafiles/";
	/** The file prefix 2. */
	private static final String filePrefix2 = "../stgraph/src/datafiles/fun/";
	/** The file prefix 3. */
	private static final String filePrefix3 = "../stgraph/docs/";
	/** The file postfix 1. */
	private static final String filePostfix1 = ".properties";
	/** The file postfix 2 txt. */
	private static final String filePostfix2Txt = ".txt";
	/** The file postfix 2 pdf. */
	private static final String filePostfix2Pdf = ".pdf";
	/** The document. */
	private static Document document;
	/** Plain font. */
	private static Font plainFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.NORMAL);
	/** Bold font. */
	private static Font boldFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLD);
	/** Link font. */
	private static Font linkFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.UNDERLINE, new Color(0, 0, 255));
	/** Title1 font. */
	private static Font h1Font = FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD);
	/** Title2 font. */
	private static Font h2Font = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
	/** Code font. */
	private static Font codeFont = FontFactory.getFont(FontFactory.COURIER, 10);
	/** Meta-char corresponding to the HTML CODE tag. */
	private final static String META_CODE = "@0";
	/** Meta-char corresponding to the HTML A tag. */
	private final static String META_A = "@1";
	/** Meta-char corresponding to the HTML BR tag. */
	private final static String META_BR = "@2";

	/** Group header. */
	String[][][] groupHeader = new String[][][] {
		{
			{"op", "an operator", "Operators"},
			{"mat", "a mathematical function", "Mathematical functions"},
			{"stat", "a statistical function", "Statistical functions"},
			{"control", "a control function", "Control functions"},
			{"vector", "an array function", "Array functions"}
		}, {
			{"op", "un operatore", "Operatori"},
			{"mat", "una funzione matematica", "Funzioni matematiche"},
			{"stat", "una funzione statistica", "Funzioni statistiche"},
			{"control", "una funzione di controllo", "Funzioni di controllo"},
			{"vector", "una funzione per array", "Funzioni per array"}
		}
	};
	/** Member header. */
	String[][][] categoryHeader = new String[][][] {
		{
			{"monadic", "a monadic polymorphic function", "Monadic polymorphic functions / operators"},
			{"diadic", "a diadic polymorphic function", "Diadic polymorphic functions / operators"},
			{"boolean", "a boolean operator", "Boolean operators"},
			{"distrib", "a statistical distribution function", "Polymorphic functions handling statistical distributions"},
			{"interp", "an interpolation function", "Interpolation functions"}
		}, {
			{"monadic", "una funzione monadica polimorfa", "Funzioni / operatori monadici polimorfi"},
			{"diadic", "una funzione diadica polimorfa", "Funzioni / operatori diadici polimorfi"},
			{"boolean", "un operatore booleano", "Operatori booleani"},
			{"distrib", "una funzione di distribuzione statistica", "Funzioni polimorfe per la gestione di distribuzioni statistiche"},
			{"interp", "una funzione di interpolazione", "Funzioni di interpolazione"}
		}
	};
	/** Version header. */
	String[] versionHeader = new String[] {"Version", "Versione"};
	/** Legend header. */
	String[] legendHeader = new String[] {
		"Legend: A=array (or specifically: S=scalar; V=vector; M=matrix); E=expression",
		"Legenda: A=array (o specificamente: S=scalare; V=vettore; M=matrice); E=espressione"
	};
	/** System def functions. */
	String[] sysDefFun = new String[] {"System defined functions", "Funzioni di sistema"};
	/** To list. */
	String[] toList = new String[] {"To the list of the available documentation", "All'elenco dei documenti disponibili"};
	/** See also. */
	String[] seeAlso = new String[] {"See also", "Vedi anche"};
	/** Is. */
	String[] is = new String[] {"is", "e'"};
	/** Format. */
	String[] format = new String[] {"Format", "Formato"};
	/** Constraints. */
	String[] constraints = new String[] {"Constraints", "Vincoli"};
	/** Description. */
	String[] description = new String[] {"Description", "Descrizione"};
	/** Exceptions. */
	String[] exceptions = new String[] {"Exceptions", "Eccezioni"};
	/** Functions. */
	String[] functions = new String[] {"Functions", "Funzioni"};
	/** User-defined functions. */
	String[] ufunctions = new String[] {"Functions2", "Funzioni2"};
	/** Organized functions. */
	String[] orgFunctions = new String[] {"Functions listed by functional categories", "Funzioni elencate per categorie funzionali"};

	/** User def functions. */
	String[] usrDefFun = new String[] {"User-defined functions", "Funzioni definite dall'utente"};
	/** User def header. */
	String[] usrDefHeader = new String[] {
		"These functions are written in STEL and are stored in the files *.stf in the directory fun_lib",
		"Queste funzioni sono scritte in STEL e sono memorizzate nei file *.stf nella cartella fun_lib"
	};

	/** Readme filename. */
	String[] readme = new String[] {"readme_en.txt", "readme_it.txt"};

	/** Number of supported languages. */
	private static final int numLocales = 2;

	private static final String BLANK = "";
	private static final String COLON = ":";
	private static final String SPACE = " ";


	/**
	 * Starter method.
	 *
	 * @param args the args
	 */
	public static void main(final String[] args) {
		new STFunctionDocumenter();
		new STFunctionUDocumenter();
	}


	/**
	 * Class constructor.
	 */
	public STFunctionDocumenter() {
		System.out.print("\n*** STGraph ***");
		System.out.print("\n*** Generator of documentation for system functions ***\n");

		File directory = new File(filePrefix2);
	    if (!directory.exists()) { directory.mkdir(); }

		for(int i = 0; i < numLocales; i++) {
			try { workbook = Workbook.getWorkbook(new File(filePrefix0 + fileName)); }
			catch (Exception e) { System.err.println("1: " + e.getMessage()); return; }
			if(workbook == null) { System.err.println("File not found"); return; }
			document = new Document();
			try {
				PdfWriter.getInstance(document, new FileOutputStream(filePrefix3 + functions[i] + langPref[i] + filePostfix2Pdf));
			} catch (Exception e) { System.err.println("2: " + e.getMessage()); }
			document.open();
			makeGroupList(i);
			makeCategoryList(i);
			makeFunctionFiles(i);
			document.close();
		}
		System.out.println("\nOk");
	}


	/**
	 * Generate the file listing the functions / operators organized by groups.
	 *
	 * @param lang language pointer
	 */
	private void makeGroupList(final int lang) {
		HashMap<String, String> map;
		PrintWriter file = null;
		PrintWriter file2 = null;
		String fn;
		String t;

		System.out.println("Generating group list file");

		// .properties version
		try { file = new PrintWriter(new FileWriter(filePrefix1 + "functions" + langPref[lang] + filePostfix1), true);
		} catch (Exception e) {
			System.err.println("3: " + e.getMessage());
			file.close();
			return;
		}
		file.println("#<a href=\"" + readme[lang] + "\">" + toList[lang] + "</a>");
		file.println("#<h2>STGraph - " + sysDefFun[lang] + "</h2>");
		file.println("#" + legendHeader[lang]);
		file.println(BLANK);
		for(int i = 0; i < groupHeader[lang].length; i++) {
			file.println("#<a href=\"fun/_" + groupHeader[lang][i][0] + langPref[lang] + filePostfix2Txt + "\">" + groupHeader[lang][i][2] + "</a>");
		}
		file.println(BLANK);
		file.println("#" + seeAlso[lang] + COLON);
		for(int i = 0; i < categoryHeader[lang].length; i++) {
			file.println("#<a href=\"fun/_" + categoryHeader[lang][i][0] + langPref[lang] + filePostfix2Txt + "\">" + categoryHeader[lang][i][2] + "</a>");
		}
		for(int i = 0; i < groupHeader[lang].length; i++) {
			try { file2 = new PrintWriter(new FileWriter(filePrefix2 + "_" + groupHeader[lang][i][0] + langPref[lang] + filePostfix2Txt), true);
			} catch (Exception e) {
				System.err.println("4: " + e.getMessage());
				file.close();
				file2.close();
				return;
			}
			file.println("#<b>" + groupHeader[lang][i][2] + "</b>");
			file2.println("#<h2>STGraph - " + groupHeader[lang][i][2] + "</h2>");
			for(int j = 1; j <= lastRow; j++) {
				map = getData(lang, j);
				if(map.get("Group").equals(groupHeader[lang][i][0])) {
					t = map.get("Format") + "</a>: <i>[" + map.get("ShortFormat") + "]</i> " + setStringForHTML(lang, map.get("Description"));
					fn = map.get("FunctionName");
					if(!fn.startsWith("op") && fn.indexOf("-") == -1) { // default (standard) case
						file.println(fn + " = " + "<a href=\"fun/" + fn + langPref[lang] + filePostfix2Txt + "\">" + t);
						file2.println(fn + " = " + "<a href=\"fun/" + fn + langPref[lang] + filePostfix2Txt + "\">" + t);
					} else if(fn.indexOf("-") != -1) { // variadic functions, here assumed in just two versions
						file.print(fn.substring(0, fn.length() - 2) + " = " + "<a href=\"fun/" + fn +  langPref[lang] + filePostfix2Txt + "\">" + t);
						file2.print(fn.substring(0, fn.length() - 2) + " = " + "<a href=\"fun/" + fn +  langPref[lang] + filePostfix2Txt + "\">" + t);
						file.print(" <br> ");
						file2.print(" <br> ");
						j++; // bad practice, of course...
						map = getData(lang, j);
						t = map.get("Format") + "</a>: <i>[" + map.get("ShortFormat") + "]</i> " + setStringForHTML(lang, map.get("Description"));
						fn = map.get("FunctionName");
						file.println("<a href=\"fun/" + fn + langPref[lang] + filePostfix2Txt + "\">" + t);
						file2.println("<a href=\"fun/" + fn + langPref[lang] + filePostfix2Txt + "\">" + t);
					} else { // operators
						file.println(map.get("AlternateFN") + " = " + "<a href=\"fun/" + fn + langPref[lang] + filePostfix2Txt + "\">" + t);
						file2.println(map.get("AlternateFN") + " = " + "<a href=\"fun/" + fn + langPref[lang] + filePostfix2Txt + "\">" + t);
					}
				}
			}
			file2.println(BLANK);
			file2.println(BLANK);
			file2.println(BLANK);
			file2.println("<hr>");
			file2.println("<a href=\"functions_en.properties\">List of functions</a>");
			file2.close();
		}
		file.close();

		// .pdf and .html versions
		Paragraph p;
		List l;

		try {
			Chapter c = new Chapter(new Paragraph("STGraph - " + sysDefFun[lang], h1Font), 1);
			c.setNumberDepth(0);
			c.add(new Paragraph("[" + versionHeader[lang] + SPACE + DOC_VERSION + "]", plainFont));
			c.add(new Paragraph(legendHeader[lang], plainFont));
			c.add(Chunk.NEWLINE);
			c.add(Chunk.NEWLINE);
			c.add(Chunk.NEWLINE);

			l = new List(false, 10);
			for(int i = 0; i < groupHeader[lang].length; i++) {
				p = new ListItem(BLANK, plainFont);
				p.add(setSource(groupHeader[lang][i][2], groupHeader[lang][i][0], linkFont));
				l.add(p);
			}
			c.add(l);

			c.add(Chunk.NEWLINE);
			c.add(Chunk.NEWLINE);
			c.add(Chunk.NEWLINE);

			c.add(new Paragraph(seeAlso[lang] + COLON, plainFont));
			l = new List(false, 10);
			for(int i = 0; i < categoryHeader[lang].length; i++) {
				p = new ListItem(BLANK, plainFont);
				p.add(setSource(categoryHeader[lang][i][2], categoryHeader[lang][i][0], linkFont));
				l.add(p);
			}
			c.add(l);

			c.add(Chunk.NEXTPAGE);

			for(int i = 0; i < groupHeader[lang].length; i++) {
				p = new Paragraph(BLANK, boldFont);
				p.add(setTarget(groupHeader[lang][i][2], groupHeader[lang][i][0], boldFont));
				Section s = c.addSection(p);
				s.setNumberDepth(0);

				for(int j = 1; j <= lastRow; j++) {
					map = getData(lang, j);
					if(map.get("Group").equals(groupHeader[lang][i][0])) {
						fn = map.get("FunctionName");
						p = new Paragraph(BLANK, plainFont);
						p.add(setSource(fixFN(map.get("Format")), fn, linkFont));
						p.add(" [" + map.get("ShortFormat") + "] ");
						p.add(setParagraphForPDF(new Phrase(map.get("Description"))));
						s.add(p);
						if(fn.indexOf("-") != -1) { // variadic functions, here assumed in just two versions
							j++; // bad practice, of course...
							map = getData(lang, j);
							p = new Paragraph(BLANK, plainFont);
							p.add(setSource(fixFN(map.get("Format")), fn, linkFont));
							p.add(" [" + map.get("ShortFormat") + "] ");
							p.add(setParagraphForPDF(new Phrase(map.get("Description"))));
							s.add(p);
						}
					}
				}
				s.add(Chunk.NEWLINE);
				s.add(Chunk.NEWLINE);
			}
			c.add(Chunk.NEXTPAGE);
			document.add(c);
		} catch (Exception e) { System.err.println("5: " + e.getMessage()); return; }
	}


	/**
	 * Generate the files listing the functions / operators organized by membership.
	 *
	 * @param lang language pointer
	 */
	private void makeCategoryList(final int lang) {
		HashMap<String, String> map;
		BufferedReader fileIn = null;
		PrintWriter file = null;
		String fn;
		String t;
		Paragraph p;

		System.out.print("Generating member list files (/" + categoryHeader[lang].length + ") ");

		Chapter c = new Chapter(new Paragraph("STGraph - " + orgFunctions[lang], h1Font), 1);
		c.setNumberDepth(0);
		try {
			for(int i = 0; i < categoryHeader[lang].length; i++) {
				System.out.print((i + 1) + " ");
				fileIn = new BufferedReader(new FileReader(filePrefix0 + "_" + categoryHeader[lang][i][0] + langPref[lang] + filePostfix2Txt));
				file = new PrintWriter(new FileWriter(filePrefix2 + "_" + categoryHeader[lang][i][0] + langPref[lang] + filePostfix2Txt), true);
				
				file.println("<h2>STGraph - " + categoryHeader[lang][i][2] + "</h2>");
				p = new Paragraph(BLANK, h2Font);
				p.add(setTarget("STGraph - " + categoryHeader[lang][i][2], categoryHeader[lang][i][0], h2Font));
				Section s = c.addSection(p);
				s.setNumberDepth(0);

				while(fileIn.ready()) {
					t = fileIn.readLine();
					file.println(setStringForHTML(lang, t));
					s.add(setParagraphForPDF(new Phrase(t)));
					s.add(Chunk.NEWLINE);
				}

				s.add(Chunk.NEWLINE);
				for(int j = 1; j <= lastRow; j++) {
					map = getData(lang, j);
					if(map.get("MemberOf").indexOf(categoryHeader[lang][i][0]) != -1) {
						fn = map.get("FunctionName");
						file.println("<a href=\"fun/" + fn + langPref[lang] + filePostfix2Txt + "\">" + map.get("Format") + "</a>");

						p = new Paragraph(BLANK, plainFont);
						p.add(setSource(fixFN(map.get("Format")), fn, linkFont));
						s.add(p);
					}
				}
				s.add(Chunk.NEWLINE);
				s.add(Chunk.NEWLINE);
				file.println("<hr>");
				file.println("<a href=\"functions" + langPref[lang] + ".properties\">List of functions</a>");
				fileIn.close();
				file.close();
			}
			c.add(Chunk.NEXTPAGE);
			document.add(c);
		} catch (Exception e) { System.err.println("6: " + e.getMessage()); return; }
	}


	/**
	 * Generate the files documenting each function / operator.
	 *
	 * @param lang language pointer
	 */
	private void makeFunctionFiles(final int lang) {
		HashMap<String, String> map;
		BufferedReader fileIn = null;
		PrintWriter file = null;
		String fn;
		String fn2;
		String fn3;
		String t;
		String[] tt;
		Paragraph p;

		System.out.print("\nGenerating fun file (/" + lastRow + ") ");

		Chapter c = new Chapter(new Paragraph("STGraph - " + functions[lang], h1Font), 1);
		c.setNumberDepth(0);
		try {
			for(int i = 1; i <= lastRow; i++) {
				System.out.print(i + " ");
				map = getData(lang, i);

				fn = map.get("FunctionName");
				file = new PrintWriter(new FileWriter(filePrefix2 + fn + langPref[lang] + filePostfix2Txt), true);

				fn2 = (fn.startsWith("op") ? fn.substring(2) : fn);
				fn3 = fn.startsWith("op") ? fn.substring(2) : fn;
				file.println("<h2>STGraph - " + fn2 + "</h2>");
				file.println("<u>" + format[lang] + "</u>: <code>" + map.get("Format") + "</code>");
				file.println("<u>" + constraints[lang] + "</u>: " + setStringForHTML(lang, map.get("Constraints")));
				file.println("<u>" + description[lang] + "</u>: " + setStringForHTML(lang, map.get("Description")));

				p = new Paragraph(BLANK, plainFont);
				p.add(setTarget(fn2, fn, h2Font));
				c.add(p);
				p = new Paragraph(BLANK, plainFont);
				p.add(new Chunk(format[lang] + ": ", boldFont));
				p.add(new Chunk(map.get("Format"), codeFont));
				c.add(p);
				p = new Paragraph(BLANK, plainFont);
				p.add(new Chunk(constraints[lang] + ": ", boldFont));
				p.add(setParagraphForPDF(new Phrase(map.get("Constraints"))));
				c.add(p);
				p = new Paragraph(BLANK, plainFont);
				p.add(new Chunk(description[lang] + ": ", boldFont));
				p.add(setParagraphForPDF(new Phrase(map.get("Description"))));
				c.add(p);
				t = map.get("Notes");
				if(!t.equals("no")) {
					file.println(BLANK);
					if(t.equals("template")) {
						try {
							fileIn = new BufferedReader(new FileReader(filePrefix0 + "_" + fn + langPref[lang] + filePostfix2Txt));
						} catch (Exception e) {
							System.err.println("Error in opening the input file");
							file.close(); 
							fileIn.close(); 
							return;
						}
						try {
							while(fileIn.ready()) {
								t = fileIn.readLine();
								file.println(setStringForHTML(lang, t));
								c.add(new Paragraph(setParagraphForPDF(new Phrase(t))));
							}
						} catch (Exception e) {
							System.err.println("Error in copying the input file to the output file");
							file.close();
							fileIn.close(); 
							return;
						}
					} else {
						file.println(setStringForHTML(lang, t));
						c.add(new Paragraph(setParagraphForPDF(new Phrase(t))));
					}
				}

				t = map.get("Group");
				if(t.length() > 0) {
					file.print("<br><code>" + fn3 + "</code>" + SPACE + is[lang] + SPACE);
					file.print(getGroupDescription(lang, t));
					for(int j = 0; j < groupHeader[lang].length; j++) {
						if(t.equals(groupHeader[lang][j][0])) {
							c.add(Chunk.NEWLINE);
							p = new Paragraph(BLANK, plainFont);
							p.add(new Chunk(fn3, codeFont));
							p.add(SPACE + is[lang] + SPACE);
							p.add(setSource(groupHeader[lang][j][1], groupHeader[lang][j][0], linkFont));
							c.add(p);
						}
					}
				}

				t = map.get("MemberOf");
				if(t.length() > 0) {
					file.print("<br><code>" + fn3 + "</code>" + SPACE + is[lang] + SPACE);
					p = new Paragraph(BLANK, plainFont);
					p.add(new Chunk(fn3, codeFont));
					p.add(SPACE + is[lang] + SPACE);
					if(t.indexOf(",") == -1) {
						file.print(getCategoryDescription(lang, t));
						for(int k = 0; k < categoryHeader[lang].length; k++) {
							if(t.equals(categoryHeader[lang][k][0])) {
								p.add(setSource(categoryHeader[lang][k][1], categoryHeader[lang][k][0], linkFont));
								c.add(p);
							}
						}
					} else {
						tt = t.split(",");
						for(int j = 0; j < tt.length; j++) {
							file.print(getCategoryDescription(lang, tt[j]));
							for(int k = 0; k < categoryHeader[lang].length; k++) {
								if(tt[j].equals(categoryHeader[lang][k][0])) {
									p.add(setSource(categoryHeader[lang][k][1], categoryHeader[lang][k][0], linkFont));
								}
							}
							if(j < tt.length - 1) {
								file.print(", ");
								p.add(", ");
							}
						}
						c.add(p);
					}
				}

				file.println(BLANK);
				file.println(BLANK);
				file.println("<u>" + exceptions[lang] + "</u>" + COLON + SPACE + setStringForHTML(lang, map.get("Exceptions")));
				c.add(Chunk.NEWLINE);
				p = new Paragraph(BLANK, plainFont);
				p.add(new Chunk(exceptions[lang] + COLON + SPACE, boldFont));
				p.add(setParagraphForPDF(new Phrase(map.get("Exceptions"))));
				c.add(p);

				c.add(Chunk.NEWLINE);
				c.add(Chunk.NEWLINE);
				c.add(Chunk.NEWLINE);
				c.add(Chunk.NEWLINE);

				file.close();
			}
			document.add(c);
		} catch (Exception e) { System.err.println("7: " + e.getMessage()); return; }
	}


	/**
	 * Helper method: get the data describing a function.
	 *
	 * @param lang language pointer
	 * @param i row index
	 *
	 * @return the data
	 */
	private HashMap<String, String> getData(final int lang, final int i) {
		Sheet s = workbook.getSheet(lang);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("FunctionName", s.getCell(0, i).getContents());
		map.put("Format", s.getCell(1, i).getContents());
		map.put("Constraints", s.getCell(2, i).getContents());
		map.put("Description", s.getCell(3, i).getContents());
		map.put("Notes", s.getCell(4, i).getContents());
		map.put("Exceptions", s.getCell(5, i).getContents());
		map.put("MemberOf", s.getCell(6, i).getContents());
		map.put("Group", s.getCell(7, i).getContents());
		map.put("ShortFormat", s.getCell(8, i).getContents());
		map.put("AlternateFN", s.getCell(9, i).getContents());
		return map;
	}


	/**
	 * Helper method: get the group description.
	 *
	 * @param lang language pointer
	 * @param s group
	 *
	 * @return description
	 */
	private String getGroupDescription(final int lang, final String s) {
		for(int i = 0; i < groupHeader[lang].length; i++) {
			if(s.equals(groupHeader[lang][i][0])) { return "<a href=\"functions" + langPref[lang] + filePostfix1 + "\">" + groupHeader[lang][i][1] + "</a>"; }
		}
		return BLANK;
	}


	/**
	 * Helper method: get the category description.
	 *
	 * @param lang language pointer
	 * @param s category
	 *
	 * @return description
	 */
	private String getCategoryDescription(final int lang, final String s) {
		for(int i = 0; i < categoryHeader[lang].length; i++) {
			if(s.equals(categoryHeader[lang][i][0])) { return "<a href=\"fun/_" + categoryHeader[lang][i][0] + langPref[lang] + filePostfix2Txt + "\">" + categoryHeader[lang][i][1] + "</a>"; }
		}
		return BLANK;
	}


	/**
	 * Helper method: build and get an anchor from the specified elements.
	 *
	 * @param text the text
	 * @param link the link
	 * @param font the font
	 *
	 * @return the anchor
	 */
	private static Anchor setSource(final String text, final String link, final Font font) {
        Chunk c = new Chunk(text, font);
        c.setLocalGoto(link);
        Anchor a = new Anchor(c);
        a.setReference("#" + link);
        return a;
	}


	/**
	 * Helper method: build and get the target of a link from the specified elements.
	 *
	 * @param text the text
	 * @param link the link
	 * @param font the font
	 *
	 * @return the anchor
	 */
	private static Anchor setTarget(final String text, final String link, final Font font) {
        Chunk c = new Chunk(text, font);
        c.setLocalDestination(link);
        Anchor a = new Anchor(c);
        a.setName(link);
        return a;
	}


	/**
	 * Helper method: get the fixed version of a filename.
	 *
	 * @param fn the filename
	 *
	 * @return the string
	 */
	private static String fixFN(final String fn) {
		int i;
		if((i = fn.indexOf("&lt;")) == -1) { return fn; }
		return fn.substring(0, i) + "<" + fn.substring(i + 4);
	}


	/**
	 * Helper method: convert the meta-characters in the given string
	 * into the corresponding HTML tags and return the modified string.
	 *
	 * @param lang language pointer
	 * @param in the string to be dealt with
	 *
	 * @return the modified string
	 */
	private static String setStringForHTML(final int lang, final String in) {
		int i;
		// check CODE (as 2-diadic operator)
		String t = in;
		String out = BLANK;
		if((i = t.indexOf(META_CODE)) == -1) {
			out = t;
		} else {
			int i2 = 0;
			String tt = t;
			while(i != -1) {
				i2 = tt.indexOf(META_CODE, i + 2);
				out += tt.substring(0, i);
				out += "<code>" + tt.substring(i + 2, i2) + "</code>";
				tt = tt.substring(i2 + 2);
				i = tt.indexOf(META_CODE);
			}
			out += tt;
		}
		// check A (as 3-diadic operator)
		t = out;
		out = BLANK;
		if((i = t.indexOf(META_A)) == -1) {
			out = t;
		} else {
			int i2 = 0;
			int i3 = 0;
			String tt = t;
			while(i != -1) {
				i2 = tt.indexOf(META_A, i + 2);
				i3 = tt.indexOf(META_A, i2 + 2);
				out += tt.substring(0, i);
				out += "<a href=\"fun/_" + tt.substring(i + 2, i2) + langPref[lang] + filePostfix2Txt + "\">" + tt.substring(i2 + 2, i3) + "</a>";
				tt = tt.substring(i3 + 2);
				i = tt.indexOf(META_A);
			}
			out += tt;
		}
		// check BR (as 1-diadic operator)
		t = out;
		out = BLANK;
		if((i = t.indexOf(META_BR)) == -1) {
			out = t;
		} else {
			String tt = t;
			while(i != -1) {
				out += tt.substring(0, i) + "<br>";
				tt = tt.substring(i + 2);
				i = tt.indexOf(META_BR);
			}
			out += tt;
		}
		// finally...
		return out;
	}


	/**
	 * Helper method: take a phrase, scan its chunks and convert the meta-characters in their text
	 * into suitable chunks; finally return the modified phrase.
	 *
	 * @param in the phrase to be dealt with
	 *
	 * @return the generated paragraph
	 */
	private static Phrase setParagraphForPDF(final Phrase in) {
		Chunk c;
		String t;
		int i;
		// check CODE
		Phrase f = in;
		Phrase out = new Phrase(BLANK, plainFont);
		for(Object o : f.getChunks()) {
			c = (Chunk)o;
			t = c.getContent();
			if((i = t.indexOf(META_CODE)) == -1) {
				out.add(t);
			} else {
				int i2 = 0;
				String tt = t;
				while(i != -1) {
					i2 = tt.indexOf(META_CODE, i + 2);
					out.add(tt.substring(0, i));
					out.add(new Chunk(tt.substring(i + 2, i2), codeFont));
					tt = tt.substring(i2 + 2);
					i = tt.indexOf(META_CODE);
				}
				out.add(tt);
			}
		}
		// check A
		f = out;
		out = new Phrase(BLANK, plainFont);
		for(Object o : f.getChunks()) {
			c = (Chunk)o;
			t = c.getContent();
			if((i = t.indexOf(META_A)) == -1) {
				out.add(c);
			} else {
				int i2 = 0;
				int i3 = 0;
				String tt = t;
				while(i != -1) {
					i2 = tt.indexOf(META_A, i + 2);
					i3 = tt.indexOf(META_A, i2 + 2);
					out.add(tt.substring(0, i));
					out.add(setSource(tt.substring(i2 + 2, i3), tt.substring(i + 2, i2), linkFont));
					tt = tt.substring(i3 + 2);
					i = tt.indexOf(META_A);
				}
				out.add(tt);
			}
		}
		// check BR
		f = out;
		out = new Phrase(BLANK, plainFont);
		for(Object o : f.getChunks()) {
			c = (Chunk)o;
			t = c.getContent();
			if((i = t.indexOf(META_BR)) == -1) {
				out.add(c);
			} else {
				String tt = t;
				while(i != -1) {
					out.add(tt.substring(0, i));
					out.add(Chunk.NEWLINE);
					tt = tt.substring(i + 2);
					i = tt.indexOf(META_BR);
				}
				out.add(tt);
			}
		}
		// finally...
		return out;
	}

}
