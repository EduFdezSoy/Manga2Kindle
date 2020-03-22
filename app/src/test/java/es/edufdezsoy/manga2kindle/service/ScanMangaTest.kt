package es.edufdezsoy.manga2kindle.service

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.random.Random

class ScanMangaTest {

    val scanManga = ScanManga()

    @Test
    fun testGetChapter() {
        println("Test: testGetChapter\n")

        // Test input data
        val chapterNames = arrayOf(
            // General, MangaDex
            "Vol.13 Ch.113.5 - Vol 13 Extras",
            "Vol.2 Ch.10",
            "Vol.7 Ch.51 - Give Me a Hand And Teach Me Everything, Senpai",
            "Vol.18 Ch.178 - Senpai-kun and Kouhai-chan, Part 2",
            // Oneshot
            "Oneshot - My awesome manga oneshot",
            // Manga Plus
            "#143 - Z=143_ Ryusui vs. Senku",
            "#15 - Just a test",
            // Manga Rock
            "Chapter 20 _ I think Manga Rock is dead now but idk",
            // LectorManga, TuMangaOnline
            "Vol.1 Capítulo 7 - Nuevo manga",
            // NHentai
            "Chapter",
            // HeavenManga
            "Yancha Gal no Anjou-san Chap 30",
            // Guya
            "177 - Senpai-kun and Kouhai-chan",
            "26.2 - Senpai-kun",
            // Void, Null, just testing, may not happen
            "",
            // MangaLife
            "Yancha 0034"
            )

        // Test result
        val results = arrayOf(
            // General, MangaDex
            113.5F,
            10F,
            51F,
            178F,
            // Oneshot
            0F,
            // Manga Plus
            143F,
            15F,
            // Manga Rock
            20F,
            // LectorManga, TuMangaOnline
            7F,
            // NHentai
            0F,
            // HeavenManga
            30F,
            // Guya
            177F,
            26.2F,
            // Void, Null, just testing, may not happen
            0F,
            // MangaLife
            34F
        )

        for (i in chapterNames.indices) {
            val res = scanManga.pickChapter(chapterNames[i])

            println("Asserting: " + results[i] + " (Input: " + chapterNames[i] + ")")
            println("with:      " + res)

            assertEquals(results[i], res)
        }

        // Lets randomize that and try

        var counter = 5000
        val random = Random

        println("\nRandom run: $counter tries")

        while (counter-- > 0) {
            val rnd = random.nextInt(chapterNames.size)
            val res = scanManga.pickChapter(chapterNames[rnd])

            assertEquals(results[rnd], res)
        }

        println("Done.")
    }

    @Test
    fun testPickVolume() {
        println("Test: testPickVolume\n")

        // Test input data
        val chapterNames = arrayOf(
            // General, MangaDex
            "Vol.13 Ch.113.5 - Vol 13 Extras",
            "Vol.2 Ch.10",
            "Vol.7 Ch.51 - Give Me a Hand And Teach Me Everything, Senpai",
            "Vol.18 Ch.178 - Senpai-kun and Kouhai-chan, Part 2",
            // Oneshot
            "Oneshot - My awesome manga oneshot",
            // Manga Plus
            "#143 - Z=143_ Ryusui vs. Senku",
            "#15 - Just a test",
            // Manga Rock
            "Chapter 20 _ I think Manga Rock is dead now but idk",
            // LectorManga, TuMangaOnline
            "Vol.1 Capítulo 7 - Nuevo manga",
            // NHentai
            "Chapter",
            // HeavenManga
            "Yancha Gal no Anjou-san Chap 30",
            // Guya
            "177 - Senpai-kun and Kouhai-chan",
            "26.2 - Senpai-kun",
            // Void, Null, just testing, may not happen
            "",
            // MangaLife
            "Yancha 0034",
            // Other tests that may not exist
            "Yancha vol.3 0034",
            "Vol.15 Chapter",
            "Vol.16 - Chapter",
            "Vol.17 _ Chapter"
        )

        // Test result
        val results = arrayOf(
            // General, MangaDex
            13,
            2,
            7,
            18,
            // Oneshot
            null,
            // Manga Plus
            null,
            null,
            // Manga Rock
            null,
            // LectorManga, TuMangaOnline
            1,
            // NHentai
            null,
            // HeavenManga
            null,
            // Guya
            null,
            null,
            // Void, Null, just testing, may not happen
            null,
            // MangaLife
            null,
            // Other tests that may not exist
            3,
            15,
            16,
            17
        )

        for (i in chapterNames.indices) {
            val res = scanManga.pickVolume(chapterNames[i])

            println("Asserting: " + results[i] + " (Input: " + chapterNames[i] + ")")
            println("with:      " + res)

            assertEquals(results[i], res)
        }
    }

    @Test
    fun formatNameTest() {
        println("Test: formatNameTest\n")

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
            "Capítulo 1.00 _ Una invocación real",
            "Ch.56.0 _ Capítulo  56.00_  Sentimientos",
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
            "Capítulo 1.00 - Una invocación real",
            "Ch.56.0 - Capítulo 56.00: Sentimientos",
            ""
        )

        for (i in names.indices) {
            val res = scanManga.formatName(names[i])

            println("Asserting: '" + results[i] + "' (Input: '" + names[i] + "')")
            println("with:      '" + res + "'")

            assertEquals(results[i], res)
        }
    }
}