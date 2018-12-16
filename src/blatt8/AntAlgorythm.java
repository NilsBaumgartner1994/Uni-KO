package blatt8;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Solution of a integer or binary knapsack problem
 *
 * @author Nils Baumgartner
 */
public class AntAlgorythm {

	TSPInstance instance;

	List<Ant> ants;

	public final int MAXANTS = 100; //maximale Ameisen
	public final int MAXITERATIONS = 1000; //maximale Iterationen

	public final float VERDUNSTFAKTOR = 0.1f; //verdunstungsfaktor

	/**
	 * Löse ein TSP Problem und gebe die Lösung aus
	 * @param instance
	 * @return
	 */
	public TSPSolution solve(TSPInstance instance) {
		this.instance = instance;
		TSPSolution world = new TSPSolution(instance);

		int iteration = 0;

		while (iteration < MAXITERATIONS) { //Iterationen als Abbruchkrit
			initAnts(world); //erstelle Neue Ameisen
			for (Ant ant : ants) { //für alle Ameisen
				for (int i = 2; i < instance.knoten; i++) { //besuche alle Knoten
					ant.makeMove(); //nach deinem Schema
				}
			}
			world.verdunsten(VERDUNSTFAKTOR); //lass feromone verdunsten
			for (Ant ant : ants) { //für alle Ameisen
				float tourLength = ant.calcTourLength(); //berechne strecke
				if (tourLength < world.bestTourLength) { //wenn neue kürzeste Strecke
					world.bestAnt = ant; //merke diese
					world.bestTourLength = tourLength;
				}

				ant.addFeromonsOnEdges(tourLength); //lege feromone auf Kanten
			}
			iteration++;
//			System.out.println(iteration);
		}

		return world;
	}

	/**
	 * Erstelle Neue Ameisen
	 * @param world
	 */
	public void initAnts(TSPSolution world) {
		ants = new LinkedList<Ant>();
		for (int i = 0; i < MAXANTS; i++) {
			ants.add(new Ant(world, i % instance.knoten)); //setze diese auf einen Knoten, wie nach Script
		}
	}

}