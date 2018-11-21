package skyscrapers;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * A skyscrapers solver.
 *
 * @author nbaumgartner
 */
public class SkyscSolver {

	/**
	 * Enum für 4 Richtungen
	 * 
	 * @author nbaumgartner
	 *
	 */
	public enum Direction {
		TOP, LEFT, RIGHT, BOTTOM;
	}

	/**
	 * Variablen
	 */
	Model model;
	int size;
	int[][] gameField;
	IntVar[][] rows;
	IntVar[][] cols;
	Instance instance;
	int[] ones;

	/**
	 * Super tolle main
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String args[]) throws IOException {
		if (args != null && args.length > 0) {
			System.out.println("Read file " + args[0] + ".");
			Instance instance = Reader.readSkyscInstance(args[0]);
			System.out.println("Gamefield:");
			instance.printGamefield();
			long start = System.currentTimeMillis();
			SkyscSolver.solve(instance);
			long end = System.currentTimeMillis();
			System.out.printf("time = %.3fs\n", (end - start) / 1000.0);
		} else {
			System.out.println("Please enter a skyscrapers file.");
		}
	}

	/**
	 * Loesen eines Skyscrappers problem
	 * 
	 * @param problem
	 * 
	 */
	public static void solve(Instance instance) {
		// 1. Create model
		// 2. Create Variables
		// 3. add constrains
		// Alles in der SkyscSolver Klasse
		SkyscSolver skySolver = new SkyscSolver(instance, "My Skysc");

		// 4. Get solver and solve for every possible Solution
		List<Solution> solutions = skySolver.getSolver().findAllSolutions();

		// 5. Print solutions
		System.out.println("Number of solutions: " + solutions.size());
		int cnt = 1;

		for (Solution solution : solutions) {
			int[][] solutionArray = new int[skySolver.size][skySolver.size];

			for (int i = 0; i < skySolver.size; ++i) {
				for (int j = 0; j < skySolver.size; ++j) {
					solutionArray[i][j] = solution.getIntVal(skySolver.rows[i][j]);
				}
			}

			instance.setSolution(solutionArray);
			System.out.println("------- solution number " + cnt + "-------");
			instance.printSolution();

			++cnt;
		}
	}

