package com.thesis.routegenerator

import android.os.SystemClock
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.thesis.routegenerator.ui.MainActivity
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Rule
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import org.hamcrest.Matchers
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector
import com.thesis.routegenerator.adapter.MultiSelectionAdapter
import com.thesis.routegenerator.adapter.PlaceArrayAdapter
import com.thesis.routegenerator.ui.HomeActivity
import org.junit.Before

@RunWith(AndroidJUnit4::class)
class FragmentInstrumentedTest1{

    @Rule
    @JvmField
    val ruleHome: ActivityTestRule<HomeActivity> = ActivityTestRule(HomeActivity::class.java)


    @Test fun home_fragment_test() {



        SystemClock.sleep(1000)
        onView(withId(R.id.home_next_btn)).perform(click())
        SystemClock.sleep(1000)

        onView(withIndex(withId(R.id.rv_place),0)).perform(
            RecyclerViewActions.actionOnItem<MultiSelectionAdapter.MultiSelectionViewHolder>(
                hasDescendant(withText("Museum")), click()))

        onView(withIndex(withId(R.id.rv_place),0)).perform(
            RecyclerViewActions.actionOnItem<MultiSelectionAdapter.MultiSelectionViewHolder>(
                hasDescendant(withText("Park")), click()))

        onView(withIndex(withId(R.id.rv_place),0)).perform(
            RecyclerViewActions.actionOnItem<MultiSelectionAdapter.MultiSelectionViewHolder>(
                hasDescendant(withText("Bar")), click()))

        SystemClock.sleep(1000)
        onView(withIndex(withId(R.id.ic_multi_next),0)).perform(click())


        SystemClock.sleep(500)
        onView(withIndex(withId(R.id.radio2),0)).perform(click())
        SystemClock.sleep(1000)
        onView(withIndex(withId(R.id.ic_priority_next),0)).perform(click())


        SystemClock.sleep(1000)
        onView(withId(R.id.et_starting_location)).perform(typeText("Politechnika Warszawska"))
        SystemClock.sleep(1000)
        onView(withIndex(withId(R.id.rv_place_names),0)).perform(RecyclerViewActions.actionOnItemAtPosition<PlaceArrayAdapter.PlaceArrayViewHolder>
            (0, click()))

        onView(withId(R.id.et_ending_location)).perform(typeText("Student House Riviera"))
        SystemClock.sleep(1000)
        onView(withIndex(withId(R.id.rv_place_names),0)).perform(RecyclerViewActions.actionOnItemAtPosition<PlaceArrayAdapter.PlaceArrayViewHolder>
            (0, click()))

        onView(withIndex(withId(R.id.ic_location_next),0)).perform(click())

        onView(withIndex(withId(R.id.date_from),0)).perform(click())
        SystemClock.sleep(500)
        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name))).
            perform(PickerActions.setDate(2020, 1, 28))
        SystemClock.sleep(500)
        onView(withText("OK")).perform(click())
        SystemClock.sleep(500)

        onView(withIndex(withId(R.id.time_from),0)).perform(click())
        SystemClock.sleep(500)
        onView(withClassName(Matchers.equalTo(TimePicker::class.java.name))).
            perform(PickerActions.setTime(9, 0))
        SystemClock.sleep(500)
        onView(withText("OK")).perform(click())
        SystemClock.sleep(500)

        onView(withIndex(withId(R.id.time_to),0)).perform(click())
        SystemClock.sleep(500)
        onView(withClassName(Matchers.equalTo(TimePicker::class.java.name))).
            perform(PickerActions.setTime(14, 0))
        SystemClock.sleep(500)
        onView(withText("OK")).perform(click())
        SystemClock.sleep(500)

        onView(withIndex(withId(R.id.ic_time_next),0)).perform(click())
        //SystemClock.sleep(2000)



    }

    fun clickOnViewChild(viewId: Int) = object : ViewAction {
        override fun getConstraints() = null

        override fun getDescription() = "Click on a child view with specified id."

        override fun perform(uiController: UiController, view: View) = click().perform(uiController, view.findViewById<View>(viewId))
    }

    private fun withIndex(matcher: Matcher<View>, index: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            var currentIndex = 0

            override fun describeTo(description: Description) {
                description.appendText("with index: ")
                description.appendValue(index)
                matcher.describeTo(description)
            }

            override fun matchesSafely(view: View): Boolean {
                return matcher.matches(view) && currentIndex++ == index
            }
        }
    }

}
