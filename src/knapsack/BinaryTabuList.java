package knapsack;

import java.util.List;
import java.util.Random;

/**
 * Solution of a integer or binary knapsack problem
 *
 * @author Nils Baumgartner
 */
public class BinaryTabuList implements SolverInterface<Solution> {

	public TabuList tl;
	public Instance instance; // merken der Instanz
	public Random r = new Random();

	public int iterations;

	// Start Solution Type
	public STARTSOLUTION startSolutionType;

	// Stop Criteria
	public STOPCRITERIA stopCriteriaType;
	public long startTime = -1; // start Zeit zum Messen der Dauer
	public long stopCriteriaMaxTime = 9 * 1000L; // maximale Zeit bei Zeit Stop
													// Kriterium
	public long maxIterations = 10000; // Maximale anzahl an Iterationen

	// Cooling
	public double lastTi = 100.0; // startwert für die Reihe Ti
	public double alpha = 0.9; // senkfaktor bei geometric cooling

	// Neighbor Selection
	CHOOSEFROMNEIGHBOR neighborSelection;

	// TABULIST TYPE
	TABULISTTYPE tabuListType;

	// No neighbors found
	NONEIGHBORSFOUND noNeighborsFound;

	// Feasable Neighbors
	ALLOWNOTFEASABLENEIGHBORS feasableNeighbors;

	/**
	 * Diverse Enums
	 */
	public enum TABULISTTYPE {
		DYNAMIC, STATIC, INFINITE;
	}

	public enum STARTSOLUTION {
		EMPTY, FIRSTFILLED;
	}

	public enum STOPCRITERIA {
		TIME, ITERATIONS, JUSTEND;
	}

	public enum CHOOSEFROMNEIGHBOR {
		BESTFIT, RANDOM, FIRSTFIT;
	}

	public enum ALLOWNOTFEASABLENEIGHBORS {
		TRUE, FALSE;
	}

	public enum NONEIGHBORSFOUND {
		RETURNBESTFOUND, CHANGETORANDOMNEIGHBOR, CHANGETOFIRSTFITNEIGHBOR, CHANGETOBESTFITNEIGHBOR;
	}

	/**
	 * Start Variablen zum einstellen des Solvers
	 */
	public void setVariables() {
		startSolutionType = STARTSOLUTION.EMPTY;
		stopCriteriaType = STOPCRITERIA.ITERATIONS;
		neighborSelection = CHOOSEFROMNEIGHBOR.BESTFIT;
		tabuListType = TABULISTTYPE.STATIC;
		noNeighborsFound = NONEIGHBORSFOUND.CHANGETORANDOMNEIGHBOR;
		feasableNeighbors = ALLOWNOTFEASABLENEIGHBORS.FALSE;
		iterations = 0;
	}

	@Override
	public Solution solve(Instance instance) {
		setVariables(); // setzt variablen

		this.instance = instance;
		Solution sApo;
//		boolean noNeighborsFound = false; // anfangs sind nachbarn gefunden
//		CHOOSEFROMNEIGHBOR oldNeighborSelection = neighborSelection; // speichere
//																		// gewähltes
//																		// verfahren

		// Nun der Alg. nach Script S.36 angepasst an maximierung !
		Solution s = getStartSolution(instance);// 1.
		Solution sStar = s; // 2
		int cStar = sStar.getValue(); // 3
		tl = new TabuList(instance, tabuListType, feasableNeighbors); // 4
		do { // 5.
			sApo = getNeighborSolution(tl, s); // 6+7+8
			if (sApo != null) { // nur wenn eine folgelösung gefunden wird

				int nextForbiddenPos = difference(s, sApo); // index für tabu
															// update
				tl.addToTabuList(nextForbiddenPos); // 8

				if (sApo.getValue() > s.getValue() && sApo.isFeasible() && s.isFeasible()) { // Script
																								// S.38
																								// Punkt
																								// 2
					tl.betterSolutionFoundAsIterationBefore();
				} else if (sApo.getValue() < s.getValue() && sApo.isFeasible() && s.isFeasible()) { // Sscript
																									// S.38
					// Punkt 3
					tl.worseSolutionFoundAsIterationBefore();
				}

				s = sApo; // 9

				if (s.getValue() > cStar && s.isFeasible()) { // 10
					sStar = s; // 11
					cStar = s.getValue(); // 12
					tl.newGlobalBestSolutionFound(); // Script S.38 Punkt 1
				}
			} else { // keine weiteren Lösungen auf
											// diesem Weg zu finden
				// System.out.println("No Neighborsfound, changing selection
				// Method"); //nette ausgabe
				noMoreAllowedNeighborsFound(); // bestimme weiteren verlauf
			}
			updateStopCriteria();
		} while (!isStopCriteriaReached());

		return sStar;
	}

