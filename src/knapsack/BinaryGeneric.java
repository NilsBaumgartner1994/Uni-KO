package knapsack;

import knapsack.BinaryGenericConfiguration.CROSSOVER;
import knapsack.BinaryGenericConfiguration.ITERATIONS;
import knapsack.BinaryGenericConfiguration.MUTATIONCHANCE;
import knapsack.BinaryGenericConfiguration.PARENTSELECTION;
import knapsack.BinaryGenericConfiguration.POPSIZE;

/**
 * Solution of a integer or binary knapsack problem
 *
 * @author Nils Baumgartner
 */
public class BinaryGeneric implements SolverInterface<Solution> {

	/**
	 * Einstellen der Configuration
	 * Weitere Parameter in der Entsprechenden Klasse ändern und einstellen
	 * @return
	 */
	public BinaryGenericConfiguration getConfig() {
		BinaryGenericConfiguration config = new BinaryGenericConfiguration();
		config.setIteration(ITERATIONS.ALOT);
		config.setPopulationSize(POPSIZE.ALOT);
		config.setMutationCance(MUTATIONCHANCE.HIGH);
		
		config.setCrossoverType(CROSSOVER.ONEPOINTCROSSOVER);
		config.setParentSelection(PARENTSELECTION.FITNESS);
		return config;
	}
	
	@Override
	public Solution solve(Instance instance) {
		BinaryGenericSolutionPopulation pop = genericAlg(instance);
		return pop.population.get(0).solution;
	}
	
	/**
	 * Statet den Generischen Algoritmus
	 * @param instance
	 * @return
	 */
	public BinaryGenericSolutionPopulation genericAlg(Instance instance) {
		BinaryGenericConfiguration config = getConfig(); //hole einstellungen
		int iteration = 0; //iterationen reset
		BinaryGenericSolutionPopulation pop = new BinaryGenericSolutionPopulation(config); //erstelle Pop Klasse
		
		// 1. Generiere eine initiale Population POP;	
		generateInitPopulation(instance, pop);

		// 2. Berechne die Fitness f(s) f ̈ur jedes Individuum s ∈ P OP;
		// wird durch getValue geregelt

		do { // 3.
				// 4. Wähle zwei Eltern-Lösungen sM, sF ∈ POP;
			BinaryGenericSolution mother = pop.getMotherOrFather();
			BinaryGenericSolution father = pop.getMotherOrFather();

			// 5. Generiere eine Kind-L ̈osung sC von sM, sF durch Crossover;
			BinaryGenericSolution child = config.crossoverType.crossOver(mother, father);
			//System.out.println(child.solution.toString());

			// 6. Mutiere sC mit einer bestimmten Wahrscheinlichkeit;
			config.mutationConf.mutate(child);

			// 7. Berechne die Fitness f(sC) von sC;
			// wird durch getValue geregelt

			if (child.solution.isFeasible()) { //nur erlaubte lösungen dürfen rein
				// 8. Füge sC zu P OP hinzu;
				pop.addToPopulation(child);
			}

			// 9. Falls POP eine bestimmte Gröoße erreicht hat,reduziere POP
			// durch Selektion;
			// regelt pop bei add bereits
			iteration++;
			if(iteration%(config.maxIterations.getValue()/10)==0){
				System.out.println(iteration+"/"+config.maxIterations.getValue());
			}
			
		} while (iteration < config.maxIterations.getValue()); // 10.
		
		return pop;
	}
	
	/**
	 * Generiere eine Population, in welcher jedes Gen/Bit einmal vorhanden ist, wenn möglich
	 * Einfachste Art, um die unterschiedlichen Variablen miteinander zu vergleichen
	 * 
	 * @param instance
	 * @param pop
	 */
	public static void generateInitPopulation(Instance instance, BinaryGenericSolutionPopulation pop){
		for(int i=0; i<instance.getSize(); i++){ //für alle Variablen
			Solution s = new Solution(instance);
			s.set(i, 1); // erstelle Lösung mit auswahl des Bits
			if(s.isFeasible()){ //wenn erlaubt
				pop.addToPopulation(new BinaryGenericSolution(s)); //füge der Pop hinzu
			}
		}
	}

}