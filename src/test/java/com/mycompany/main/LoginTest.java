package com.mycompany.main;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class LoginTest {

    @Test
    public void usernameCorrectlyFormatted() {
        assertEquals("Username successfully captured.", Login.usernameMessage("kyl_1"));
    }

    @Test
    public void usernameIncorrectlyFormatted() {
        assertEquals(Login.USERNAME_BAD, Login.usernameMessage("kyle!!!!!!!"));
    }

    @Test
    public void passwordMeetsComplexity() {
        assertEquals("Password successfully captured.", Login.passwordMessage("Ch&&sec@ke99!"));
    }

    @Test
    public void passwordDoesNotMeetComplexity() {
        assertEquals(Login.PASSWORD_BAD, Login.passwordMessage("password"));
    }

    @Test
    public void cellPhoneCorrectlyFormatted() {
        assertEquals(Login.CELL_OK, Login.cellPhoneMessage("+27838968976"));
    }

    @Test
    public void cellPhoneIncorrectlyFormatted() {
        assertEquals(Login.CELL_BAD, Login.cellPhoneMessage("08966553"));
    }

    @Test
    public void checkUserNameTrueAndFalse() {
        assertTrue(Login.checkUserName("kyl_1"));
        assertFalse(Login.checkUserName("kyle!!!!!!!"));
        assertFalse(Login.checkUserName("kyle1"));
    }

    @Test
    public void checkPasswordComplexityTrueAndFalse() {
        assertTrue(Login.checkPasswordComplexity("Ch&&sec@ke99!"));
        assertFalse(Login.checkPasswordComplexity("password"));
        assertFalse(Login.checkPasswordComplexity("Password1"));
    }

    @Test
    public void checkCellPhoneNumberTrueAndFalse() {
        assertTrue(Login.checkCellPhoneNumber("+27838968976"));
        assertFalse(Login.checkCellPhoneNumber("08966553"));
        assertFalse(Login.checkCellPhoneNumber("0838968976"));
    }

    @Test
    public void registerUserWithValidDetails() {
        Login login = new Login();
        String result = login.registerUser("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(result.endsWith(Login.REGISTER_OK));
        assertTrue(login.isRegistered());
    }

    @Test
    public void registerUserWithInvalidDetails() {
        Login login = new Login();
        String result = login.registerUser("Kyle", "Smith", "kyle!!!!!!!", "password", "08966553");
        assertTrue(result.contains(Login.USERNAME_BAD));
        assertTrue(result.contains(Login.PASSWORD_BAD));
        assertTrue(result.contains(Login.CELL_BAD));
        assertFalse(login.isRegistered());
    }

    @Test
    public void loginSuccessful() {
        Login login = new Login();
        login.registerUser("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    public void loginFailed() {
        Login login = new Login();
        login.registerUser("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertFalse(login.loginUser("kyl_1", "wrongPass1!"));
        assertFalse(login.loginUser("abc_1", "Ch&&sec@ke99!"));
    }

    @Test
    public void loginFailsBeforeRegistering() {
        assertFalse(new Login().loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    public void loginStatusMessages() {
        Login login = new Login();
        login.registerUser("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertEquals("Welcome Kyle, Smith it is great to see you again.", login.returnLoginStatus(true));
        assertEquals("Username or password incorrect, please try again.", login.returnLoginStatus(false));
    }
}
