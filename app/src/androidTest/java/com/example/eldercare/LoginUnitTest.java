package com.example.eldercare;

import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.eldercare.account_view.Login;
import com.example.eldercare.modules.DatabaseLib;
import com.google.android.material.textfield.TextInputEditText;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class LoginUnitTest {

    @Mock
    DatabaseLib mockDatabaseLib;
    @Mock
    SharedPreferences mockSharedPreferences;
    @Mock
    SharedPreferences.Editor mockEditor;

    private Login login;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this); // Initialize the mocks
        login = new Login();

        Mockito.when(mockSharedPreferences.getBoolean("isFirstTimeUse", true)).thenReturn(true);
        Mockito.when(mockSharedPreferences.getBoolean("rememberMe", false)).thenReturn(false);
        Mockito.when(mockSharedPreferences.getBoolean("isCaregiver", true)).thenReturn(true);
    }

    @Test
    public void testShowLoginCaregiverLayout() {
        // Mock EditText, Button, and other UI components
        TextInputEditText mockEditTextUsername = Mockito.mock(TextInputEditText.class);
        TextInputEditText mockEditTextPassword = Mockito.mock(TextInputEditText.class);
        Button mockLoginButton = Mockito.mock(Button.class);
        ProgressBar mockProgressBar = Mockito.mock(ProgressBar.class);
        TextView mockRegisterNow = Mockito.mock(TextView.class);
        TextView mockForgotButton = Mockito.mock(TextView.class);

        // Add assertions to verify that the appropriate UI components are set and listeners are added
    }
}
