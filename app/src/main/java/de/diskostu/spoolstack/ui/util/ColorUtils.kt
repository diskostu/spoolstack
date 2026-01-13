package de.diskostu.spoolstack.ui.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import me.xdrop.fuzzywuzzy.FuzzySearch
import java.util.Locale

object ColorUtils {

    private val enColorMap = mapOf(
        "black" to "#000000",
        "white" to "#FFFFFF",
        "red" to "#FF0000",
        "green" to "#00FF00",
        "blue" to "#0000FF",
        "yellow" to "#FFFF00",
        "orange" to "#FFA500",
        "purple" to "#800080",
        "pink" to "#FFC0CB",
        "gray" to "#808080",
        "grey" to "#808080",
        "brown" to "#A52A2A",
        "cyan" to "#00FFFF",
        "magenta" to "#FF00FF",
        "silver" to "#C0C0C0",
        "gold" to "#FFD700",
        "beige" to "#F5F5DC",
        "navy" to "#000080",
        "teal" to "#008080",
        "olive" to "#808000",
        "maroon" to "#800000",
        "lime" to "#00FF00",
        "indigo" to "#4B0082",
        "violet" to "#EE82EE",
        "khaki" to "#F0E68C",
        "lavender" to "#E6E6FA",
        "crimson" to "#DC143C",
        "amber" to "#FFBF00",
        "coral" to "#FF7F50",
        "salmon" to "#FA8072",
        "turquoise" to "#40E0D0",
        "plum" to "#DDA0DD",
        "azure" to "#F0FFFF",
        "ivory" to "#FFFFF0",
        "peach" to "#FFDAB9",
        "mint" to "#98FF98",
        "lavender blush" to "#FFF0F5",
        "slate gray" to "#708090",
        "deep sky blue" to "#00BFFF",
        "royal blue" to "#4169E1",
        "midnight blue" to "#191970",
        "forest green" to "#228B22",
        "sea green" to "#2E8B57",
        "goldenrod" to "#DAA520",
        "firebrick" to "#B22222",
        "dark red" to "#8B0000",
        "dark green" to "#006400",
        "dark blue" to "#00008B",
        "light red" to "#FFCCCB",
        "light green" to "#90EE90",
        "light blue" to "#ADD8E6",
        "light gray" to "#D3D3D3",
        "dark gray" to "#A9A9A9",
        "hot pink" to "#FF69B4",
        "deep pink" to "#FF1493",
        "chartreuse" to "#7FFF00",
        "aquamarine" to "#7FFFD4",
        "fuchsia" to "#FF00FF",
        "orchid" to "#DA70D6",
        "thistle" to "#D8BFD8",
        "wheat" to "#F5DEB3",
        "chocolate" to "#D2691E",
        "sienna" to "#A0522D",
        "peru" to "#CD853F",
        "tan" to "#D2B48C",
        "rosy brown" to "#BC8F8F",
        "moccasin" to "#FFE4B5",
        "navajo white" to "#FFDEAD",
        "misty rose" to "#FFE4E1",
        "honeydew" to "#F0FFF0",
        "alice blue" to "#F0F8FF",
        "ghost white" to "#F8F8FF",
        "seashell" to "#FFF5EE",
        "linen" to "#FAF0E6",
        "old lace" to "#FDF5E6",
        "antique white" to "#FAEBD7",
        "papaya whip" to "#FFEFD5",
        "blanched almond" to "#FFEBCD",
        "bisque" to "#FFE4C4",
        "cornsilk" to "#FFF8DC",
        "lemon chiffon" to "#FFFACD",
        "light goldenrod yellow" to "#FAFAD2",
        "light yellow" to "#FFFFE0",
        "dark orange" to "#FF8C00",
        "tomato" to "#FF6347",
        "orange red" to "#FF4500",
        "dark salmon" to "#E9967A",
        "light salmon" to "#FFA07A",
        "light coral" to "#F08080",
        "indian red" to "#CD5C5C",
        "medium violet red" to "#C71585",
        "pale violet red" to "#DB7093",
        "dark orchid" to "#9932CC",
        "dark violet" to "#9400D3",
        "medium purple" to "#9370DB",
        "blue violet" to "#8A2BE2",
        "dark magenta" to "#8B008B",
        "dark slate blue" to "#483D8B",
        "slate blue" to "#6A5ACD",
        "medium slate blue" to "#7B68EE",
        "medium blue" to "#0000CD",
        "cornflower blue" to "#6495ED",
        "steel blue" to "#4682B4",
        "dodger blue" to "#1E90FF",
        "sky blue" to "#87CEEB",
        "light sky blue" to "#87CEFA",
        "powder blue" to "#B0E0E6",
        "light steel blue" to "#B0C4DE",
        "cadet blue" to "#5F9EA0",
        "dark cyan" to "#008B8B",
        "dark turquoise" to "#00CED1",
        "medium turquoise" to "#48D1CC",
        "pale turquoise" to "#AFEEEE",
        "light sea green" to "#20B2AA",
        "medium sea green" to "#3CB371",
        "spring green" to "#00FF7F",
        "medium spring green" to "#00FA9A",
        "dark sea green" to "#8FBC8F",
        "medium aquamarine" to "#66CDAA",
        "yellow green" to "#9ACD32",
        "lime green" to "#32CD32",
        "pale green" to "#98FB98",
        "dark olive green" to "#556B2F",
        "olive drab" to "#6B8E23",
        "lawngreen" to "#7CFC00"
    )

