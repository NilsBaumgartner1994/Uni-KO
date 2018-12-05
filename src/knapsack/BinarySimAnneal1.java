package knapsack;

import java.util.List;
import java.util.Random;

/**
 * Solution of a integer or binary knapsack problem
 *
 * @author Nils Baumgartner
 */
public class BinarySimAnneal1 implements SolverInterface<Solution> {

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
	public long maxIterations = 1000000; // Maximale anzahl an Iterationen

	// Cooling
	public COOLING coolingType;
	public double lastTi = 100.0; // startwert für die Reihe Ti
	public double alpha = 0.9; // senkfaktor bei geometric cooling

	public enum COOLING {
		GEOMETRICCOOLING, WURZEL;
	}

	public enum STARTSOLUTION {
		EMPTY, FIRSTFILLED;
	}

	public enum STOPCRITERIA {
		TIME, ITERATIONS;
	}

	/**
	 * Start Variablen zum einstellen des Solvers
	 */
	public void setVariables() {
		startSolutionType = STARTSOLUTION.FIRSTFILLED;
		coolingType = COOLING.WURZEL;
		stopCriteriaType = STOPCRITERIA.TIME;
	}

	@Override
	public Solution solve(Instance instance) {
		
		this.instance = instance;

		setVariables(); // setzt die art der sim annealing

		// Nun der Alg. nach Script S.36 angepasst an maximierung !
		iterations = 0; // 1.
		Solution s = getStartSolution(instance);// 2. init
		Solution sStar = s; // 3.
		int cStar = sStar.getValue(); // 4.
		do { // 5.
			Solution sApo = getRandomSolution(s); // 6
			if (sApo.getValue() >= s.getValue() || r.nextDouble() < getAbkuehlWert(sApo, s, iterations)) { // 7.
				s = sApo; // 8
				if (sApo.getValue() > cStar) { // 9
					sStar = sApo; // 10
					cStar = sApo.getValue(); // 11
				} // 12
			} // 13
			iterations += 1; // 14
			updateStopCriteria();
		} while (!isStopCriteriaReached());

		// Ausgabe für messungen
		// System.out.println("Zeit: " +
		// (System.currentTimeMillis()-startTime)/1000);
		// System.out.println("Iteration: " + iterations + " Solution: " +
		// sStar.toString());
		// System.out.println("Value: " + sStar.getValue());
		// System.out.println("Cap: " + sStar.getWeight() + "/" +
		// instance.getCapacity());

		return sStar;
	}

	/**
	 * HIlfsfunktion für den Abkühlwert im Script S.36 Z.7
	 * 
	 * @param sApo
	 * @param s
	 * @param i
	 * @return
	 */
	private double getAbkuehlWert(Solution sApo, Solution s, int i) {
		int sApoVal = sApo.getValue();
		int sVal = s.getValue();
		double ti = getTi(i);
		return Math.pow(Math.E, -((double) (sVal - sApoVal)) / ti);
	}

	/**
	 * Returns Ti abhängig von der Abkühlart
	 * 
	 * @param i
	 * @return
	 */
	public double getTi(int i) {
		switch (coolingType) {
		case GEOMETRICCOOLING:
			return lastTi * alpha;
		case WURZEL:
			return lastTi / (i + 1); // einfaches 1/x
		default:
			return 0;
		}
	}

	/**
	 * Erzeugt eine Zufällige Nachbar Lösung
	 * 
	 * @param s
	 * @return
	 */
	private Solution getRandomSolution(Solution s) {
		Solution randomSol = new Solution(s);
		do {
			randomSol = new Solution(s);
			int pos = r.nextInt(instance.getSize()); // zufällige position
			randomSol.set(pos, (randomSol.get(pos) + 1) % 2); // flip
		} while (!randomSol.isFeasible()); // nur erlaubte lösungen, bringt
											// sonst ja nix
		// System.out.println("RandomSol: "+randomSol.toString());
		return randomSol;
	}

	/**
	 * Stop Criteria
	 */
	public void updateStopCriteria() {
		switch (stopCriteriaType) {
		case ITERATIONS:
			// nothing todo, cause we already got iterations
			break;
		case TIME:
			if (startTime == -1) { // falls noch nie gestartet
				startTime = System.currentTimeMillis(); // setze start
			}
			break;
		default:
			break;

		}
	}

	public boolean isStopCriteriaReached() {
		switch (stopCriteriaType) {
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