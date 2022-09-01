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
package it.liuc.stgraph.fun;

import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphExec;
import it.liuc.stgraph.STGraphImpl;
import it.liuc.stgraph.STInterpreter;
import it.liuc.stgraph.node.STData;
import it.liuc.stgraph.node.STNode;

import java.util.Stack;
import java.util.Vector;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.type.Tensor;


/** Integration function. */
public class Integral extends STFunction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** The buffer. */
	private transient Object[] buffer = null;
	/** The index. */
	private static int index;


	/** Class constructor. */
	public Integral() { numberOfParameters = -1; }


	/** Run method.
	 * @param stack the stack
	 * @throws ParseException the parse exception */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final void run(final Stack stack) throws ParseException {
		checkStack(stack);
		if(curNumberOfParameters == 0 || curNumberOfParameters > 2) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_NUMBER"), STData.VALUETYPE_SCALAR)); //$NON-NLS-1$
			return;
		}
		STNode node = STGraph.getSTC().getCurrentlyComputedNode();
		if(!node.isState()) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.ONLY_IN_STATES"), STData.VALUETYPE_SCALAR)); //$NON-NLS-1$
			return;
		}
		STGraphExec graph = STGraph.getSTC().getCurrentlyComputedGraph();
		Tensor param1 = null;
		Tensor param2 = null;
		if(curNumberOfParameters == 1) {
			try {
				param1 = (Tensor)stack.pop();
			} catch (Exception e) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STFunction.getType(param1))); //$NON-NLS-1$
				return;
			}
			param2 = graph.thethis;
		} else {
			try {
				param2 = (Tensor)stack.pop();
				param1 = (Tensor)stack.pop();
			} catch (Exception e) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STFunction.getType(param1))); //$NON-NLS-1$
				return;
			}
		}
		int order = param1.getOrder();
		if(order != param2.getOrder()) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STFunction.getType(param1))); //$NON-NLS-1$
			return;
		}
		
		/* EULER: a single phase **************************************************************************************
		 * integral(x) = this + x * timeD
		 */
		if(graph.integrationMethod == STGraphImpl.EULER) {
			if(order == 0) {
				stack.push(Tensor.newScalar(param2.getValue() + param1.getValue() * graph.getTimeD()));
				return;
			}
			Tensor result = new Tensor(param1.getDimensions());
			for(int i = 0; i < param1.getSize(); i++) { result.setScalar(Tensor.newScalar(param2.getScalar(i).getValue() + param1.getScalar(i).getValue() * graph.getTimeD()), i); }
			stack.push(result);
			return;
		}
		/* RK2: three phases ******************************************************************************************
		 * 	- save states
		 *  - phase 1: timeD = 3 * timeD / 4 and compute
		 *  - phase 2: compute only states, and integral store its argument in buffer
		 *  - restore states
		 *  - phase 3: compute, and integral = this + x * timeD / 3 + buffer * 2 * timeD / 3
		 */
		if(graph.integrationMethod == STGraphImpl.RK2) {
			if(graph.getIntegrationPhase() == 1) {
				buffer = new Object[1];
				buffer[0] = new Vector();
				index = -1;
				if(order == 0) {
					stack.push(Tensor.newScalar(param2.getValue() + param1.getValue() * graph.getTimeD()));
					return;
				}
				Tensor result = new Tensor(param1.getDimensions());
				for(int i = 0; i < param1.getSize(); i++) { result.setScalar(Tensor.newScalar(param2.getScalar(i).getValue() + param1.getScalar(i).getValue() * graph.getTimeD()), i); }
				stack.push(result);
				return;
			}
			if(graph.getIntegrationPhase() == 2) {
				((Vector)buffer[0]).add(param1);
				stack.push(param1); // dummy, just for having something pushed in the stack...
				return;
			}
			if(graph.getIntegrationPhase() == 3) {
				if(index < ((Vector)buffer[0]).size() - 1) { //TODO this test is here to deal the peculiar case of RK2 integration and integral() appears in a single condition of a if(), when the second step of the integration switches from condition=true to =false or viceversa, so that the integration phase = 2, which pushes the data into the buffer, would be called a number of times less than required
					index++;
				}
				double timeD13 = graph.getTimeD() / 3;
				double timeD23 = 2 * graph.getTimeD() / 3;
				if(order == 0) { //this + x * timeD / 3 + buffer * 2 * timeD / 3
					double v = ((Tensor)((Vector)buffer[0]).get(index)).getValue();
					stack.push(Tensor.newScalar(param2.getValue() + param1.getValue() * timeD13 + v * timeD23));
					return;
				}
				Tensor t = (Tensor)((Vector)buffer[0]).get(index);
				Tensor result = new Tensor(param1.getDimensions());
				for(int i = 0; i < param1.getSize(); i++) { result.setScalar(Tensor.newScalar(param2.getScalar(i).getValue() + param1.getScalar(i).getValue() * timeD13 + t.getScalar(i).getValue() * timeD23), i); }
				stack.push(result);
				return;
			}
		}
		/* RK2(3) *****************************************************************************************************
		 *  - save states
		 *  - phase 1: timeD = timeD / 2 and compute
		 *  - phase 2: compute only states, and integral store its argument in buffer
		 *  - restore states
		 *  - phase 3: timeD = 3 * timeD / 4 and compute
		 *  - phase 4: compute only states, and integral store its argument in buffer2
		 *  - restore states
		 *  - phase 5: compute, and integral = this + x * 2 * timeD / 9 + buffer * timeD / 3 + buffer2 * 4 * timeD / 9
		 */

		/* RK4 ********************************************************************************************************
		 *  - save states
		 *  - phase 1: timeD = timeD / 2 and compute
		 *  - phase 2: compute only states, and integral store its argument in buffer
		 *  - restore states
		 *  - phase 3: timeD = 3 * timeD / 4 and compute
		 *  - phase 4: compute only states, and integral store its argument in buffer2
		 *  - restore states
		 *  - phase *: compute, and integral = this + (x + 2 * buffer + 2 * buffer2 + buffer3) * timeD / 6
		 */

		if(graph.integrationMethod == STGraphImpl.RK23) {
			if(graph.getIntegrationPhase() == 1) {
				buffer = new Object[2];
				buffer[0] = new Vector();
				buffer[1] = new Vector();
				index = -1;
				if(order == 0) {
					stack.push(Tensor.newScalar(param2.getValue() + param1.getValue() * graph.getTimeD()));
					return;
				}
				Tensor result = new Tensor(param1.getDimensions());
				for(int i = 0; i < param1.getSize(); i++) { result.setScalar(Tensor.newScalar(param2.getScalar(i).getValue() + param1.getScalar(i).getValue() * graph.getTimeD()), i); }
				stack.push(result);
				return;
			}
			if(graph.getIntegrationPhase() == 3) {
				index = -1;
				if(order == 0) {
					stack.push(Tensor.newScalar(param2.getValue() + param1.getValue() * graph.getTimeD()));
					return;
				}
				Tensor result = new Tensor(param1.getDimensions());
				for(int i = 0; i < param1.getSize(); i++) { result.setScalar(Tensor.newScalar(param2.getScalar(i).getValue() + param1.getScalar(i).getValue() * graph.getTimeD()), i); }
				stack.push(result);
				return;
			}
			if(graph.getIntegrationPhase() == 2 || graph.getIntegrationPhase() == 4) {
				((Vector)buffer[(graph.getIntegrationPhase() - 2) / 2]).add(param1);
				stack.push(param1);
				return;
			}
			if(graph.getIntegrationPhase() == 5) {
				if(index < ((Vector)buffer[0]).size() - 1) { //TODO this test is here to deal the peculiar case of RK23 integration and integral() appears in a single condition of a if(), when the second step of the integration switches from condition=true to =false or viceversa, so that the integration phase = 2, which pushes the data into the buffer, would be called a number of times less than required
					index++;
				}
				double timeD29 = 2 * graph.getTimeD() / 9;
				double timeD13 = graph.getTimeD() / 3;
				double timeD49 = 4 * graph.getTimeD() / 9;
				if(order == 0) { //this + x * timeD / 3 + buffer * 2 * timeD / 3
					double k1 = ((Tensor)((Vector)buffer[0]).get(index)).getValue();
					double k2 = ((Tensor)((Vector)buffer[1]).get(index)).getValue();
					stack.push(Tensor.newScalar(param2.getValue() + param1.getValue() * timeD29 + k1 * timeD13 + k2 * timeD49));
					return;
				}
				Tensor t1 = (Tensor)((Vector)buffer[0]).get(index);
				Tensor t2 = (Tensor)((Vector)buffer[1]).get(index);
				Tensor result = new Tensor(param1.getDimensions());
				for(int i = 0; i < param1.getSize(); i++) { result.setScalar(Tensor.newScalar(param2.getScalar(i).getValue() + param1.getScalar(i).getValue() * timeD29 + t1.getScalar(i).getValue() * timeD13 + t2.getScalar(i).getValue() * timeD49), i); }
				stack.push(result);
				return;
			}
		}
	}

}
