package me.cristiangomez.radioappconcept.util

class ColorUtil {
    companion object {
        fun hashToColor(hashCode: String): Int {
            var hash = 0
            for (x in 1 until hashCode.count()) {
                hash = hashCode.elementAt(x).toInt() + ((hash.shl(5)) - hash)
            }
            return hash
        }
    }
}