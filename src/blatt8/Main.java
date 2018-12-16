package blatt8;

import java.io.IOException;

import knapsack.BinaryGeneric;
import knapsack.BinarySimAnneal1;
import knapsack.BinarySolver;
import knapsack.BinaryTabuList;
import knapsack.GenericSolution;
import knapsack.Instance;
import knapsack.Logger;
import knapsack.Reader;
import knapsack.SolverInterface;

public class Main {
	public static boolean debug = true; //debug auf False, wenn über args die Datei gegeben werden soll
	//sonst bitte bei getArguments schauen
	
	private static final String usage = "Usage: [-v|--verbose] <filename>";

	/**
	 * Löse das TSP Problem und gebe die Zeit und Lösung aus
	 * @param solver
	 * @param instance
	 */
	public static void runSolver(AntAlgorythm solver,
			TSPInstance instance) {
		System.out.println("=== " + solver.getClass().getName() + " ===");
		long start = System.currentTimeMillis();
		TSPSolution solution = solver.solve(instance);
		long end = System.currentTimeMillis();
		System.out.println("value = " + solution.getValue());
		System.out.printf("time = %.3fs\n", (end - start) / 1000.0);
		for(int knoten : solution.bestAnt.besuchteKnoten){
			System.out.print(knoten+",");
		}
		System.out.println("");
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
			
			TSPInstance instance = TSPReader.readInstance(args[1]);

			System.out.println("###############################################");
	
			System.out.println("###############################################");
			System.out.println();
			runSolver(new AntAlgorythm(),instance);
			break;
		default:
			throw new IllegalArgumentException(usage);
		}
	}

	/**
	 * Auswahl des Problems
	 * @return
	 */
	public static String[] getArguments() {
		String[] args = new String[2];
		
		args[0] = "-v";
		
		String folder = "/Users/nbaumgartner/Documents/GitHub/Uni-KO/src/blatt8/assets/";
		String problem = "tsp1.txt";
		
		args[1] = folder+problem;

		return args;
	}

}