	/**
	 * Wenn keine Nachbarn gefunden wurden, bestimmte weiteren Verlauf
	 */
	public void noMoreAllowedNeighborsFound() {
		tl.reset();
		switch (noNeighborsFound) {
		case CHANGETOBESTFITNEIGHBOR: // wenn zum BestFit gewechselt werden soll
			neighborSelection = CHOOSEFROMNEIGHBOR.BESTFIT;
			break;
		case CHANGETOFIRSTFITNEIGHBOR: // wenn zum FirstFit gewechselt werden
										// soll
			neighborSelection = CHOOSEFROMNEIGHBOR.FIRSTFIT;
			break;
		case CHANGETORANDOMNEIGHBOR: // wenn zufällige Lösungen dann gewählt
										// werden sollen
			neighborSelection = CHOOSEFROMNEIGHBOR.RANDOM;
			break;
		case RETURNBESTFOUND: // wenn einfach aufgehört werden soll
			this.stopCriteriaType = STOPCRITERIA.JUSTEND;
			return;
		default:
			break;

		}
	}

	/**
	 * Gibt das erste Bit aus, welches anders ist
	 * 
	 * @param a
	 *            Lösung
	 * @param b
	 *            Lösung
	 * @return
	 */
	public int difference(Solution a, Solution b) {
		if (a.getInstance().getSize() == b.getInstance().getSize()) {
			for (int i = 0; i < a.instance.getSize(); i++) { // gehe alle bits
																// durch
				if (a.get(i) != b.get(i)) // wenn unterschied gebe aus
					return i;
			}
		}
		return -1; // -1 zur fehlerbehandlung, bei gleicheit oder unterschiedlicher größe
	}

	/**
	 * Wähle nachbarn nach ausgewählter Strategie aus mithilfe der TabuList
	 * @param tl
	 * @param s
	 * @return
	 */
	public Solution getNeighborSolution(TabuList tl, Solution s) {
		List<Solution> allowedNeighbors = tl.getAllAllowedNeighbors(s); // 6
		if (allowedNeighbors.size() == 0) { // wenn keine gefunden, geben null aus
			return null;
		}
		switch (neighborSelection) { // 7
		case BESTFIT: // best fit
			return getBestFit(allowedNeighbors);
		case FIRSTFIT: // first fit
			return getFirstFit(allowedNeighbors, s);
		case RANDOM: //random
			return getRandomSolution(allowedNeighbors);
		default: //fehlerbehandlung
			return allowedNeighbors.get(0);
		}
	}

	/**
	 * Gib den ersten Nachbarn, welcher besser ist als die Lösung s, sonst nimm den besten
	 * @param allowedNeighbors erlaubte Nachbarn
	 * @param s Lösung
	 * @return
	 */
	public Solution getFirstFit(List<Solution> allowedNeighbors, Solution s) {
		Solution best = allowedNeighbors.get(0); //bester erster
		int bestVal = best.getValue();
		for (Solution n : allowedNeighbors) { // iterate all neighbors
			int v = n.getValue();
			if (v > s.getValue()) { //wenn besser als Lösung s
				return n; // find first best fit
			}
			if (v > bestVal) { //merke den besten
				best = n;
				bestVal = v;
			}
		}
		return best; // falls keiner besser ist als s, nimm den besten

	}

