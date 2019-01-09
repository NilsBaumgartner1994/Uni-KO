# Probleminstanz
param file := "knapsacks/rucksack0500.txt";

# Auslesen der Itemanzahl
param n := read file as "1n" skip 0 use 1;

# Auslesen des maximalen Gewichts
param maxWeight := read file as "1n" skip 1+n use 1;

# Erstelle das Set fuer alle Items
set N := {1 .. n};

# Parsen der Datei
var items[N] binary; # Erstelle N items
param weight[N] := read file as "1n" skip 1 use n; # Setze Gewicht der Items
param value[N] := read file as "2n" skip 1 use n; # Setze Nutzen der Items

# Zielfunktion, mitnahme von allen N items mit deren Nutzen
maximize rucksack: sum <j> in N : items[j] * value[j];

# Alle Items zusammen duerfen das maximale Gewicht nicht ueberschreiten
subto maxWeightConstraint: (sum <j> in N : items[j] * weight[j]) <= maxWeight;
