# MaschinenKosten
param hemdMaschineWochenKosten := 1000;
param hoseRockMaschineWochenKosten := 2000;

# Tabelle
param stundenProHemd := 3;
param materialProHemd := 1.2;
param gewinnProHemd := 30;

param stundenProRock := 2;
param materialProRock := 0.6;
param gewinnProRock := 45;

param stundenProHose := 1;
param materialProHose := 1.4;
param gewinnProHose := 60;


# Contraints Pro Woche
param maxArbeitsstunden := 150;
param maxMaterial := 160;

# Variable Stueckanzahl der Produktion
var hemdenAnz integer >= 0;
var rockAnz integer >= 0;
var hosenAnz integer >= 0;

# Maximale Produktions Anzahl (anpassbar)
param maxAnzahlProdukte := 99; #Mehr Produkte werden nie hergestellt

# Variable ob eine Maschine angeschafft wird
var hemdenMaschineVorhanden integer >= 1 <= 1+1; #Binaere 1 = kein mal oder maschine kaufen
var hosenRockMaschineVorhanden integer >= 1 <= 1+1; #Binaere 1 = kein mal oder maschine kaufen

# Zielfunktion
maximize money: (hemdenAnz * gewinnProHemd) + (rockAnz * gewinnProRock) + (hosenAnz * gewinnProHose) - (hemdenMaschineVorhanden * hoseRockMaschineWochenKosten) - (hosenRockMaschineVorhanden * hemdMaschineWochenKosten);

# Erstelle Abhaengigkeit, dass Produkte nur mit entsprechender Maschine hergestellt werden koennen
subto maximaleHemden: hemdenAnz <= hemdenMaschineVorhanden * maxAnzahlProdukte;
subto maximaleRoecke: rockAnz <= hosenRockMaschineVorhanden * maxAnzahlProdukte;
subto maximaleHosen: hosenAnz <= hosenRockMaschineVorhanden * maxAnzahlProdukte;

# Resourcen Limitierung
subto arbeitsstunden: (hemdenAnz * stundenProHemd) + (rockAnz * stundenProRock) + (hosenAnz * stundenProHose) <= maxMaterial;
subto material: (hemdenAnz * materialProHemd) + (rockAnz * materialProRock) + (hosenAnz * materialProHose) <= maxArbeitsstunden;