	public SkyscSolver(Instance instance, String name) {
		this.instance = instance;

		model = new Model(name); // 1. Create model

		// 2. Create Variables
		size = instance.getGamefieldSize();

		// just some1's :D
		ones = new int[size];
		Arrays.fill(ones, 1);

		gameField = instance.getGamefield();

		rows = new IntVar[size][size];
		cols = new IntVar[size][size];

		// Populate all Variables and post them to the Model
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				int value = gameField[y][x];
				boolean fixedValue = gameField[y][x] != 0;
				int min = fixedValue ? value : 1;
				int max = fixedValue ? value : size;
				rows[y][x] = model.intVar("SkyScraper(" + y + '|' + x + ")", min, max);
				cols[x][y] = rows[y][x];
			}
		}

		// All Variables in one Row or Col should be different.
		for (int y = 0; y < gameField.length; y++) {
			model.allDifferent(rows[y]).post();
			model.allDifferent(cols[y]).post();
		}

		// Make sure the Visible-Constraint is fulfilled for every Direction
		createAllVisibilityConstraintsFor(Direction.TOP);
		createAllVisibilityConstraintsFor(Direction.BOTTOM);
		createAllVisibilityConstraintsFor(Direction.LEFT);
		createAllVisibilityConstraintsFor(Direction.RIGHT);
	}

	/**
	 * Getter for the solver
	 * 
	 * @return
	 */
	public Solver getSolver() {
		return model.getSolver();
	}

	/**
	 * Creates the Visibility Constrains for Direction
	 * 
	 * @param direction
	 *            from where should be looked
	 */
	private void createAllVisibilityConstraintsFor(Direction direction) {
		// First create the initial Visible-Matrix. If the Value at y and x is
		// TRUE, this Skyscraper is
		// visible from the Direction handled in this Method.
		BoolVar[][] visible = createInitBoolArray(direction);

		// First Skysc. seen are always true, so skip them
		int rowStart = direction == Direction.TOP ? 1 : 0;
		int rowEnd = direction == Direction.BOTTOM ? size - 1 : size;
		int colStart = direction == Direction.LEFT ? 1 : 0;
		int colEnd = direction == Direction.RIGHT ? size - 1 : size;

		boolean topOrBottom = direction == Direction.TOP || direction == Direction.BOTTOM;
		boolean rightOrBottom = direction == Direction.RIGHT || direction == Direction.BOTTOM;
		IntVar[][] vars = topOrBottom ? rows : cols;

		/**
		 * Constrains for smaller ones
		 */
		for (int y = rowStart; y < rowEnd; y++) {
			for (int x = colStart; x < colEnd; x++) {
				IntVar actualTower = rows[y][x];
				int staticPos = topOrBottom ? x : y; // look for row or colum
				int border = topOrBottom ? y : x; // actual position in row/col
				// 0-border or border-size
				int startPos = rightOrBottom ? border + 1 : 0;
				int endPos = rightOrBottom ? size : border;
				int amountSmallerTowers = rightOrBottom ? size - (startPos) : border;

				// Skysc which should be lower, depending on direction
				IntVar[] smallerTowers = new IntVar[amountSmallerTowers];
				// take all smaller ones
				for (int pos = startPos; pos < endPos; pos++) {
					smallerTowers[pos - (startPos)] = vars[pos][staticPos];
				}

				// tell which towers have to be smaller than the actualTower
				model.reification(visible[y][x], SkyscSolver.allLess(model, actualTower, smallerTowers));
			}
		}

		/**
		 * Post the visibility
		 */
		int[] skyScInDir = null;
		switch (direction) {
		case TOP:
			skyScInDir = instance.getNorth();
			break;
		case RIGHT:
			skyScInDir = instance.getEast();
			break;
		case BOTTOM:
			skyScInDir = instance.getSouth();
			break;
		case LEFT:
			skyScInDir = instance.getWest();
			break;
		}

		BoolVar[] boolVars = new BoolVar[size]; // clean boolean array
		for (int i = 0; i < size; i++) { // look in direction
			if (skyScInDir[i] != 0) { // If there is View-Distance
				for (int j = 0; j < size; j++) { // run in direction
					int y = topOrBottom ? j : i;
					int x = topOrBottom ? i : j;
					boolVars[j] = visible[y][x]; // copy the visibility
				}
				// set the visibility
				model.scalar(boolVars, ones, "=", skyScInDir[i]).post();
			}
		}
	}

	/**
	 * Create a initiated Visibility Boolean 2-Dim-Array, where the borders are
	 * always true, cause the skyscapers are always seen
	 * 
	 * @param direction
	 *            the direction from where is looked at
	 */
	private BoolVar[][] createInitBoolArray(Direction direction) {
		// Create the Array that should be returned
		BoolVar[][] skyscVisible = new BoolVar[size][size];
		String name = "SkyScaperVisibility[" + direction.name() + "](";

		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				boolean borderSc = (direction == Direction.TOP && y == 0)
						|| (direction == Direction.RIGHT && x == size - 1)
						|| (direction == Direction.BOTTOM && y == size - 1) || (direction == Direction.LEFT && x == 0);

				if (borderSc) { // always visible
					skyscVisible[y][x] = model.boolVar(name + y + '|' + x + ")", true);
				} else { // unkown visibility
					skyscVisible[y][x] = model.boolVar(name + y + '|' + x + ")");
				}
			}
		}

		// Returns the Matrix
		return skyscVisible;
	}

	/**
	 * Constrainthelper, for a model, where all values of an array are smaller
	 * than a given max value
	 * 
	 * @param model
	 *            the Model on which applied
	 * @param max
	 *            the upper bound
	 * @param smaller
	 */
	public static Constraint allLess(Model model, IntVar max, IntVar[] smaller) {
		Constraint[] smallerCon = new Constraint[smaller.length];

		for (int i = 0; i < smallerCon.length; i++) {// Create constraints
			smallerCon[i] = model.arithm(smaller[i], "<", max);
		}

		return model.and(smallerCon); // aplly to model and return the constrain
	}
}
