package es.edufdezsoy.manga2kindle.service

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.random.Random


class ScanFoldersForMangaJobServiceTest {
    private val scanManga = ScanFoldersForMangaJobService()

    //#region test data and results

    // formatName test input data
    private val chapterNames = arrayOf(
        // MangaDex
        "vgperson_Vol.2 Ch.8.5 - The Unspoken Lightness of Being",
        "vgperson_Vol.2 Ch.9",
        "Kirei Cake_Vol.3 Ch.20 - Master's Technique",
        // Guya
        "Psylocke Scans_3 - Kaguya Doesn't Know Much",
        "Fans Scans _ Jaimini's~Box~_189 - Kaguya Shinomiya's Impossible Challenge_ _The Buddha's Stone Begging Bowl_",
        // Manga Plus
        "Shueisha_#174 - Z=174_ The Specter of the Panama Canal",
        "Shueisha_#001 - Chapter 1_ Romance Dawn",
        "Shueisha_#996 - Chapter 996_ Island of the Strongest",
        // Webtoons (dot) com
        "Ep. 2 - The Two Ladies of Rumor (2) Ch. 2 ♫",
        // NHentai
        "_Chapter",
        // Oneshot
        "Oneshot - My awesome manga oneshot",
        // Blank (technically impossible)
        "",
        // Other tests
        "Vol.2 Ch.10 - An awesome chapter - part 3",
        // MangaLife
        "_Punch 34",

//        // Manga Rock
//        // LectorManga, TuMangaOnline
//        // HeavenManga
    )

    // formatName test result and following ones input data
    private val formattedChapterNamesResults = arrayOf(
        // MangaDex
        "vgperson_Vol.2 Ch.8.5 - The Unspoken Lightness of Being",
        "vgperson_Vol.2 Ch.9",
        "Kirei Cake_Vol.3 Ch.20 - Master's Technique",
        // Guya
        "Psylocke Scans_3 - Kaguya Doesn't Know Much",
        "Fans Scans - Jaimini's~Box~_189 - Kaguya Shinomiya's Impossible Challenge: _The Buddha's Stone Begging Bowl_",
        // Manga Plus
        "Shueisha_#174 - Z=174: The Specter of the Panama Canal",
        "Shueisha_#001 - Chapter 1: Romance Dawn",
        "Shueisha_#996 - Chapter 996: Island of the Strongest",
        // Webtoons (dot) com
        "Ep. 2 - The Two Ladies of Rumor (2) Ch. 2 ♫",
        // NHentai
        "Chapter",
        // Oneshot
        "Oneshot - My awesome manga oneshot",
        // Blank (technically impossible)
        "",
        // Other tests
        "Vol.2 Ch.10 - An awesome chapter - part 3",
        // MangaLife
        "Punch 34",
    )

    // pickChapter test result
    private val chaptersResults = arrayOf(
        // MangaDex
        8.5F,
        9F,
        20F,
        // Guya
        3F,
        189F,
        // Manga Plus
        174F,
        1F,
        996F,
        // Webtoons (dot) com
        2F,
        // NHentai
        0F,
        // Oneshot
        0F,
        // Blank (technically impossible)
        0F,
        // Other tests
        10F,
        // MangaLife
        34F,
    )

    // pickVolume test result
    private val volumeResults = arrayOf(
        // MangaDex
        2,
        2,
        3,
        // Guya
        null,
        null,
        // Manga Plus
        null,
        null,
        null,
        // Webtoons (dot) com
        null,
        // NHentai
        null,
        // Oneshot
        null,
        // Blank (technically impossible)
        null,
        // Other tests
        2,
        // MangaLife
        null,
    )

    // getChapterTitle test result
    private val chapterTitlesResults = arrayOf(
        // MangaDex
        "The Unspoken Lightness of Being",
        "",
        "Master's Technique",
        // Guya
        "Kaguya Doesn't Know Much",
        "Jaimini's~Box~_189 - Kaguya Shinomiya's Impossible Challenge: _The Buddha's Stone Begging Bowl_", // TODO: tis may not be like this
        // Manga Plus
        "Z=174: The Specter of the Panama Canal",
        "Chapter 1: Romance Dawn",
        "Chapter 996: Island of the Strongest",
        // Webtoons (dot) com
        "The Two Ladies of Rumor (2) Ch. 2 ♫",
        // NHentai
        "",
        // Oneshot
        "My awesome manga oneshot",
        // Blank (technically impossible)
        "",
        // Other tests
        "An awesome chapter - part 3",
        // MangaLife
        "",
    )

    //#endregion

    @Test
    fun formatName() {
        println("Test: formatNameTest\n")

        for (i in chapterNames.indices) {
            val res = scanManga.formatName(chapterNames[i])

            println("Asserting: '" + formattedChapterNamesResults[i] + "' (Input: '" + chapterNames[i] + "')")
            println("with:      '$res'")
            assertEquals(formattedChapterNamesResults[i], res)
        }

        println("\nDone.\n")
    }

    @Test
    fun pickVolume() {
        println("Test: testPickVolume\n")

        for (i in formattedChapterNamesResults.indices) {
            val res = scanManga.pickVolume(formattedChapterNamesResults[i], chaptersResults[i])

            println("Asserting: " + volumeResults[i] + " (Input: " + formattedChapterNamesResults[i] + ")")
            println("with:      $res")

            assertEquals(volumeResults[i], res)
        }

        println("\nDone.\n")
    }

    @Test
    fun pickChapter() {
        println("Test: testPickChapter\n")

        for (i in formattedChapterNamesResults.indices) {
            val res = scanManga.pickChapter(formattedChapterNamesResults[i])

            println("Asserting: " + chaptersResults[i] + " (Input: " + formattedChapterNamesResults[i] + ")")
            println("with:      $res")

            assertEquals(chaptersResults[i], res)
        }

        println("\nDone.\n")
    }

    @Test
    fun getChapterTitle() {
        println("Test: getChapterTitle\n")

        for (i in formattedChapterNamesResults.indices) {
            val res = scanManga.getChapterTitle(formattedChapterNamesResults[i])

            println("Asserting: '" + chapterTitlesResults[i] + "' (Input: '" + formattedChapterNamesResults[i] + "')")
            println("with:      '$res'")
            assertEquals(chapterTitlesResults[i], res)
        }

        println("\nDone.\n")
    }

    @Test
    fun pickChapterRandomRun() {
        println("Test: testGetChaptersInRandomOrder\n")

        // Lets randomize that and try
        var counter = 10000
        val random = Random

        println("Random run: $counter tries")

        while (counter-- > 0) {
            val rnd = random.nextInt(formattedChapterNamesResults.size)
            val res = scanManga.pickChapter(formattedChapterNamesResults[rnd])

            assertEquals(chaptersResults[rnd], res)
        }

        println("\nDone.\n")
    }
}