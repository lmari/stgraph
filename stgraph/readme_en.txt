STGraph - README

STGraph is Copyright 2004-2021, Luca Mari and is licensed under the GNU GPL 2.0.

STGraph is an application for creating, editing, and executing
models of dynamical systems according to the state-variable approach
to Systems Theory.

STGraph is based on several open source libraries and is itself
open source software. Please see the file licenses/license.txt.

STGraph has been used for several modeling projects, mainly in the context
of the course 'Systems Theory' in the Management Engineering curriculum
of the Universita' Cattaneo - LIUC (http://www.liuc.it), Castellanza, Italy.

Many students have given useful suggestions (and found bugs...)
to make STGraph better: thank you all.
Giuseppe Catalfamo and Sara Sterlocchi have been critical users of STGraph:
its development has benefited from their expert opinions.

******************************************************
The latest version of STGraph and of its documentation
can be freely downloaded from the site:
	http://research.liuc.it/luca.mari/stgraph
The user interface of STGraph currently supports
EN and IT languages.
******************************************************

The downloaded zip files must be unzipped with full pathnames,
in a folder whose pathname does not contain spaces or special characters.
The directory will contain the files:
- stgraph.sh: run STGraph in Linux
- stgraph.command: run STGraph in Apple MacOS
- stgraph.exe and stgraph.bat: run STGraph in MSWindows
- readme_*.txt: this file
and the directories:
- licenses: information on the open source license of STGraph
	and the used libraries
- lib: the program and its libraries
- docs: the documentation

When executing stgraph.sh /  stgraph.app / stgraph.exe / stgraph.bat,
the application should start the Java VM and then open its main window:
in this case, enjoy STGraph!

Otherwise, you should check that a Java Virtual Machine (JRE 1.6+)
is installed on your system:
open a terminal / command shell and type:
	java -version
If a 'File not found' error is displayed or the installed JRE
is older than 1.6, then you must install a JRE, which can be
freely downloaded from:
	http://java.com/download
(select to download Java SE, either the JRE or the JDK).
Finally, be sure that the bin directory of the installed JRE
has been added to the system path (or manually edit
stgraph.sh / stgraph.bat to introduce the full pathname
to the Java VM interpreter).

Any comments, suggestions, ... on STGraph are welcome:
please mail them to lmari@liuc.it
