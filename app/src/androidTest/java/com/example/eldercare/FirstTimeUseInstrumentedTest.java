package com.example.eldercare;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.core.app.ActivityScenario;

import com.example.eldercare.account_view.FirstTimeUse;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class FirstTimeUseInstrumentedTest {

    @Test
    public void testCaregiverButton() {
        ActivityScenario<FirstTimeUse> scenario = ActivityScenario.launch(FirstTimeUse.class);

        onView(withId(R.id.caregiverButton)).perform(click());

        scenario.close();
    }

    @Test
    public void testElderlyButton() {
        ActivityScenario<FirstTimeUse> scenario = ActivityScenario.launch(FirstTimeUse.class);

        onView(withId(R.id.elderlyButton)).perform(click());

        scenario.close();
    }
}
