package knapsack;

/**
 * Solution of a integer or binary knapsack problem
 *
 * @author Nils Baumgartner
 */
public class BinarySolver implements SolverInterface<Solution> {
	
	public Instance instance; //merken der Instanz
	public static Solution best; //merken der besten L�sung

	@Override
	public Solution solve(Instance instance) {
		this.instance = instance;
		
		
		
		Solution toTest = new Solution(instance); //wir beginne damit, nichts mitzunehmen
		best = new Solution(toTest); //erstelle 0 L�sung als Bestes
		
	
		return best; //und geben die beste L�sung zur�ck
	}
	
	
}