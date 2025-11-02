#!/bin/bash
set -euo pipefail

echo "=== 0) Berechtigungen für gradlew setzen ==="
chmod +x ./gradlew || { echo "WARNUNG: chmod fehlgeschlagen"; }

echo "=== 1) Prüfe/Setze Android SDK Umgebung ==="
: "${ANDROID_SDK_ROOT:=$HOME/Android/Sdk}"
: "${ANDROID_HOME:=$ANDROID_SDK_ROOT}"
echo "Verwendeter SDK‑Pfad: $ANDROID_SDK_ROOT (ANDROID_HOME: $ANDROID_HOME)"

# Mögliche Pfade zum sdkmanager
POSSIBLE_SDKMANAGER_PATHS=(
  "$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager"
  "$ANDROID_SDK_ROOT/cmdline-tools/tools/bin/sdkmanager"
  "$ANDROID_SDK_ROOT/tools/bin/sdkmanager"
  "$ANDROID_SDK_ROOT/cmdline-tools/bin/sdkmanager"
)

SDKMANAGER_BIN=""
for p in "${POSSIBLE_SDKMANAGER_PATHS[@]}"; do
  if [ -x "$p" ]; then
    SDKMANAGER_BIN="$p"
    break
  fi
done

# Falls nicht gefunden: versuche Installation
if [ -z "$SDKMANAGER_BIN" ]; then
  echo "sdkmanager nicht gefunden in bekannten Pfaden – versuche Installation der Command‐Line‑Tools..."
  mkdir -p "$ANDROID_SDK_ROOT/cmdline-tools/latest"
  cd "$ANDROID_SDK_ROOT"
  wget https://dl.google.com/android/repository/commandlinetools-linux-13114758_latest.zip -O cmdline-tools.zip
  unzip -q cmdline-tools.zip -d cmdline-tools/latest
  rm cmdline-tools.zip
  cd - >/dev/null
  chmod +x "$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" || true
  if [ -x "$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" ]; then
    SDKMANAGER_BIN="$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager"
    echo "sdkmanager gefunden: $SDKMANAGER_BIN"
  fi
fi

if [ -z "$SDKMANAGER_BIN" ]; then
  echo "FEHLER: sdkmanager konnte nicht gefunden werden."
  echo "Gängige Pfade sind z. B.:"
  for p in "${POSSIBLE_SDKMANAGER_PATHS[@]}"; do
    echo "  • $p"
  done
  echo "Bitte prüfen: wurde das Paket „Android SDK Command‑Line Tools“ installiert? (siehe https://developer.android.com/tools/sdkmanager) :contentReference[oaicite:2]{index=2}"
  exit 1
fi

echo "gefundenes sdkmanager: $SDKMANAGER_BIN"

export ANDROID_SDK_ROOT ANDROID_HOME
export PATH="$(dirname "$SDKMANAGER_BIN"):$ANDROID_SDK_ROOT/platform-tools:$PATH"
echo "Aktueller PATH erweitert mit: $(dirname "$SDKMANAGER_BIN") und $ANDROID_SDK_ROOT/platform-tools"

echo "=== 2) Automatisiert Lizenz‑Bedingungen akzeptieren ==="
# Hier rufen wir sdkmanager mit vollständigem Pfad auf
"$SDKMANAGER_BIN" --sdk_root="$ANDROID_SDK_ROOT" --licenses < <(yes) >/dev/null || { echo "WARNUNG: Lizenzannahme fehlgeschlagen"; }

echo "=== 3) Installiere erforderliche SDK‑Pakete (falls nicht vorhanden) ==="
"$SDKMANAGER_BIN" --sdk_root="$ANDROID_SDK_ROOT" "platform-tools" "platforms;android-33" "build-tools;33.0.2" >/dev/null

echo "=== 4) Prüfe Kotlin, Gradle & Build Umgebung ==="
./gradlew --version
echo "Gradle wrapper ausgeführt."

if [ -z "${ANDROID_SDK_ROOT:-}" ]; then
  echo "WARNUNG: ANDROID_SDK_ROOT ist **nicht** gesetzt."
else
  echo "ANDROID_SDK_ROOT = $ANDROID_SDK_ROOT"
fi

echo "=== 5) Optional: Abhängigkeiten prüfen (wenn „implementation“-Konfiguration vorhanden) ==="
if ./gradlew properties | grep -q "configuration 'implementation'"; then
  ./gradlew dependencies --configuration implementation | grep -E "androidx.compose|hilt|room" || echo "WARNUNG: Filter‑Ergebnis leer oder grep fehlgeschlagen."
else
  echo "Hinweis: Konfiguration 'implementation' im Root‑Projekt nicht gefunden – Schritt übersprungen."
fi

echo "=== 6) Clean & Build ==="
./gradlew clean assembleDebug
echo "Build erfolgreich."

echo "=== 7) Unit‑Tests ausführen ==="
./gradlew testDebugUnitTest
echo "Unit‑Tests erfolgreich abgeschlossen."

echo "=== 8) UI‑Tests ausführen (falls vorhanden) ==="
if ! ./gradlew connectedDebugAndroidTest; then
  echo "Hinweis: Keine UI‑Tests oder Fehler beim Ausführen."
fi

echo "=== 9) Snapshot für Jules erstellen ==="
git rev‑parse HEAD > jules_snapshot_commit.txt
echo "Snapshot erstellt: $(cat jules_snapshot_commit.txt)"

echo "=== Setup abgeschlossen ==="
