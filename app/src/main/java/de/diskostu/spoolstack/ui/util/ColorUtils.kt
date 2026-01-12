package de.diskostu.spoolstack.ui.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import me.xdrop.fuzzywuzzy.FuzzySearch

object ColorUtils {

    private val colorMap = mapOf(
        "black" to "#000000", "schwarz" to "#000000",
        "white" to "#FFFFFF", "weiß" to "#FFFFFF", "weiss" to "#FFFFFF",
        "red" to "#FF0000", "rot" to "#FF0000",
        "green" to "#00FF00", "grün" to "#00FF00",
        "blue" to "#0000FF", "blau" to "#0000FF",
        "yellow" to "#FFFF00", "gelb" to "#FFFF00",
        "orange" to "#FFA500",
        "purple" to "#800080", "violett" to "#800080", "lila" to "#800080",
        "pink" to "#FFC0CB", "rosa" to "#FFC0CB",
        "gray" to "#808080", "grey" to "#808080", "grau" to "#808080",
        "brown" to "#A52A2A", "braun" to "#A52A2A",
        "cyan" to "#00FFFF", "türkis" to "#00FFFF",
        "magenta" to "#FF00FF",
        "silver" to "#C0C0C0", "silber" to "#C0C0C0",
        "gold" to "#FFD700",
        "beige" to "#F5F5DC",
        "navy" to "#000080", "marineblau" to "#000080",
        "teal" to "#008080", "blaugrün" to "#008080",
        "olive" to "#808000", "olivgrün" to "#808000",
        "maroon" to "#800000", "kastanienbraun" to "#800000",
        "lime" to "#00FF00", "limette" to "#00FF00",
        "indigo" to "#4B0082",
        "violet" to "#EE82EE",
        "khaki" to "#F0E68C",
        "lavender" to "#E6E6FA", "lavendel" to "#E6E6FA",
        "crimson" to "#DC143C", "karmesinrot" to "#DC143C",
        "amber" to "#FFBF00", "bernstein" to "#FFBF00",
        "coral" to "#FF7F50", "koralle" to "#FF7F50",
        "salmon" to "#FA8072", "lachs" to "#FA8072",
        "turquoise" to "#40E0D0",
        "plum" to "#DDA0DD", "pflaume" to "#DDA0DD",
        "azure" to "#F0FFFF", "azurblau" to "#F0FFFF",
        "ivory" to "#FFFFF0", "elfenbein" to "#FFFFF0",
        "peach" to "#FFDAB9", "pfirsich" to "#FFDAB9",
        "mint" to "#98FF98", "minze" to "#98FF98",
        "lavender blush" to "#FFF0F5",
        "slate gray" to "#708090", "schiefergrau" to "#708090",
        "deep sky blue" to "#00BFFF", "tiefhimmelblau" to "#00BFFF",
        "royal blue" to "#4169E1", "königsblau" to "#4169E1",
        "midnight blue" to "#191970", "mitternachtsblau" to "#191970",
        "forest green" to "#228B22", "waldgrün" to "#228B22",
        "sea green" to "#2E8B57", "meeresgrün" to "#2E8B57",
        "goldenrod" to "#DAA520", "goldrute" to "#DAA520",
        "firebrick" to "#B22222", "ziegelrot" to "#B22222",
        "dark red" to "#8B0000", "dunkelrot" to "#8B0000",
        "dark green" to "#006400", "dunkelgrün" to "#006400",
        "dark blue" to "#00008B", "dunkelblau" to "#00008B",
        "light red" to "#FFCCCB", "hellrot" to "#FFCCCB",
        "light green" to "#90EE90", "hellgrün" to "#90EE90",
        "light blue" to "#ADD8E6", "hellblau" to "#ADD8E6",
        "light gray" to "#D3D3D3", "hellgrau" to "#D3D3D3",
        "dark gray" to "#A9A9A9", "dunkelgrau" to "#A9A9A9",
        "hot pink" to "#FF69B4", "leuchtend rosa" to "#FF69B4",
        "deep pink" to "#FF1493", "tiefrosa" to "#FF1493",
        "chartreuse" to "#7FFF00",
        "aquamarine" to "#7FFFD4", "aquamarin" to "#7FFFD4",
        "fuchsia" to "#FF00FF",
        "orchid" to "#DA70D6", "orchidee" to "#DA70D6",
        "thistle" to "#D8BFD8", "distel" to "#D8BFD8",
        "wheat" to "#F5DEB3", "weizen" to "#F5DEB3",
        "chocolate" to "#D2691E", "schokolade" to "#D2691E",
        "sienna" to "#A0522D",
        "peru" to "#CD853F",
        "tan" to "#D2B48C",
        "rosy brown" to "#BC8F8F",
        "moccasin" to "#FFE4B5", "mokassin" to "#FFE4B5",
        "navajo white" to "#FFDEAD",
        "misty rose" to "#FFE4E1",
        "honeydew" to "#F0FFF0", "honigtau" to "#F0FFF0",
        "alice blue" to "#F0F8FF",
        "ghost white" to "#F8F8FF",
        "seashell" to "#FFF5EE", "muschel" to "#FFF5EE",
        "linen" to "#FAF0E6", "leinen" to "#FAF0E6",
        "old lace" to "#FDF5E6",
        "antique white" to "#FAEBD7",
        "papaya whip" to "#FFEFD5",
        "blanched almond" to "#FFEBCD",
        "bisque" to "#FFE4C4",
        "cornsilk" to "#FFF8DC",
        "lemon chiffon" to "#FFFACD",
        "light goldenrod yellow" to "#FAFAD2",
        "light yellow" to "#FFFFE0", "hellgelb" to "#FFFFE0",
        "dark orange" to "#FF8C00", "dunkelorange" to "#FF8C00",
        "tomato" to "#FF6347", "tomate" to "#FF6347",
        "orange red" to "#FF4500", "orangerot" to "#FF4500",
        "dark salmon" to "#E9967A", "dunkellachs" to "#E9967A",
        "light salmon" to "#FFA07A", "helllachs" to "#FFA07A",
        "light coral" to "#F08080", "hellkoralle" to "#F08080",
        "indian red" to "#CD5C5C", "indischrot" to "#CD5C5C",
        "medium violet red" to "#C71585",
        "pale violet red" to "#DB7093",
        "dark orchid" to "#9932CC", "dunkelorchidee" to "#9932CC",
        "dark violet" to "#9400D3", "dunkelviolett" to "#9400D3",
        "medium purple" to "#9370DB",
        "blue violet" to "#8A2BE2", "blauviolett" to "#8A2BE2",
        "dark magenta" to "#8B008B", "dunkelmagenta" to "#8B008B",
        "dark slate blue" to "#483D8B",
        "slate blue" to "#6A5ACD", "schieferblau" to "#6A5ACD",
        "medium slate blue" to "#7B68EE",
        "medium blue" to "#0000CD",
        "cornflower blue" to "#6495ED", "kornblumenblau" to "#6495ED",
        "steel blue" to "#4682B4", "stahlblau" to "#4682B4",
        "dodger blue" to "#1E90FF",
        "sky blue" to "#87CEEB", "himmelblau" to "#87CEEB",
        "light sky blue" to "#87CEFA", "hellhimmelblau" to "#87CEFA",
        "powder blue" to "#B0E0E6", "puderblau" to "#B0E0E6",
        "light steel blue" to "#B0C4DE", "hellstahlblau" to "#B0C4DE",
        "cadet blue" to "#5F9EA0", "kadettenblau" to "#5F9EA0",
        "dark cyan" to "#008B8B", "dunkelcyan" to "#008B8B",
        "dark turquoise" to "#00CED1", "dunkeltürkis" to "#00CED1",
        "medium turquoise" to "#48D1CC",
        "pale turquoise" to "#AFEEEE",
        "light sea green" to "#20B2AA",
        "medium sea green" to "#3CB371",
        "spring green" to "#00FF7F", "frühlingsgrün" to "#00FF7F",
        "medium spring green" to "#00FA9A",
        "dark sea green" to "#8FBC8F",
        "medium aquamarine" to "#66CDAA",
        "yellow green" to "#9ACD32", "gelbgrün" to "#9ACD32",
        "lime green" to "#32CD32", "limettengrün" to "#32CD32",
        "pale green" to "#98FB98",
        "dark olive green" to "#556B2F", "dunkelolivgrün" to "#556B2F",
        "olive drab" to "#6B8E23",
        "lawngreen" to "#7CFC00", "rasengrün" to "#7CFC00"
    )

