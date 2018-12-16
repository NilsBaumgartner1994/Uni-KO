package blatt8;

import java.io.IOException;

/**
 * Einfache TSP Lösung
 * 
 * @author nbaumgartner
 *
 */
public class TSPSolution {

	TSPInstance instance;
	
	Ant bestAnt;
	float bestTourLength = Integer.MAX_VALUE; //beste Länge auf Infinite

	public float[][] duft; //alle düfte

	/**
	 * Erstellen einer Lösung
	 * @param instance
	 */
	public TSPSolution(TSPInstance instance) {
		this.instance = instance;
		duft = new float[instance.knoten][instance.knoten];
	}

	/**
	 * Duft Auslesen aus Tabelle, sicher
	 * @param from
	 * @param to
	 * @return
	 * @throws IOException
	 */
	public float getDuft(int from, int to) throws IOException {
		if (from < 0 || from > instance.knoten) {
			throw new IOException("From is too far or to low (" + from + ")");
		}
		if (to < 0 || to > instance.knoten) {
			throw new IOException("To is too far or to low (" + to + ")");
		}
		return duft[from][to];
	}
	
	/**
	 * Lässt alle Düfte um einen Factor verdunsten
	 * @param factor
	 */
	public void verdunsten(float factor){
		for(int i=0;i<this.duft.length;i++){
			for(int j=0;j<this.duft[i].length;j++){
				this.duft[i][j] = this.duft[i][j]*(1f-factor);
			}
		}
	}

	/**
	 * Setze den Duft an einer Stelle, sicher
	 * @param from
	 * @param to
	 * @param duft
	 * @throws IOException
	 */
	public void setDuft(int from, int to, float duft) throws IOException {
		if (from < 0 || from > instance.knoten) {
			throw new IOException("From is too far or to low (" + from + ")");
		}
		if (to < 0 || to > instance.knoten) {
			throw new IOException("To is too far or to low (" + to + ")");
		}
		this.duft[from][to] = duft < 0 ? 0f : duft;
	}

	/**
	 * Gebe Wert der Besten Tour aus
	 * @return
	 */
	public float getValue() {
		return bestTourLength;
	}

	/**
	 * Getter der INstance
	 * @return
	 */
	public TSPInstance getInstance() {
		return instance;
	}

}
