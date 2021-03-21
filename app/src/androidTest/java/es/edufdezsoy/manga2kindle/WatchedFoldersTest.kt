package es.edufdezsoy.manga2kindle


import android.view.View
import android.view.ViewGroup
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import es.edufdezsoy.manga2kindle.data.model.Folder
import es.edufdezsoy.manga2kindle.data.repository.FolderRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
class WatchedFoldersTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun watchedFoldersHelpDialogTest() {
        // swipe onBoarding
        for (i in 0..3) {
            onView(withId(R.id.viewPager)).perform(swipeLeft())
        }

        val onBoardingFinishButton = onView(
            allOf(
                withId(R.id.nextBtn),
                withText(R.string.sliders_finish_button),
                isDisplayed()
            )
        )
        onBoardingFinishButton.perform(click())

        val navBarWatchedFoldersButton = onView(
            allOf(
                withId(R.id.nav_folders),
                withContentDescription("Folders"),
                isDisplayed()
            )
        )
        navBarWatchedFoldersButton.perform(click())
        Thread.sleep(300)

        val watchedFoldersBackgroundHelpLayout = onView(
            allOf(
                withId(R.id.watched_folders_background_help_text_layout),
                withParent(
                    allOf(
                        withId(R.id.watched_folders_background),
                        withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        watchedFoldersBackgroundHelpLayout.check(matches(isDisplayed()))

        val watchedFoldersBackgroundHelpLayoutToClick = onView(
            allOf(
                withId(R.id.watched_folders_background_help_text_layout),
                childAtPosition(
                    allOf(
                        withId(R.id.watched_folders_background),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            2
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        Thread.sleep(100)
        watchedFoldersBackgroundHelpLayoutToClick.perform(click())

        val watchedFoldersHelpDialog = onView(
            allOf(
                withId(R.id.md_root),
                withParent(
                    allOf(
                        withId(android.R.id.content),
                        withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        watchedFoldersHelpDialog.check(matches(isDisplayed()))

        val watchedFoldersDialogCloseButton = onView(
            allOf(
                withId(R.id.close_button),
                withText(R.string.watched_folders_help_dialog_close),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.md_content_layout),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        watchedFoldersDialogCloseButton.perform(click())

        val folderRepo = FolderRepository(ApplicationProvider.getApplicationContext())
        GlobalScope.launch {
            folderRepo.insert(
                Folder(
                    "MockFolder",
                    "Placeholder/Path/MockFolder",
                    true
                )
            )
        }
        Thread.sleep(100) // to wait for the insert

        watchedFoldersBackgroundHelpLayout.check(doesNotExist())

        // remove folder, if the scan service tries to use it will fail
        GlobalScope.launch {
            folderRepo.delete(folderRepo.getStaticFolderList().last())
        }
        Thread.sleep(100) // to wait for the delete

        watchedFoldersBackgroundHelpLayout.check(matches(isDisplayed()))
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
