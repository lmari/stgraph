/*****************************************************************************

JEP 2.4.0, Extensions 1.1.0
      June 13 2006
      (c) Copyright 2006, Nathan Funk and Richard Morris
      See LICENSE-*.txt for license information.

*****************************************************************************/
package org.nfunk.jep.function;

import java.lang.Math;
import java.util.*;
import org.nfunk.jep.*;
import org.nfunk.jep.type.*;

public class CosineH extends PostfixMathCommand
{
	public CosineH()
	{
		numberOfParameters = 1;
	}

	public void run(Stack inStack)
		throws ParseException
	{
		checkStack(inStack);// check the stack
		Object param = inStack.pop();
		inStack.push(cosh(param));//push the result on the inStack
		return;
	}

	public Object cosh(Object param)
		throws ParseException
	{
		if (param instanceof Complex)
		{
			return ((Complex)param).cosh();
		}
		else if (param instanceof Number)
		{
			double value = ((Number)param).doubleValue();
			return Double.valueOf((Math.exp(value) + Math.exp(-value))/2);
		}

		throw new ParseException("Invalid parameter type");
	}

}
