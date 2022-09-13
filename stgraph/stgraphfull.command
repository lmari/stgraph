#!/bin/bash
# This script runs STGraph
cd $(dirname "$0");
echo "  _____ _______ _____                 _     ";
echo " / ____|__   __/ ____|               | |    ";
echo "| (___    | | | |  __ _ __ __ _ _ __ | |__  ";
echo " \___ \   | | | | |_ | '__/ _' | '_ \| '_ \ ";
echo " ____) |  | | | |__| | | | (_| | |_) | | | |";
echo "|_____/   |_|  \_____|_|  \__,_| .__/|_| |_|";
echo "                               | |          ";
echo "                               |_|          ";
java -Xms256m -Xmx2048m -classpath "./:./lib/commons-logging-1.1.1.jar:./lib/ekit.jar:./lib/ilf-gpl.jar:./lib/jep.jar:./lib/jgraph.jar:./lib/jxl.jar:./lib/nanoxml-2.2.3.jar:./lib/spring-beans.jar:./lib/spring-context.jar:./lib/spring-core.jar:./lib/stgraphfun.jar:./lib/surfaceplotter.jar:./lib/java-spacebrew.jar:./lib/json4processing.jar:./lib/WebSocket.jar:./lib/trident.jar:./lib/steel.jar:./lib/bgicons.jar:./lib/stgraph.jar" it.liuc.stgraph.STGraph
