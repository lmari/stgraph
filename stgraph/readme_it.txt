STGraph - LEGGIMI

STGraph e' Copyright 2004-2021, Luca Mari e sotto licenza GNU GPL 2.0.

STGraph e' un'applicazione per creare, modificare ed eseguire
modelli di sistemi dinamici secondo l'approccio agli stati
della Teoria dei Sistemi.

STGraph e' basato su varie librerie open source ed e' esso stesso
software open source. Si veda il file licenses/license.txt.

STGraph e' stato usato per vari progetti di modellistica, soprattutto
nel corso 'Teoria dei Sistemi', corso di laurea in Ingegneria Gestionale,
Universita' Cattaneo - LIUC (http://www.liuc.it), Castellanza, Italia.

Molti studenti hanno fornito utili suggerimenti (e trovato bug...)
per rendere STGraph migliore: grazie a tutti.
Giuseppe Catalfamo e Sara Sterlocchi sono stati utenti critici di STGraph:
il suo sviluppo ha tratto beneficio dalla loro competenza.

**************************************************************
La piu' recente versione di STGraph e della sua documentazione
possono essere scaricate liberamente dal sito:
	http://research.liuc.it/luca.mari/stgraph
L'interfaccia utente di STGraph attualmente supporta
i linguaggi EN e IT.
**************************************************************

I file zip scaricati devono essere decompressi in una cartella
il cui percorso non deve contenere spazi o caratteri speciali,
e mantenendo i percorsi completi dei file.
La cartella conterra' i file:
- stgraph.sh: per eseguire STGraph in Linux
- stgraph.command: per eseguire STGraph in Apple MacOS
- stgraph.exe e stgraph.bat: per eseguire STGraph in MSWindows
- readme_*.txt: questo file
e le cartelle:
- licenses: informazioni sulla licenza open source di STGraph
	e delle librerie utilizzate
- lib: il programma e le sue librerie
- docs: la documentazione

Eseguendo stgraph.sh /  stgraph.app / stgraph.exe / stgraph.bat,
l'applicazione dovrebbe attivare la Java VM e quindi
aprire la sua finestra principale: in tal caso, buon lavoro!

Altrimenti, occorre controllare che una Java Virtual Machine (JRE 1.6+)
sia installata:
aprire un terminale / prompt dei comandi e digitare:
	java -version
Se si ottiene un errore 'File non trovato' o la JRE
e' precedente alla versione 1.6, occorre installare una JRE,
scariabile liberamente da:
	http://java.com/download
(scegliere di scaricare Java SE, JRE oppure JDK).
Infine, assicurarsi che la cartella bin del JRE installato
sia stata aggiunta ai percorsi di sistema (o modificare manualmente
stgraph.sh / stgraph.bat per introdurre il percorso completo
all'interprete Java VM).

Ogni commento, suggerimento, ... su STGraph e' benvenuto:
per favore inviarli via email a lmari@liuc.it
