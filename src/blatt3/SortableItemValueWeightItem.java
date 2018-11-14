package knapsack;

import java.util.ArrayList;
import java.util.List;

/**
 * Dies ist eine kleine private Hilfsklasse, mit welcher ich die Items sortieren
 * m�chte ohne viel Auffwand. Die Klasse ist klein und denke ich
 * selbsterkl�rend. Sie soll halt nur eben ein Item nach Nutzen/Kosten sortieren
 * k�nnen.
 * 
 * @author nilsb
 *
 */
public class SortableItemValueWeightItem implements Comparable {

	public int itemNumber; // die Item Nummer
	public double valueperweight; // das Nutzen Kosten Verh�ltnis

	public static List<SortableItemValueWeightItem> getSortedItemValueWeightItem(Instance instance) {
		List<SortableItemValueWeightItem> list = getListItemValueWeightItems(instance); // hole
																						// eine
																						// Liste
																						// mit
																						// Items
		list.sort(null); // sortiere diese
		return list;
	}

	private static List<SortableItemValueWeightItem> getListItemValueWeightItems(Instance instance) {
		List<SortableItemValueWeightItem> list = new ArrayList<SortableItemValueWeightItem>(); // neue
																								// Liste

		int amountItems = instance.getSize();
		for (int i = 0; i < amountItems; i++) { // durchlaufe alle Items
			// Erstelle neues <SortableItemValueWeightItem> und f�ge der Liste
			// hinzu
			SortableItemValueWeightItem element = new SortableItemValueWeightItem(i, instance.getValue(i),
					instance.getWeight(i));
			list.add(element);
		}

		return list;
	}

	// einfacher Konstruktor
	private SortableItemValueWeightItem(int itemNumber, double value, double weight) {
		this.itemNumber = itemNumber;
		this.valueperweight = value / weight; // Berechne Nutzen/Kosten
												// Quotienten
	}

	/**
	 * CompareTo Methode, geeignet f�r SortableItemValueWeightItem
	 * 
	 * @param Object
	 *            obj sollte vom Typ SortableItemValueWeightItem sein
	 * @return -1 falls dieses Objekt einen h�heren Nutzen/Kosten Faktor hat
	 * @return 1 wenn dieser kleiner ist
	 * @return 0 sonst
	 */
	@Override
	public int compareTo(Object obj) {
		if (obj instanceof SortableItemValueWeightItem) { // minimale Security
			SortableItemValueWeightItem item = (SortableItemValueWeightItem) obj;
			if (this.valueperweight > item.valueperweight) { // entweder bist du
																// gr��er
				return -1;
			} else if (this.valueperweight < item.valueperweight) { // oder
																	// kleiner
				return 1;
			}
			return 0; // sonst gleich
		} else {
			return 0;
		}
	}

	/**
	 * Einfach toString Methode f�r Debugging
	 */
	@Override
	public String toString() {
		return "[" + this.itemNumber + ";" + this.valueperweight + "]";
	}

}