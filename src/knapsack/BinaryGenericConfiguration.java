package knapsack;

import java.util.List;
import java.util.Random;

/**
 * Solution of a integer or binary knapsack problem
 *
 * @author Nils Baumgartner
 */
public class BinaryGenericConfiguration {

	public static Random rand;

	public ITERATIONS maxIterations; // 2 diff max iterations
	public POPSIZE populationSize; // 2 diff pop size
	public MUTATIONCHANCE mutationConf; //2 diff mutation chance
	public CROSSOVER crossoverType; //crossovertype
	public PARENTSELECTION parentSelection; //ausawhl der eltern

	/**
	 * Default einstellungen
	 */
	public BinaryGenericConfiguration() {
		rand = new Random();
		setIteration(ITERATIONS.ALOT);
		setPopulationSize(POPSIZE.ALOT);
		setCrossoverType(CROSSOVER.ONEPOINTCROSSOVER);
		setMutationCance(MUTATIONCHANCE.LOW);
		setParentSelection(PARENTSELECTION.FITNESS);
	}

	/**
	 * Setter für ElternAuswahl
	 * @param parentSelection
	 */
	public void setParentSelection(PARENTSELECTION parentSelection) {
		this.parentSelection = parentSelection;
	}

	/**
	 * Setter für Max Iterationen
	 * @param maxIterations
	 */
	public void setIteration(ITERATIONS maxIterations) {
		this.maxIterations = maxIterations;
	}

	/**
	 * Setter für Max Iterationen
	 * @param populationSize
	 */
	public void setPopulationSize(POPSIZE populationSize) {
		this.populationSize = populationSize;
	}

	/**
	 * Set Crossover Type
	 * @param crossoverType
	 */
	public void setCrossoverType(CROSSOVER crossoverType) {
		this.crossoverType = crossoverType;
	}

	/**
	 * Set MutationChance
	 * @param mutationConf
	 */
	public void setMutationCance(MUTATIONCHANCE mutationConf) {
		this.mutationConf = mutationConf;
	}

	// 2 diff mutation chance (flip bit chance
	public enum PARENTSELECTION {
		RANDOM, FITNESS;

		/**
		 * Random selection
		 * @return
		 */
		public static <T extends GenericFitnessInterface> T getRandom(List<T> items) {
			return items.get(rand.nextInt(items.size()));
		}

		/**
		 * Selection of Fittest in pop after script
		 */
		public static <T extends GenericFitnessInterface> T getFittest(List<T> items) {
			int maxFitness = 0;
			for (T item : items) {
				maxFitness += item.getFitness(); //berechne max fitness
			}

			int chance = rand.nextInt(maxFitness);
			T selected = items.get(0); //standart wahl, falls was schief geht

			for (T item : items) { //wähle Element nach größe seiner Fitness zufällig
				chance -= item.getFitness();
				if (chance < 0) {
					return item;
				}
			}

			return selected;
		}
	}

	public enum MUTATIONCHANCE {
		LOW(0.1f), HIGH(0.5f);
		
		private final float value;
		
		/**
		 * Lässt eine Solution Mutieren nach bestimmer wahrsch.
		 */
		public void mutate(BinaryGenericSolution sol){
			float chance = rand.nextFloat();
			if(chance-this.getValue()<0){ //wenn mutiert werden soll
				int item = rand.nextInt(sol.solution.instance.getSize()); //zufälliges item
				sol.solution.set(item, (sol.solution.get(item)+1)%2); //flip
			}
		}

		/**
		 * Constructor für Mutationschance
		 * @param newValue
		 */
		MUTATIONCHANCE(final float newValue) {
			value = newValue;
		}

		public float getValue() {
			return value;
		}
	}

	public enum ITERATIONS {
		SOME(10000), ALOT(100000);

		private final int value;

		/**
		 * Constructor für MaxIterations
		 * @param newValue
		 */
		ITERATIONS(final int newValue) {
			value = newValue;
		}

		public int getValue() {
			return value;
		}
	}

	public enum POPSIZE {
		SOME(100), ALOT(1000);

		private final int value;

		/**
		 * Constructor für Populationsize
		 */
		POPSIZE(final int newValue) {
			value = newValue;
		}

		public int getValue() {
			return value;
		}
	}

	public enum CROSSOVER {
		ONEPOINTCROSSOVER;

		/**
		 * Crossover von Father und Mutter nach gegebenem CrossoverTyp
		 * @param mother
		 * @param father
		 * @return
		 */
		public BinaryGenericSolution crossOver(BinaryGenericSolution mother, BinaryGenericSolution father) {
			switch(this){ //es gibt nur eine implementierung
			case ONEPOINTCROSSOVER: 
				return onePointCrossOver(mother,father);
			default:
				return onePointCrossOver(mother,father);
			}
		}

		/**
		 * One-Point-Crossover nach Script
		 * @param mother
		 * @param father
		 * @return
		 */
		public BinaryGenericSolution onePointCrossOver(BinaryGenericSolution mother, BinaryGenericSolution father) {
			int motherGenes = rand.nextInt(mother.solution.instance.getSize());
			Solution child = new Solution(father.solution);
			for(int i=0; i<motherGenes; i++){ //motherGenes anzahl zufällige hintereinanderliegende Gene ans Kind weiter geben
				child.set(i, mother.solution.get(i));
			}
			
			return new BinaryGenericSolution(child);
		}
	}

}