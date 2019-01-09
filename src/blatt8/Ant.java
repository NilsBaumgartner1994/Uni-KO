package blatt8;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Eine einfache Ameise
 *
 * @author Nils Baumgartner
 */
public class Ant {

	static Random r = new Random();

	public final float ALPHA = 0.5f; // pheromoneinfluss
	public final float BETA = 0.5f; // heuristik einfluss

	TSPSolution world; // die Welt
	int position; // die Position des Knotens auf welcher diese sich befindet

	List<Integer> unbekannteKnoten; // alle nicht besuchte Knoten
	List<Integer> besuchteKnoten; // alle besuchten Knoten

	/**
	 * Setze eine Ameise in die Welt an eine Position
	 * 
	 * @param world
	 * @param position
	 */
	public Ant(TSPSolution world, int position) {
		this.world = world;
		this.position = position;
		initUnbekannteKnoten();
		besuchteKnoten = new LinkedList<Integer>();
		visitedKnoten(position);
	}

	/**
	 * Berechnet die Tourlänge anhand der besuchten Knoten
	 * 
	 * @return
	 */
	public float calcTourLength() {
		float tour = 0;
		int from = besuchteKnoten.get(0);
		for (int i = 1; i < besuchteKnoten.size(); i++) { // alle Knoten
															// nacheinder
			int nextKnot = besuchteKnoten.get(i);
			try {
				tour += this.world.instance.getCosts(from, nextKnot); // hole
																		// Kosten
																		// der
																		// Kante
																		// und
																		// addiere
			} catch (IOException e) {

			}
		}

		// letzte kante zurück zum Start, da es ja ein TSP ist
		int ende = besuchteKnoten.get(besuchteKnoten.size() - 1);
		int start = besuchteKnoten.get(0);
		try {
			tour += this.world.instance.getCosts(ende, start);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tour; // gebe gesmat kosten aus
	}

	/**
	 * Erstelle eine Liste aller Knoten, welche zu beginn ja unbekannt sind
	 */
	private void initUnbekannteKnoten() {
		this.unbekannteKnoten = new LinkedList<Integer>();
		for (int i = 0; i < world.instance.knoten; i++) {
			this.unbekannteKnoten.add(i);
		}
	}

	/**
	 * Entfernt einen Unbekannten knoten und fügt diesen als bekannten wieder
	 * ein
	 * 
	 * @param knoten
	 */
	private void visitedKnoten(int knoten) {
		int index = -1;
		for (int i = 0; i < this.unbekannteKnoten.size(); i++) { // finde den
																	// Knoten
			if (this.unbekannteKnoten.get(i) == knoten) {
				index = i; // merke den index
			}
		}
		if (index > -1) { // wenn gefunden
			this.unbekannteKnoten.remove(index); // lösche index
		}

		besuchteKnoten.add(knoten); // füge knoten ein
	}

	/**
	 * Berechnet den besten nächsten Knoten und besucht diesen
	 */
	public void makeMove() {
		int nextKnoten = chooseNextKnot();
		visitedKnoten(nextKnoten);
		// System.out.println("From: "+position+" --> "+nextKnoten);
		this.position = nextKnoten;
	}

	/**
	 * Legt Pheromone auf alle Pfade in der Welt, abhängig von der TourLänge
	 * 
	 * @param tourLength
	 */
	public void addFeromonsOnEdges(float tourLength) {
		int from = besuchteKnoten.get(0);
		for (int i = 1; i < besuchteKnoten.size(); i++) { // über alle besuchten
															// Kannten
			int nextKnot = besuchteKnoten.get(i);
			updateFeromons(from, nextKnot, 1 / tourLength); // lege Pheromone
															// rauf, wie nach
															// script
		}

		// letzte kante
		int ende = besuchteKnoten.get(besuchteKnoten.size() - 1);
		int start = besuchteKnoten.get(0);
		updateFeromons(ende, start, 1 / tourLength); // lege pheromone auf letze
														// kannte
	}

	/**
	 * Lege Feromone auf eine Kante
	 * 
	 * @param from
	 * @param to
	 * @param feromon
	 */
	private void updateFeromons(int from, int to, float feromon) {
		try {
			float oldFeromon = this.world.getDuft(from, to); // alter wert
			this.world.setDuft(from, to, feromon + oldFeromon); // auf Kante
																// legen
			this.world.setDuft(to, from, feromon + oldFeromon); // von beiden
																// Seiten
																// betrachtet
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Erhalte nächsten zu gehenden Knoten
	 * 
	 * @return
	 */
	private int chooseNextKnot() {
		float chance = r.nextFloat();
		float[] props = caclPropapilitysForAllKnots(); // berechne wahr. für
														// alle übrigen Kanten
		// float sum = 0;
		// for (float prop : props) {
		// System.out.printf("%1.2f, ",prop);
		// sum += prop;
		// }
		// System.out.println("");
		// System.out.println(sum);

		for (int i = 0; i < props.length; i++) { // wähle eine kante abhängig
													// von ihrer wahr aus
			chance -= props[i];
			if (chance <= 0) { //wenn wahr. erreicht
				int knoten = this.unbekannteKnoten.get(i);
				return knoten; 
			}
		}
		throw new RuntimeException("Kein Knoten kommt in Frage?");
	}

	/**
	 * Hole alle wahr. aller pbrigen Kanten
	 * Werte zwischen 0 und 1
	 * 
	 * @return
	 */
	float[] caclPropapilitysForAllKnots() {
		float[] props = new float[this.unbekannteKnoten.size()];
		for (int i = 0; i < props.length; i++) { //für alle kanten
			int knoten = this.unbekannteKnoten.get(i); //nehme knoten
			props[i] = calcPropapilityForKnot(knoten); //berechne wahr. zu dem knoten
		}
		return props;
	}

	/**
	 * Berechne wahr. zu einem Knoten
	 * @param knoten
	 * @return
	 */
	public float calcPropapilityForKnot(int knoten) {
		// pk = (pheromone zu k)^alpha * (kosten zu k)^beta
		// -------------------------------------------
		// Summe aller anderen ((pheromone zu k)^alpha * (kosten zu k)^beta)

		//Numerator
		float obererTeil = getEinflussVonKante(this.position, knoten);

		float untererTeil = 0; //Denominator nach Script
		for (Integer unbesuchterKnoten : this.unbekannteKnoten) {
			untererTeil += getEinflussVonKante(this.position, unbesuchterKnoten);
		}

		// System.out.println("Oben: "+obererTeil);
		// System.out.println("Unten: "+untererTeil);

		return obererTeil / untererTeil;
	}

	/**
	 * Berechne Einfluss eines Knotens
	 * @param from
	 * @param to
	 * @return
	 */
	private float getEinflussVonKante(int from, int to) {
		return getDuftEinflussVonKante(from, to) * getKostenEinflussVonKante(from, to);
	}

	/**
	 * Gebe DuftEinfluss eines Knotens
	 * @param from
	 * @param to
	 * @return
	 */
	private float getDuftEinflussVonKante(int from, int to) {
		try {
			// System.out.println(this.world.getDuft(from, to));
			return this.world.getDuft(from, to) * ALPHA + 1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * Gebe Heuristik Einfluss eines Knotens
	 * @param from
	 * @param to
	 * @return
	 */
	private float getKostenEinflussVonKante(int from, int to) {
		try {
			return this.world.instance.getCosts(from, to) * BETA;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

}