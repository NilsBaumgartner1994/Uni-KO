package knapsack;

import java.util.List;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.limits.SolutionCounter;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.criteria.Criterion;

/**
 * Solution of a integer or binary knapsack problem
 *
 * @author Nils Baumgartner
 */
public class BinaryChocoNotKnapsackSolver implements SolverInterface<Solution> {

	@Override
	public Solution solve(Instance instance) {

		int itemNumber = instance.getSize();

		Model model = new Model("KnapsackProblem");

		// Allocation variable
		IntVar[] occurrences = new IntVar[itemNumber];
		for (int i = 0; i < itemNumber; i++) {
			occurrences[i] = model.intVar("x" + i, 0, 1);
		}


		// Knapsack capacities variables
		IntVar limit = model.intVar(instance.getCapacity()); //maximale Last
		IntVar weight = model.intVar(0,IntVar.MAX_INT_BOUND); //gewichtssumme
		IntVar efficiency = model.intVar(0,IntVar.MAX_INT_BOUND); //nutzen

//		IntVar[] itemWeightVar = new IntVar[itemNumber];
//		IntVar[] itemValueVar = new IntVar[itemNumber];
		
//		for (int i = 0; i < itemNumber; i++) {
//			//Gewichte
//			itemWeightVar[i] = model.intVar(instance.getWeight(i));
//			model.element(itemWeightVar[i], instance.getWeightArray(), x[i]).post(); //Contraints
//		
//			//Effizienz
//			itemValueVar[i] = model.intVar(instance.getValue(i));
//			model.element(itemWeightVar[i], instance.getValueArray(), x[i]).post(); //Contraints
//		}
		
//		model.sum(itemWeightVar, "<=", limit).post(); //Constraints
//		model.sum(itemValueVar, "=", energySum).post(); //Constraints
		
		//Constraints
		model.arithm(weight, "<=", limit).post(); //maximales gewicht nicht ueberschreiten
        model.scalar(occurrences, instance.getValueArray(), "=", efficiency).post(); //effizienz
        model.scalar(occurrences, instance.getWeightArray(), "=", weight).post(); //gewichts zuweisung

		Solver solver = model.getSolver();

//		Criterion stop = new SolutionCounter(model, Long.MAX_VALUE); //stoppe wenn zu lange
//		System.out.println("Solve Now !");
//		org.chocosolver.solver.Solution chocoS =  solver.findOptimalSolution(energySum, true, stop);
//		List<org.chocosolver.solver.Solution> allSolutions = solver.findAllSolutions(stop);
//		for(org.chocosolver.solver.Solution s : allSolutions){
//			System.out.println(s.toString());
//		}
		
		org.chocosolver.solver.Solution chocoS =  solver.findOptimalSolution(efficiency, true); //suche beste loesung
		
		Solution actualBest = new Solution(instance);
		

        for(int i = 0; i < instance.getSize(); i++) {
        	actualBest.set(i, chocoS.getIntVal(occurrences[i]));
        }
		
//		 = getSolutionFromChocoSolution(instance, chocoS);
//		Solution actualBest 
		
		System.out.println("######## ENDE #######");
		
		return actualBest;
	}

	public static Solution getSolutionFromChocoSolution(Instance instance, org.chocosolver.solver.Solution chocoS) {
		Solution solution = new Solution(instance);
		String solString = chocoS.toString();
		// System.out.println("Before: "+solString);
		solString = solString.replaceAll(" ", "");
		solString = solString.replaceAll("#\\d+=", "");
		String[] splits = solString.split(",");
		for (int i = 1; i < splits.length; i++) {
			// System.out.print(splits[i]+" ");
			int a = Integer.parseInt(splits[i]);
			solution.set(i - 1, a);
		}

		return solution;
	}

}