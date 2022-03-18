package es.edufdezsoy.manga2kindle.utils

import es.edufdezsoy.manga2kindle.data.model.ChapterWithManga
import net.greypanther.natsort.CaseInsensitiveSimpleNaturalComparator

class ChapterWithMangaComparator {

    class DefaultComparator : Comparator<ChapterWithManga> {
        private val natSort = CaseInsensitiveSimpleNaturalComparator.getInstance<String>()

        override fun compare(o1: ChapterWithManga, o2: ChapterWithManga): Int {
            var i = natSort.compare(o1.manga.manga.title, o2.manga.manga.title)
            if (i != 0) return i

            i = o1.chapter.chapter.compareTo(o2.chapter.chapter)
            if (i != 0) return i

            return natSort.compare(o1.chapter.title, o2.chapter.title)
        }
    }

    class ChapterComparator : Comparator<ChapterWithManga> {
        private val natSort = CaseInsensitiveSimpleNaturalComparator.getInstance<String>()

        override fun compare(o1: ChapterWithManga, o2: ChapterWithManga): Int {
            val i = o1.chapter.chapter.compareTo(o2.chapter.chapter)
            if (i != 0) return i

            return natSort.compare(o1.manga.manga.title, o2.manga.manga.title)
        }
    }
}