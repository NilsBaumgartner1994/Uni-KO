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

	/**
	 * Constructor einer Population
	 * @param config
	 */
	public BinaryGenericSolutionPopulation(BinaryGenericConfiguration config) {
		this.config = config;
		this.population = new LinkedList<BinaryGenericSolution>(); //einfache Liste
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
		if(isPopulationSizeReached()){ //wenn grenze erreicht
			population.remove(population.size() - 1); //lösche schwächsten
		}
		this.population.add(child); //füge kind hinzu
	}

	/**
	 * Sortiert die Population nach der Fitness
	 */
	private void sort() {
		Collections.sort(population, this);
	}
	
//	private void checkForPopSize() {
//		while (isPopulationSizeReached()) {
//			population.remove(population.size() - 1);
//		}
//	}
	
	/**
	 * Ist die angegebene Grenze der Population erreicht
	 * @return
	 */
	private boolean isPopulationSizeReached(){
		return config.populationSize.getValue() <= population.size();
	}

	/**
	 * Wähle ein Element aus nach der Eltern Configuration aus
	 * @return
	 */
	public BinaryGenericSolution getMotherOrFather() {
		switch (config.parentSelection) {
		case FITNESS: //nach fitness
			return BinaryGenericConfiguration.PARENTSELECTION.getFittest(population);
		case RANDOM: //oder zufällig
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