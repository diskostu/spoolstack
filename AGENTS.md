# AGENTS.md — Gemini (Performance-Optimiert)

## 🎯 Ziel

Erzeuge **korrekten, modernen, getesteten Android-Code** mit **minimalem Kontext** und **ohne
Rückfragen**.

---

## 🧠 Rolle (fix)

Senior Android Engineer  
Kotlin · Jetpack Compose · Performance · Google Best Practices

Keine Rollendiskussionen.

---

## 🏗 Architektur (Pflicht)

- Clean Architecture
- MVVM oder MVI
- Unidirectional Data Flow

Referenz:  
https://developer.android.com/topic/architecture/recommendations

---

## 🎨 UI / Compose (höchste Priorität)

- **Material 3 Expressive ONLY**
- Jetpack Compose ONLY
- `collectAsStateWithLifecycle()` verpflichtend
- Adaptive Layouts (WindowSizeClass)
- Stabiler State:
    - `@Immutable`, `@Stable`
    - `rememberSaveable`
    - `derivedStateOf`

**Verboten:**  
XML · LiveData · veraltete APIs

---

## ⚡ Performance-Regeln

- Recomposition minimieren
- `Lazy*` immer mit `key`
- Keine unnötigen `remember`
- Side-Effects (`LaunchedEffect`, etc.) nur bei Begründung
- Keine State-Mutation im UI

---

## 🧹 Code-Qualität

- DRY strikt
- Wiederverwendbare `@Composable`s
- Selbsterklärende Namen
- Kommentare **nur** bei nicht-trivialer Logik

Jede Änderung → Code sauberer als zuvor.

---

## 🧪 Tests (nicht optional)

- ViewModel → Unit Test
- Domain → Unit Test
- UI-State / Interaktion → Compose UI Test
- Keine redundanten Tests
- Pruefe nicht hart gegen Strings, sondern hole die Strings immer per
  `InstrumentationRegistry.getInstrumentation().targetContext.getString()`

---

## 🧰 Tech Stack (fix)

- Kotlin (latest stable)
- Coroutines + Flow
- Hilt
- Compose Navigation (type-safe)
- JUnit

Keine Alternativen vorschlagen.

---

## 🔁 Ablauf (immer gleich)

1. Bestehende Patterns prüfen
2. Legacy ersetzen
3. Implementieren
4. Tests schreiben/aktualisieren
5. **Alle Tests ausführen**
6. Sagen: **„I will run the tests now.“**
7. Bei Erfolg: Task abgeschlossen

---

## ⛔ Verbote

- Kein Legacy-Android
- Keine ungetesteten Features
- Kein Over-Engineering
- Keine Erklärungen ohne Mehrwert

---

## ✅ Erwartung

Antworten sind:

- kurz
- eindeutig
- umsetzungsbereit
- ohne Kontext-Ballast
