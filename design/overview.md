# Overview

I want to develop an app that allows me to manage a collection of filaments for 3D printing.
Codename: spoolstack

## Features

### Listing existing filaments
* I can see a list of all filaments in my inventory.
* I can filter by filament type, color, manufacturer, and other criteria.

### Adding a new filament
* I can add a new filament using an input form.
* Fields:
* Filament type
* Manufacturer
* Color
* Quantity (e.g., 1kg)
* Checkbox: with or without spool
* When purchased?
* Where purchased?
* Purchase price
* Notes (free text)
* Up to 5 photo attachments allowed

### Editing an existing filament
* All fields available under "Adding a new filament" can be edited here.
* The same UI is used technically.

### Recording a print
* Here I can record a specific 3D print.
* Fields:
* Date
* Filament used (select from existing filaments using a listbox)
* Filament quantity (check available quantity)
* Link to the 3D model
* 5-star rating for the print output
* Notes
* Up to 5 photo attachments allowed
* When a print is recorded, the filament quantity in the database is reduced accordingly.

## Tech Stack

* Platform and language: Kotlin
* UI: Jetpack Compose with Material Design 3
* Clean architecture with MVVM
* Use of ViewModels
* Repositories for data access
* Dependency injection using Hilt
* Room as the database
* Image loading using Coil
* Extensive use of JUnit tests, for both UI and backend
* Primary app language: English, secondary: German