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
public class BinaryChocoKnapsackSolver implements SolverInterface<Solution> {

	@Override
	public Solution solve(Instance instance) {
		
		Model m = new Model(); //beginne neues problem
		IntVar weightSum = m.intVar("Solution Capacity",instance.getCapacity()); //max gewicht
		
		//TODO Right max
		IntVar energySum = m.intVar("Solution Value",0,500); //erlaubte effiziens
		
		IntVar[] occurrences = new IntVar[instance.getSize()]; //anzahl der items
        for (int i = 0; i < occurrences.length; i++) {
            occurrences[i] = m.intVar("#"+i,0, 1); //jedes Item min 0, max 1
        }
        
//        System.out.println("Items");
//        for(int i=0;i<instance.getWeightArray().length;i++){
//        	System.out.println("Item("+i+"): w:("+instance.getWeightArray()[i]+") v:("+instance.getValueArray()[i]+")");
//        }
//        System.out.println();

		m.knapsack(occurrences, weightSum, energySum, instance.getWeightArray(), instance.getValueArray()).post();

		Solver solver = m.getSolver();

//		Criterion stop = new SolutionCounter(m, Long.MAX_VALUE); //stoppe wenn zu lange
//		System.out.println("Solve Now !");
//		org.chocosolver.solver.Solution chocoS =  solver.findOptimalSolution(energySum, true, stop);
		org.chocosolver.solver.Solution chocoS =  solver.findOptimalSolution(energySum, true); //suche beste loesung
		
		Solution actualBest = new Solution(instance);
		
		for(int i = 0; i < instance.getSize(); i++) {
        	actualBest.set(i, chocoS.getIntVal(occurrences[i]));
        }
		
		return actualBest ;
	}

}