    private val deColorMap = mapOf(
        "schwarz" to "#000000",
        "weiß" to "#FFFFFF",
        "weiss" to "#FFFFFF",
        "rot" to "#FF0000",
        "grün" to "#00FF00",
        "blau" to "#0000FF",
        "gelb" to "#FFFF00",
        "orange" to "#FFA500",
        "violett" to "#800080",
        "lila" to "#800080",
        "pink" to "#FFC0CB",
        "rosa" to "#FFC0CB",
        "grau" to "#808080",
        "braun" to "#A52A2A",
        "cyan" to "#00FFFF",
        "türkis" to "#00FFFF",
        "magenta" to "#FF00FF",
        "silber" to "#C0C0C0",
        "gold" to "#FFD700",
        "beige" to "#F5F5DC",
        "marineblau" to "#000080",
        "blaugrün" to "#008080",
        "olivgrün" to "#808000",
        "kastanienbraun" to "#800000",
        "limette" to "#00FF00",
        "indigo" to "#4B0082",
        "khaki" to "#F0E68C",
        "lavendel" to "#E6E6FA",
        "karmesinrot" to "#DC143C",
        "bernstein" to "#FFBF00",
        "koralle" to "#FF7F50",
        "lachs" to "#FA8072",
        "pflaume" to "#DDA0DD",
        "azurblau" to "#F0FFFF",
        "elfenbein" to "#FFFFF0",
        "pfirsich" to "#FFDAB9",
        "minze" to "#98FF98",
        "schiefergrau" to "#708090",
        "tiefhimmelblau" to "#00BFFF",
        "königsblau" to "#4169E1",
        "mitternachtsblau" to "#191970",
        "waldgrün" to "#228B22",
        "meeresgrün" to "#2E8B57",
        "goldrute" to "#DAA520",
        "ziegelrot" to "#B22222",
        "dunkelrot" to "#8B0000",
        "dunkelgrün" to "#006400",
        "dunkelblau" to "#00008B",
        "hellrot" to "#FFCCCB",
        "hellgrün" to "#90EE90",
        "hellblau" to "#ADD8E6",
        "hellgrau" to "#D3D3D3",
        "dunkelgrau" to "#A9A9A9",
        "leuchtend rosa" to "#FF69B4",
        "tiefrosa" to "#FF1493",
        "aquamarin" to "#7FFFD4",
        "orchidee" to "#DA70D6",
        "distel" to "#D8BFD8",
        "weizen" to "#F5DEB3",
        "schokolade" to "#D2691E",
        "mokassin" to "#FFE4B5",
        "honigtau" to "#F0FFF0",
        "muschel" to "#FFF5EE",
        "leinen" to "#FAF0E6",
        "hellgelb" to "#FFFFE0",
        "dunkelorange" to "#FF8C00",
        "tomate" to "#FF6347",
        "orangerot" to "#FF4500",
        "dunkellachs" to "#E9967A",
        "helllachs" to "#FFA07A",
        "hellkoralle" to "#F08080",
        "indischrot" to "#CD5C5C",
        "dunkelorchidee" to "#9932CC",
        "dunkelviolett" to "#9400D3",
        "blauviolett" to "#8A2BE2",
        "dunkelmagenta" to "#8B008B",
        "schieferblau" to "#6A5ACD",
        "kornblumenblau" to "#6495ED",
        "stahlblau" to "#4682B4",
        "himmelblau" to "#87CEEB",
        "hellhimmelblau" to "#87CEFA",
        "puderblau" to "#B0E0E6",
        "hellstahlblau" to "#B0C4DE",
        "kadettenblau" to "#5F9EA0",
        "dunkelcyan" to "#008B8B",
        "dunkeltürkis" to "#00CED1",
        "frühlingsgrün" to "#00FF7F",
        "gelbgrün" to "#9ACD32",
        "limettengrün" to "#32CD32",
        "dunkelolivgrün" to "#556B2F",
        "rasengrün" to "#7CFC00"
    )

    private fun getColorMap(language: String): Map<String, String> {
        return when (language.lowercase()) {
            "de" -> deColorMap
            else -> enColorMap
        }
    }

