package es.edufdezsoy.manga2kindle.service

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.random.Random

class ScanMangaTest {
    @Test
    fun testGetChapter() {
        println("\nTest: testGetChapter\n")

        val scanManga = ScanManga()

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
    }
}