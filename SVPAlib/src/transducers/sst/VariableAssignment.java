/**
 * SVPAlib
 * transducers.sst
 * Apr 21, 2015
 * @author Loris D'Antoni
 */

package transducers.sst;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import theory.BooleanAlgebraSubst;

/**
 * A variable assignment. A function mapping each variable to its current value
 * @param <S>
 *            The domain of the Boolean algebra
 */
public class VariableAssignment<S> {

	ArrayList<List<S>> variableAssignments;

	public VariableAssignment(ArrayList<List<S>> variableAssignments) {
		super();
		this.variableAssignments = variableAssignments;
	}
	
	/**
	 * Value of the first variable at index 0
	 * @return
	 */
	public List<S> outputVariableValue(){
		return variableAssignments.get(0);
	}
	
	/**
	 * Value of the first variable at index 0
	 * @return
	 */
	public List<S> variableValue(int index){
		return variableAssignments.get(index);
	}
	
	/**
	 * returns number of variables
	 */
	public int numVars(){
		return variableAssignments.size();
	}
	
	/**
	 * returns an initialize assignment to epsilon
	 * @param varsLength
	 * @param ba
	 * @return
	 */
	public static <S1, F1, P1> VariableAssignment<S1> MkInitialValue(
			int varsLength,
			BooleanAlgebraSubst<P1, F1, S1> ba
			){
		ArrayList<List<S1>> emptyVariableAssignment = new ArrayList<List<S1>>(varsLength);
		for(int variable = 0; variable<varsLength; variable++)
			emptyVariableAssignment.add(new LinkedList<S1>());
		
		return new VariableAssignment<S1>(emptyVariableAssignment); 
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(List<S> ass :  variableAssignments){
			for(S v :  ass){
				sb.append(v.toString());				
			}			
			sb.append(';');
		}
		return sb.toString();
	}
}