    fun inferColorFromText(text: String): Color? {
        val normalizedText = text.trim().lowercase()

        // Check for direct hex match
        if (normalizedText.startsWith("#") && (normalizedText.length == 7 || normalizedText.length == 9)) {
            return try {
                Color(android.graphics.Color.parseColor(normalizedText))
            } catch (e: Exception) {
                null
            }
        }

        if (normalizedText.isEmpty()) return null

        // First, check for exact matches
        if (colorMap.containsKey(normalizedText)) {
            return hexToColor(colorMap[normalizedText])
        }

        // Sort keys by length descending to match longer phrases first (e.g. "light blue" before "blue")
        val sortedKeys = colorMap.keys.sortedByDescending { it.length }

        // check for exact substring matches with word boundaries to prioritize "natural" language
        for (name in sortedKeys) {
            val hex = colorMap[name]
            if (normalizedText.contains(" $name") || normalizedText.contains("$name ")) {
                return hexToColor(hex)
            }
        }

        val colorNames = colorMap.keys.toList()

        // Find all matches with score >= 70
        val matches = FuzzySearch.extractAll(normalizedText, colorNames)
            .filter { it.score >= 70 }

        // Only return a result if there are matches and at most 3 potential candidates
        // EXCEPT if there is a 100% score match, we return it regardless of other partial matches
        if (matches.isNotEmpty()) {
            val bestMatch = matches.maxByOrNull { it.score }!!

            if (bestMatch.score == 100) {
                return hexToColor(colorMap[bestMatch.string])
            }

            if (matches.size <= 3) {
                return hexToColor(colorMap[bestMatch.string])
            }
        }

        return null
    }

    fun colorToHex(color: Color): String {
        return String.format("#%06X", 0xFFFFFF and color.toArgb())
    }

    fun hexToColor(hex: String?): Color? {
        if (hex.isNullOrBlank()) return null
        return try {
            Color(android.graphics.Color.parseColor(hex))
        } catch (e: Exception) {
            null
        }
    }

    fun isColorLight(color: Color): Boolean {
        val luminance = ColorUtils.calculateLuminance(color.toArgb())
        return luminance > 0.5
    }
}