	/**
	 * Wähle eine zufällige Lösung aus allen erlaubten Nachbarn
	 * @param allowedNeighbors
	 * @return
	 */
	public Solution getRandomSolution(List<Solution> allowedNeighbors) {
		return allowedNeighbors.get(r.nextInt(allowedNeighbors.size()));
	}

	/**
	 * Wähle den besten Nachbarn aus
	 * @param allowedNeighbors
	 * @return
	 */
	public Solution getBestFit(List<Solution> allowedNeighbors) {
		Solution best = allowedNeighbors.get(0);
		for (Solution s : allowedNeighbors) {
			if (best.getValue() < s.getValue()) {
				best = s;
			}
		}
		return best;
	}

	/**
	 * Stop Criteria
	 */
	public void updateStopCriteria() {
		switch (stopCriteriaType) {
		case ITERATIONS:
			iterations++;
			break;
		case TIME:
			if (startTime == -1) { // falls noch nie gestartet
				startTime = System.currentTimeMillis(); // setze start
			}
			break;
		case JUSTEND:
			break;
		default:
			break;

		}
	}

	public boolean isStopCriteriaReached() {
		switch (stopCriteriaType) {
		case JUSTEND:
			return true; //wenn sofort aufgehört werden soll
		case ITERATIONS:
			return iterations >= maxIterations; // wenn iterationen
												// überschritten
		case TIME:
			// System.out.println("StartTime: "+startTime);
			// System.out.println("Now: "+System.currentTimeMillis());
			return startTime + stopCriteriaMaxTime <= System.currentTimeMillis(); // wenn
																					// zeit
																					// vorbei
		default:
			return true;
		}
	}

	/**
	 * Start Solutions
	 */

	/**
	 * Returns a StartSolution depending on set Type
	 * 
	 * @param instance
	 * @return
	 */
	private Solution getStartSolution(Instance instance) {
		switch (startSolutionType) {
		case FIRSTFILLED:
			return getFirstFilledStartSolution(instance);
		case EMPTY:
			return getEmtpyStartSolution(instance);
		default:
			return new Solution(instance);
		}
	}

	/**
	 * Returns an random Start Solution, not taken because hard to compare with
	 * other
	 * 
	 * @param instance
	 * @return
	 */
	private Solution getRandomStartSolution(Instance instance) {
		Solution startSol = new Solution(instance);
		for (int i = 0; i < instance.getSize(); i++) {
			startSol.set(i, r.nextBoolean() ? 1 : 0);
			// wir erlauben nur eine gueltige Random startLoesung
			if (!startSol.isFeasible()) {
				startSol.set(i, 0);
			}
		}

		return startSol;
	}

	/**
	 * Returns a empty Start Solution
	 * 
	 * @param instance
	 * @return
	 */
	private Solution getEmtpyStartSolution(Instance instance) {
		Solution startSol = new Solution(instance);
		for (int i = 0; i < instance.getSize(); i++) {
			startSol.set(i, 0);
		}

		return startSol;
	}

	/**
	 * Returns a Solution, where all items are taken if possible (from first
	 * item to last)
	 * 
	 * @param instance
	 * @return
	 */
	private Solution getFirstFilledStartSolution(Instance instance) {
		Solution startSol = new Solution(instance);
		for (int i = 0; i < instance.getSize(); i++) {
			startSol.set(i, 1);
			if (!startSol.isFeasible()) {
				startSol.set(i, 0);
			}
		}

		return startSol;
	}

	/**
	 * Returns a Bad Start Solution for given Instance with 10 Items
	 * 
	 * @param instance
	 * @return
	 */
	private Solution getFor10ItemsBadstartSolution(Instance instance) {
		// 1 1 1 1 1 0 0 1 0 1 is a very good solution, so flip it

		Solution startSol = new Solution(instance);
		startSol.set(5, 1);
		startSol.set(6, 1);
		startSol.set(8, 1);

		return startSol;
	}

}