# Überblick

Ich möchte eine App entwickeln, mit der ich eine Sammlung von Filamenten für 3D-Druck verwalten kann.
### Codename: spoolstack

## Funktionen

### Auflistung vorhandener Filamente
* Ich sehe eine Liste alle Filamente in meinem Bestand.
* Ich kann nach Filament-Typ, Farbe, Hersteller und weiteren Kriterien filtern.

### Neues Filament hinzufügen
* Ich kann neues Filament in einer Eingabemaske hinzufügen.
* Felder:
    * Filament-Typ
    * Hersteller
    * Farbe
    * Menge (z.B. 1kg)
    * Checkbox: mit Rolle oder ohne
    * wann gekauft?
    * wo gekauft?
    * Kaufpreis
    * Bemerkung (Freitext)
    * bis zu 5 Foto-Anhänge erlaubt

### Vorhandenes Filament editieren
* Hier können alle Felder, die unter “Neues Filament hinzufügen” vorhanden sind, editiert werden.
* Es wird technisch die gleiche UI verwendet.

### Einen Druck erfassen
* Hier kann ich einen konkreten 3D-Druck erfassen.
* Felder:
    * Datum
    * benutztes Filament (hier per Listbox aus den vorhandenen Filamenten auswählen)
    * Filament-Menge (verfügbare Menge prüfen)
    * Link zum 3D-Modell
    * 5-Sterne Bewertung für den Druck-Output
    * Bemerkung
    * bis zu 5 Foto-Anhänge erlaubt
* Wenn ein Druck erfasst wird, wird in der Datenbank die Filament-Menge entsprechend reduziert.