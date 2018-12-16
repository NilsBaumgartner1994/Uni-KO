package blatt8;

import java.io.*;
import knapsack.Instance;

public class TSPReader {

	/**
	 * Einfaches Einlesen einer TSP Datei
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static TSPInstance readInstance(String filename) throws IOException {

		TSPInstance instance = null;

		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			int n = Integer.parseInt(reader.readLine().trim());
			instance = new TSPInstance(n); //knoten anzahl

			boolean end = false;
			for (int i = 1; !end ; i++) { //über alle Zeilen
				String lineRaw = reader.readLine();
				if(lineRaw==null){
					end = true;
					continue;
				}
				String[] line = lineRaw.trim().split("\\s+");
				
				if (line.length != 3) {
					throw new IOException("TSP format invalid");
				}
				//Setze Kosten für 2-Dim Array
				//System.out.println("From: "+(Integer.parseInt(line[0])-1)+" --> "+(Integer.parseInt(line[1])-1)+" with: "+Float.parseFloat(line[2]));
				instance.setCosts(Integer.parseInt(line[0])-1, Integer.parseInt(line[1])-1, Float.parseFloat(line[2]));
				instance.setCosts(Integer.parseInt(line[1])-1, Integer.parseInt(line[0])-1, Float.parseFloat(line[2])); // in beide richtungen
			}
		}
		return instance;
	}
}
