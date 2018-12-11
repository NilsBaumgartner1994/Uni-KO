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
	public MUTATIONCHANCE mutationConf;
	public CROSSOVER mutationType;
	public PARENTSELECTION parentSelection;

	public BinaryGenericConfiguration() {
		rand = new Random();
		setIteration(ITERATIONS.ALOT);
		setPopulationSize(POPSIZE.ALOT);
		setMutationType(CROSSOVER.ONEPOINTCROSSOVER);
		setMutationCance(MUTATIONCHANCE.LOW);
		setParentSelection(PARENTSELECTION.FITNESS);
	}

	public void setParentSelection(PARENTSELECTION parentSelection) {
		this.parentSelection = parentSelection;
	}

	public void setIteration(ITERATIONS maxIterations) {
		this.maxIterations = maxIterations;
	}

	public void setPopulationSize(POPSIZE populationSize) {
		this.populationSize = populationSize;
	}

	public void setMutationType(CROSSOVER mutationType) {
		this.mutationType = mutationType;
	}

	public void setMutationCance(MUTATIONCHANCE mutationConf) {
		this.mutationConf = mutationConf;
	}

	// 2 diff mutation chance (flip bit chance
	public enum PARENTSELECTION {
		RANDOM, FITNESS;

		public static <T extends GenericFitnessInterface> T getRandom(List<T> items) {
			return items.get(rand.nextInt(items.size()));
		}

		public static <T extends GenericFitnessInterface> T getFittest(List<T> items) {
			int maxFitness = 0;
			for (T item : items) {
				maxFitness += item.getFitness();
			}

			int chance = rand.nextInt(maxFitness);
			T selected = items.get(0);

			for (T item : items) {
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
		
		public void mutate(BinaryGenericSolution sol){
			float chance = rand.nextFloat();
			if(chance-this.getValue()<0){
				int item = rand.nextInt(sol.solution.instance.getSize());
				sol.solution.set(item, (sol.solution.get(item)+1)%2);
			}
		}

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

		POPSIZE(final int newValue) {
			value = newValue;
		}

		public int getValue() {
			return value;
		}
	}

	public enum CROSSOVER {
		ONEPOINTCROSSOVER;

		public BinaryGenericSolution crossOver(BinaryGenericSolution mother, BinaryGenericSolution father) {
			switch(this){
			case ONEPOINTCROSSOVER:
				return onePointCrossOver(mother,father);
			default:
				return onePointCrossOver(mother,father);
			}
		}

		public BinaryGenericSolution onePointCrossOver(BinaryGenericSolution mother, BinaryGenericSolution father) {
			int motherGenes = rand.nextInt(mother.solution.instance.getSize());
			Solution child = new Solution(father.solution);
			for(int i=0; i<motherGenes; i++){
				child.set(i, mother.solution.get(i));
			}
			
			return new BinaryGenericSolution(child);
		}
	}

}