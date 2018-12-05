package knapsack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import knapsack.BinaryTabuList.ALLOWNOTFEASABLENEIGHBORS;
import knapsack.BinaryTabuList.TABULISTTYPE;

/**
 * Solution of a integer or binary knapsack problem
 *
 * @author Nils Baumgartner
 */
public class TabuList {

	/**
	 * Variablen
	 */
	public TABULISTTYPE type;
	public ALLOWNOTFEASABLENEIGHBORS allowNotFeasableNeighbors;

	public List<Integer> forbidden; //die eigentliche Tabu Liste
	int size = -1; //aktuell erlaubte größe
	int LB = 0; //unter schranke für dyn Liste
	int UB = 0; //obere schranke für dyn Liste

	/**
	 * Creates a new TabuList
	 * 
	 * @param feasableNeighbors
	 */
	public TabuList(Instance instance, TABULISTTYPE type, ALLOWNOTFEASABLENEIGHBORS allowNotFeasableNeighbors) {
		this.type = type;
		this.allowNotFeasableNeighbors = allowNotFeasableNeighbors;
		LB = 0; //untere schranke ist 0
		UB = instance.getSize() - 1; //obere schranke ist erstmal alle anderen bits groß
		size = UB; //startgröße ist erstmal größte
		reset(); //leere die Liste und erstelle diese
	}

	// DYNAMIC
	/**
	 * Neues globales Maximum
	 */
	public void newGlobalBestSolutionFound() { 
		if (type == TABULISTTYPE.DYNAMIC) {
			reset();
			setDynamicSize(UB); // keine angabe im Script dazu
		}
	}

	/**
	 * Wenn eine local bessere Lösung gefunden wurde wird die TabuListe gerringert
	 */
	public void betterSolutionFoundAsIterationBefore() { //nach Script vorschalg
		if (type == TABULISTTYPE.DYNAMIC) {
			setDynamicSize(this.size - 1);
		}
	}

	/**
	 * Wenn eine local schlechtere Lösung gefunden wird, TabuListe länger machen
	 */
	public void worseSolutionFoundAsIterationBefore() { //vorschlag nach Script
		if (type == TABULISTTYPE.DYNAMIC) {
			setDynamicSize(this.size + 1);
		}
	}

	/**
	 * Setzt die Dynamische Größe der Liste, wobei die Schranken hierbei beachtet werden
	 * @param size
	 */
	private void setDynamicSize(int size) {
		if (size >= LB && size <= UB) {
			this.size = size;
		} else if (size > UB) {
			this.size = UB;
		} else if (size < LB) {
			this.size = LB;
		}
	}

	/**
	 * Clears the List
	 */
	public void reset() {
		forbidden = new ArrayList<Integer>();
	}

	/**
	 * Deletes elements if there are too much elements
	 */
	public void checkForLimitations() {
		// but only when not inifit size
		while (type != TABULISTTYPE.INFINITE && forbidden.size() > size) {
			// System.out.println("To much elements");
			forbidden.remove(0);
		}
	}

	/**
	 * Adds an Index to the Tabu List and deletes first
	 * 
	 * @param index
	 */
	public void addToTabuList(int index) {
		forbidden.add(index);
		// System.out.println("Size: "+forbidden.size());
		checkForLimitations();
	}

	/**
	 * Überprüft ob ändern eines Bits verboten ist
	 * @param pos
	 * @return
	 */
	public boolean isTransformationAllowed(int pos) {
		return !forbidden.contains(pos); //hat die Liste das Bit nicht
	}

	/**
	 * Gibt alle zulässigen Nachbarn einer Lösung zurück
	 * @param s
	 * @return
	 */
	public List<Solution> getAllAllowedNeighbors(Solution s) {
		List<Solution> allowed = new ArrayList<Solution>(); //liste aller erlaubten nachbarn
		for (int i = 0; i < s.instance.getSize(); i++) { //laufe alle Items durch
			Solution neighbor = getSolutionWithPositionFlipped(s, i); //flippe entsprechendes Item
			//checke ob feasible oder ob darauf verzichtet werden soll
			if (this.allowNotFeasableNeighbors == ALLOWNOTFEASABLENEIGHBORS.TRUE || neighbor.isFeasible()) {
				if (isTransformationAllowed(i) || neighbor.getValue() > s.getValue()) { // aspirations
																						// crit
					allowed.add(neighbor); //merke diesen nachbarn
				}
			}
		}
		return allowed;
	}

	/**
	 * Gibt eine neue Lösung zurück mit einem Bit geflipped zurück
	 * @param s
	 * @param pos
	 * @return
	 */
	public Solution getSolutionWithPositionFlipped(Solution s, int pos) {
		Solution neighbor = new Solution(s);
		neighbor.set(pos, (neighbor.get(pos) + 1) % 2); //flippe bit an entsprechender Stelle
		return neighbor;
	}

}