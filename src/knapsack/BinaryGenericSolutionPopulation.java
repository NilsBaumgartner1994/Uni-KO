package knapsack;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Solution of a integer or binary knapsack problem
 *
 * @author Nils Baumgartner
 */
public class BinaryGenericSolutionPopulation implements Comparator<BinaryGenericSolution> {

	List<BinaryGenericSolution> population;
	BinaryGenericConfiguration config;

	public BinaryGenericSolutionPopulation(BinaryGenericConfiguration config) {
		this.config = config;
		this.population = new LinkedList<BinaryGenericSolution>();
	}

	/**
	 * falls Max erreich, Schmeiße den schlechtesten Raus
	 *	packe neues Kind hinzu
	 * 
	 * “steady-state”-Strategie: sobald eine Kind-L ̈osung generiert wurde, wird
	 * ein Individuum aus der alten Population durch die neue L ̈osung ersetzt
	 * (dabei wird die zu ersetzende L ̈osung meist zuf ̈allig, gem ̈aß ihrer
	 * Fitness oder ihres Alters gew ̈ahlt).
	 */
	public void addToPopulation(BinaryGenericSolution child) {
		sort();
		if(isPopulationSizeReached()){
			population.remove(population.size() - 1);
		}
		this.population.add(child);
	}

	private void sort() {
		Collections.sort(population, this);
	}
	
//	private void checkForPopSize() {
//		while (isPopulationSizeReached()) {
//			population.remove(population.size() - 1);
//		}
//	}
	
	private boolean isPopulationSizeReached(){
		return config.populationSize.getValue() <= population.size();
	}

	public BinaryGenericSolution getMotherOrFather() {
		switch (config.parentSelection) {
		case FITNESS:
			return BinaryGenericConfiguration.PARENTSELECTION.getFittest(population);
		case RANDOM:
			return BinaryGenericConfiguration.PARENTSELECTION.getRandom(population);
		default:
			return BinaryGenericConfiguration.PARENTSELECTION.getRandom(population);
		}
	}

	@Override
	public int compare(BinaryGenericSolution o1, BinaryGenericSolution o2) {
		return o2.getFitness() - o1.getFitness();
	}

}