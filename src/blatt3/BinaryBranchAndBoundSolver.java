package knapsack;

import java.util.List;

/**
 * Solution of a integer or binary knapsack problem
 *
 * @author Nils Baumgartner
 */
public class BinaryBranchAndBoundSolver implements SolverInterface<Solution> {

	public Instance instance; // merken der Instanz
	public double lowerBound;
	public Solution actualBest;
	public List<SortableItemValueWeightItem> itemsSortet;

	@Override
	public Solution solve(Instance instance) {
		this.instance = instance;

		// für jeden Gegenstand der Nutzen/Gewicht-Quotient bestimmt
		itemsSortet = SortableItemValueWeightItem.getSortedItemValueWeightItem(instance);

		actualBest = new Solution(instance); // wir beginne damit, nichts
												// mitzunehmen

		// System.out.println(
		// "The highest Value without WeightLimit is: " +
		// getSolutionValueByCheckedItemNr(actualBest, -1));

		checkSortetItemNr(actualBest, 0); // Start der Rekusrion mit Branch &
											// Bound

		// System.out.println("Okay all Possibilitys found");

		return actualBest;
	}

	/**
	 * Berechnet nach Branch & Bound alg. die Loesung ab der gegebenen ItemNr
	 * @param s Loesung welche es zu betrachten gilt
	 * @param nr die ItemNr aus der Sortierten Liste, ab welcher betrachtet werden soll
	 */
	private void checkSortetItemNr(Solution s, int nr) {
		/**
		 * Rekursions Anker
		 */
		
		//System.out.println("Check: "+nr+" Solution: "+s.toString());
		
		if (getSolutionValueByCheckedItemNr(s, nr - 1) > actualBest.getValue()) { //ist noch was rauszuholen?
			// System.out.println("There could be a better solution, lets check:
			// "+getSolutionValueByCheckedItemNr(s,nr-1)+">"+actualBest.getValue());
		} else {
			// System.out.println("No way this can be better than best");
			return; //rekursionsanker
		}

		if (nr >= itemsSortet.size()) { //haben wir alle Items gecheckt?
			 System.out.println("All Items checked; Solution Value:"+s.getValue()+" is better than best? best:"+actualBest.getValue());
			if (s.getValue() > actualBest.getValue()) {
				actualBest = s;
				System.out.println("Yes take it: "+s.toString());
			}
			return; //rekursionsanker
		}
		
		
		/**
		 * Rekursionsschritt
		 */

		int instanceItemNr = itemsSortet.get(nr).itemNumber; //um welches Item in der Instance handelt es sich
		
		//1.
		Solution with = new Solution(s);
		with.set(instanceItemNr, 1); //nehme neue Item mit
		if (with.isFeasible()) { //wenn erlaubt
			// System.out.println("Max:
			// "+getSolutionValueByCheckedItemNr(with,nr-1)+ " | Is:
			// "+with.getValue());
			// System.out.println("Okay, lets try this one");
			checkSortetItemNr(with, nr + 1); //pruefe weiter
		} else { //sonst gehe weiter
			// System.out.println("Dont check this one, cause not feasable");
		}

		//wiederhole zu 1. analog nur dass das Item nicht mitgenommen wird
		Solution withOut = new Solution(s);
		withOut.set(instanceItemNr, 0);

		if (withOut.isFeasible()) {
			// System.out.println("Max:
			// "+getSolutionValueByCheckedItemNr(withOut,nr-1)+ " | Is:
			// "+withOut.getValue());
			// System.out.println("Okay, lets try this one");
			checkSortetItemNr(withOut, nr + 1);
		} else {
			// System.out.println("Dont check this one, cause not feasable");
		}

	}

	/**
	 * Erhalte den Nutzen einer Loesung wobei, die besten sortetItemNrChecked
	 * Items gesetzt sind, und alle uebrigen als mitgenommen angesehen werden
	 * 
	 * @param s
	 *            die Loesung welche es zu betrachten gilt
	 * @param sortetItemNrChecked
	 *            die Anzahl der bereits gesetzten Items aus der sortierten
	 *            Kosten/Nutzen Reihe
	 * @return den moeglichen Nutzen einer Loesung
	 */
	private double getSolutionValueByCheckedItemNr(Solution s, int sortetItemNrChecked) {
		double value = 0;
		for (int sortetItem = 0; sortetItem < itemsSortet.size(); sortetItem++) {
			int instanceItemNr = itemsSortet.get(sortetItem).itemNumber;
			if (sortetItem <= sortetItemNrChecked) { // check what we packed in
				value += s.get(instanceItemNr) * s.getInstance().getValue(instanceItemNr); // calc
																							// value
																							// if
																							// packed
			} else { // packe den rest mit dem naechst besten Item fractional voll
				double restPlace = s.getInstance().getCapacity()-s.getWeight();
				double fracAmount = (restPlace/s.getInstance().getWeight(instanceItemNr));
				value += fracAmount*s.getInstance().getValue(instanceItemNr);
				return value; //gebe den rest zurueck
			}
		}
		return value;
	}

}