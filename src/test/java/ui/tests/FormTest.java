package ui.tests;

import org.junit.jupiter.api.*;
import ui.base.BaseUITest;
import ui.pages.FormPage;

import java.io.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Tag("ui")
public class FormTest extends BaseUITest {

    @Test
    @DisplayName("User should be able to submit the registration form successfully")
    void testSuccessfulFormSubmission() throws IOException {

        File tempFile = File.createTempFile("test_image", ".png");
        tempFile.deleteOnExit();
        FormPage formPage = new FormPage(page);

        String successfulMessage = formPage.open()
                .fillPersonalData("John", "Smith", "johnsmith@test.com", "1234567890")
                .setSubject("Maths")
                .selectHobbies()
                .uploadFile(tempFile.getAbsolutePath())
                .fillAddress("123 Main Street, 4B", "NCR", "Delhi")
                .submitForm()
                .getSuccessModalText();

        assertThat(successfulMessage)
                .isEqualTo("Thanks for submitting the form");
    }

    @Test
    @DisplayName("User should not be able to submit the empty registration form")
    void testEmptyFormSubmission() {
        FormPage formPage = new FormPage(page);

        formPage.open()
                .submitForm();

        assertThat(formPage.isSuccessModalVisible()).isFalse();
    }
}