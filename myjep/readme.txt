Readme for the STGraph-modified version of JEP

	http://www.singularsys.com/jep

Jep is a Java library for parsing and evaluating mathematical expressions.
While the current version is distributed under a close source license,
the version used and modified here was free open source software.

This package is distributed as an Eclipse project:

	http://www.eclipse.org/

and contains JEP 2.4 in a slightly modified version, with some extensions,
and a new custom grammar file, to adapt it to its specific usage for
the STGraph application:

	http://cetic.liuc.it/stgraph

Hence this IS NOT intended to be a fork of the original version,
whose unmodified license is included.

The list of changes follows.

Project contact:
Luca Mari (lmari@liuc.it)


*** List of changes with respect to JEP 2.4 ***

- org.nfunk.jep.Parser.java: modified by some changes in the grammar

- org.nfunk.jep.ParserConstants.java: modified because of the introduction of new symbols

- org.nfunk.jep.ParserTokenManager.java, method getNextToken(): modified to allow
	a looser handling of exceptions:
		substituted:
		     throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
		with:
		     throw new RuntimeException("" + curChar);

- org.nfunk.jep.ParseException.java: modified because of custom handling of error messages

- org.nfunk.jep.TokenMgrError.java: modified because of custom handling of error messages

- added the directory datafiles and the messages_*.properties (en and it)

- org.nfunk.jep.JEP.java:
--- substituted:
		private Node topNode;
	with:
		public Node topNode;
--- in the method addStandardFunctions(), whose operation is actually substituted by the dynamic handling by Spring,
commented out the function which are redefined in STGraph or could generate some problems (such as sum)
--- added the field msg and the methods setMessageBundle and getMessage
--- modified the addVariable(String, double) method: now it returns Number instead of Double, so it can be overridden in STInterpreter
--- commented the addVariable(String, double, double) method: its original version returns Complex, that does not extends Number;
implemented in STInterpreter returning a Number

- org.nfunk.jep.OperatorSet.java: modified to add the (reference to the) operators Concatenate, Decatenate, List2

- org.nfunk.jep.type.Matrix.java: added to operate with matrix as first-class data type together with Number and Vector

- org.nfunk.jep.type.Tensor.java: added to operate with tensor as first-class general data type,
and modified org.nfunk.jep.type.DoubleNumberFactory.java accordingly

- org.nfunk.jep.SimpleNode.java, org.nfunk.jep.Operator.java, org.nfunk.jep.Variable.java, org.nfunk.jep.function.List.java:
added the implementation of Serializable
