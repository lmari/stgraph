package org.nfunk.jep.type;

import org.nfunk.jep.Node;


/** Deal with functions.
 */
public class Function {
	private Node expression;


	/** Default constructor.
	 */
	public Function(Node _expression) {
		expression = _expression;
	}


	/** Return the expression that defines the function.
	 * @return expression
	 */
	public Node getExpression() { return expression; }
	
	
	public String toString() {
		return expression.toString();
	}

}
