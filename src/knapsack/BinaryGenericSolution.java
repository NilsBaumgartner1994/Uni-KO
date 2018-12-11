package knapsack;

import java.util.Comparator;

/**
 * Solution of a integer or binary knapsack problem
 *
 * @author Nils Baumgartner
 */
public class BinaryGenericSolution implements GenericFitnessInterface{
	
	Solution solution;

	public BinaryGenericSolution(Solution solution) {
		this.solution = solution;
	}
	
	public Integer getFitness(){
		return this.solution.getValue();
	}
	
	/**
	public String getChromosom(){
		String chromosom = "";
		for(int item = 0; item<this.solution.instance.getSize(); item++){
			chromosom+=this.solution.get(item);
		}
		return chromosom;
	}
	
	public void mutate(){
		
	}
	// */
}