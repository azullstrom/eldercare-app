package com.example.eldercare;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ActivityScenario;
import com.example.eldercare.account_view.Login;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class LoginInstrumentedTest {

    @Test
    public void testLoginButton() {
        // Launch the Login activity
        ActivityScenario<Login> scenario = ActivityScenario.launch(Login.class);

        // Type a username (if necessary)
        onView(withId(R.id.username)).perform(typeText("azullstrom"));

        // Type a password (if necessary)
        onView(withId(R.id.password)).perform(typeText("hejhej123"));

        // Click the login button
        // onView(withId(R.id.loginButton)).perform(click());

        // Add assertions to verify the expected behavior after clicking the login button

        // Close the activity scenario
        scenario.close();
    }

    // You can add more test methods to cover different aspects of your Login activity
}
