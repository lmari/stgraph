#!/bin/bash

# This script regenerates the pdf documentation, stored in the ./docs directory,
# of the user-defined functions in ./fun_lib

java -classpath "./:./lib/iText-2.1.4.jar:./lib/stgraph.jar" it.liuc.stgraph.tools.STFunctionUDocumenter
