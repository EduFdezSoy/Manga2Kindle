package es.edufdezsoy.manga2kindle.service

import org.junit.Assert.assertEquals
import org.junit.Test

class ScanMangaTest {
    @Test
    fun formatNameTest() {
        val scanManga = ScanManga()

        // Mock data
        val names = arrayOf(
            "Vol.TBD Chapter 104_ CSI Men",
            "Chapter 105_ The Most Beautiful Girl on the Island",
            "Service created_",
            "Kyou no Yuiko-san",
            "Karakai Jouzu no Takagi-san",
            "Ookami_Shounen wa Kyou mo Uso o Kasaneru",
            "Gokushufudou_ The Way of the House Husband",
            "5Toubun no Hanayome",
            "Kyou_no Yuiko-san_ testo",
            null
        )

        // Mock result
        val results = arrayOf(
            "Vol.TBD Chapter 104: CSI Men",
            "Chapter 105: The Most Beautiful Girl on the Island",
            "Service created_",
            "Kyou no Yuiko-san",
            "Karakai Jouzu no Takagi-san",
            "Ookami_Shounen wa Kyou mo Uso o Kasaneru",
            "Gokushufudou: The Way of the House Husband",
            "5Toubun no Hanayome",
            "Kyou_no Yuiko-san: testo",
            ""
        )

        for (i in names.indices) {
            val res = scanManga.formatName(names[i])

            assertEquals(results[i], res)
        }
    }
}