    fun getColorNameForHex(hex: String?, language: String = Locale.getDefault().language): String? {
        if (hex == null) return null
        val normalizedHex = hex.uppercase()
        val currentMap = getColorMap(language)
        return currentMap.entries.firstOrNull { it.value.uppercase() == normalizedHex }?.key
    }

    /**
     * Finds up to 5 closest color names and their hex codes for a given hex color.
     * Prioritizes shorter ("simpler") names if distances are similar.
     */
    fun getClosestColors(
        hex: String,
        language: String = Locale.getDefault().language
    ): List<Pair<String, String>> {
        val targetColor = try {
            parseColor(hex)
        } catch (e: Exception) {
            return emptyList()
        }

        val currentMap = getColorMap(language)

        return currentMap.entries
            .asSequence()
            .map { entry ->
                val entryColor = parseColor(entry.value)
                val distance = calculateColorDistance(targetColor, entryColor)
                // Score combines distance and name simplicity (length)
                // A lower score is better. penalty factor for length helps prefer simple names.
                val score = distance + (entry.key.length * 2.0)
                Triple(entry.key, entry.value, score)
            }
            .sortedBy { it.third }
            .take(5)
            .map { it.first to it.second }
            .toList()
    }

    /**
     * Calculates the Euclidean distance between two colors in RGB space.
     */
    private fun calculateColorDistance(c1: Int, c2: Int): Double {
        val r1 = (c1 shr 16) and 0xFF
        val g1 = (c1 shr 8) and 0xFF
        val b1 = c1 and 0xFF

        val r2 = (c2 shr 16) and 0xFF
        val g2 = (c2 shr 8) and 0xFF
        val b2 = c2 and 0xFF

        val dr = (r1 - r2).toDouble()
        val dg = (g1 - g2).toDouble()
        val db = (b1 - b2).toDouble()

        return Math.sqrt(dr * dr + dg * dg + db * db)
    }

    fun inferColorFromText(text: String, language: String = Locale.getDefault().language): Color? {
        val normalizedText = text.trim().lowercase()

        // Check for direct hex match
        if (normalizedText.startsWith("#") && (normalizedText.length == 7 || normalizedText.length == 9)) {
            return try {
                Color(parseColor(normalizedText))
            } catch (e: Exception) {
                null
            }
        }

        if (normalizedText.isEmpty()) return null

        val currentMap = getColorMap(language)

        // First, check for exact matches
        if (currentMap.containsKey(normalizedText)) {
            return hexToColor(currentMap[normalizedText])
        }

        // Sort keys by length descending to match longer phrases first (e.g. "light blue" before "blue")
        val sortedKeys = currentMap.keys.sortedByDescending { it.length }

        // check for exact substring matches with word boundaries to prioritize "natural" language
        for (name in sortedKeys) {
            val hex = currentMap[name]
            if (normalizedText.contains(" $name") || normalizedText.contains("$name ")) {
                return hexToColor(hex)
            }
        }

        val colorNames = currentMap.keys.toList()

        // Find all matches with score >= 70
        val matches = FuzzySearch.extractAll(normalizedText, colorNames)
            .filter { it.score >= 70 }

        // Only return a result if there are matches and at most 3 potential candidates
        // EXCEPT if there is a 100% score match, we return it regardless of other partial matches
        if (matches.isNotEmpty()) {
            val bestMatch = matches.maxByOrNull { it.score }!!

            if (bestMatch.score == 100) {
                return hexToColor(currentMap[bestMatch.string])
            }

            if (matches.size <= 3) {
                return hexToColor(currentMap[bestMatch.string])
            }
        }

        return null
    }

    fun colorToHex(color: Color): String {
        return String.format("#%06X", 0xFFFFFF and (color.toArgb()))
    }

    fun hexToColor(hex: String?): Color? {
        if (hex.isNullOrBlank()) return null
        return try {
            Color(parseColor(hex))
        } catch (e: Exception) {
            null
        }
    }

    fun isColorLight(color: Color): Boolean {
        val argb = color.toArgb()
        val r = (argb shr 16) and 0xFF
        val g = (argb shr 8) and 0xFF
        val b = argb and 0xFF

        // Use standard perceived luminance formula
        val luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255.0
        return luminance > 0.5
    }

    private fun parseColor(colorString: String): Int {
        if (colorString[0] == '#') {
            // Use long to avoid sign issue
            var color = colorString.substring(1).toLong(16)
            if (colorString.length == 7) {
                // Set alpha to opaque
                color = color or 0x00000000FF000000L
            } else if (colorString.length != 9) {
                throw IllegalArgumentException("Unknown color")
            }
            return color.toInt()
        }
        throw IllegalArgumentException("Unknown color")
    }
}
