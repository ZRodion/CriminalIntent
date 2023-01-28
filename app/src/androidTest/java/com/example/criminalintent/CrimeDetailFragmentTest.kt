package com.example.criminalintent

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.FragmentScenario.Companion.launchInContainer
import androidx.fragment.app.testing.launchFragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CrimeDetailFragmentTest {

    //private lateinit var scenario: FragmentScenario<CrimeDetailFragment>

    /*@Before
    fun setUp() {
        scenario = launchFragmentInContainer<CrimeDetailFragment>()
    }

    @After
    fun tearDown() {
        scenario.close()
    }*/

    @Test
    fun checkEditText(){
        val scenario = launchFragmentInContainer<CrimeDetailFragment>()
    }
}