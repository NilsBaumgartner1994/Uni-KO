package blatt1;

import java.io.IOException;

import knapsack.BinarySimAnneal1;
import knapsack.BinarySolver;
import knapsack.GenericSolution;
import knapsack.Instance;
import knapsack.Logger;
import knapsack.Reader;
import knapsack.SolverInterface;

public class Main {
	public static boolean debug = true;
	
	private static final String usage = "Usage: [-v|--verbose] <filename>";

	public static <SolutionType extends GenericSolution<?>> void runSolver(SolverInterface<SolutionType> solver,
			Instance instance, boolean binary) {
		System.out.println("=== " + solver.getClass().getName() + " ===");
		long start = System.currentTimeMillis();
		SolutionType solution = solver.solve(instance);
		long end = System.currentTimeMillis();
		if (instance.getSize() <= 60) {
			System.out.println("solution = " + solution);
		}
		System.out.println("value = " + solution.getValue());
		System.out.printf("time = %.3fs\n", (end - start) / 1000.0);
		assert solution.getInstance() == instance : "Solution is for another instance!";
		assert solution.isFeasible() : "Solution is not feasible!";
		if (binary) {
			assert solution.isBinary() : "Solution is not binary!";
		}
	}

	public static <SolutionType extends GenericSolution<?>> void runSolver(SolverInterface<SolutionType> solver,
			Instance instance) {
		runSolver(solver, instance, true);
	}

	public static void main(String[] args) throws IOException {
		if (debug) {
			args = getArguments();
		}

		switch (args.length) {
		case 2:
			if (args[0].equals("-v") || args[0].equals("--verbose")) {
				Logger.enable();
			} else {
				throw new IllegalArgumentException(usage);
			}
		case 1:
			Instance instance = Reader.readInstance(args[args.length - 1]);

			System.out.println("###############################################");
			System.out.println("# Instance file: " + args[0]);
			System.out.println("# Number of items: " + instance.getSize());
			System.out.println("# Capacity of knapsack: " + instance.getCapacity());
			System.out.println("###############################################");
			System.out.println();
			runSolver(new BinarySimAnneal1(),instance,true);
			break;
		default:
			throw new IllegalArgumentException(usage);
		}
	}

	public static String[] getArguments() {
		String[] args = new String[2];
		
		args[0] = "-v";
		
		String folder = "/Users/nbaumgartner/Documents/GitHub/Uni-KO/bin/blatt1/assets/";
		String problem = "rucksack0050.txt";
		
		args[1] = folder+problem;

		return args;
	}

}
