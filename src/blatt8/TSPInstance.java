package blatt8;

import java.io.IOException;

public class TSPInstance {
	
	public float[][] graph; //einfacher graph, ggf. für nicht bidirectionale wege
	public int knoten; //anzahl an Knoten
	
	/**
	 * Erstellen eines TSP Problems
	 * @param knoten
	 */
	public TSPInstance(int knoten){
		this.knoten = knoten;
		this.graph = new float[knoten][knoten];
	}
	
	/**
	 * Setze sicher die kosten in dem TSP für eine Kante
	 * @param from
	 * @param to
	 * @param costs
	 * @throws IOException
	 */
	public void setCosts(int from, int to, float costs) throws IOException{
		if(from < 0 || from > knoten){
			throw new IOException("From is too far or to low ("+from+")");
		}
		if(to < 0 || to > knoten){
			throw new IOException("To is too far or to low ("+to+")");
		}
		if(costs < 0){
			throw new IOException("I dont allow negative costs! From: "+from+" --> TO:"+to+" costs: "+costs);
		}
		graph[from][to] = costs;
	}
	
	/**
	 * Erhalte die Kosten einer Kante, zwischen zwei Knoten, sicher
	 * @param from
	 * @param to
	 * @return
	 * @throws IOException
	 */
	public float getCosts(int from, int to) throws IOException{
		if(from < 0 || from > graph.length){
			throw new IOException("From is too far or to low ("+from+")");
		}
		if(to < 0 || to > graph[from].length){
			throw new IOException("To is too far or to low ("+to+")");
		}
		return graph[from][to];
	}
	
	/**
	 * Gebe den Graphen aus
	 */
	public void printTable(){
		for(int i=0;i<knoten;i++){
			for(int j=0;j<knoten;j++){
				try {
					System.out.printf("%8.2f",getCosts(i, j));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("");
		}
	}
	
}
