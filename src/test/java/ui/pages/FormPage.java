package ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import java.nio.file.Paths;
import static config.TestConfig.UI_BASE_URL;

public class FormPage {
    // mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="codegen https://demoqa.com/automation-practice-form"
    private final Page page;

    private final Locator firstNameInput;
    private final Locator lastNameInput;
    private final Locator emailInput;
    private final Locator genderMaleRadio;
    private final Locator mobileInput;
    private final Locator subjectsInput;
    private final Locator hobbiesSportsCheckbox;
    private final Locator uploadPictureInput;
    private final Locator currentAddressInput;
    private final Locator stateDropdown;
    private final Locator cityDropdown;
    private final Locator submitButton;
    private final Locator successModalTitle;

    public FormPage(Page page) {
        this.page = page;

        this.firstNameInput = page.getByPlaceholder("First Name");
        this.lastNameInput = page.getByPlaceholder("Last Name");
        this.emailInput = page.getByPlaceholder("name@example.com");
        this.mobileInput = page.getByPlaceholder("Mobile Number");
        this.currentAddressInput = page.getByPlaceholder("Current Address");
        this.genderMaleRadio = page.getByRole(AriaRole.RADIO, new Page.GetByRoleOptions().setName("Male").setExact(true));
        this.hobbiesSportsCheckbox = page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Sports"));
        this.stateDropdown = page.locator("#state");
        this.cityDropdown = page.locator("#city");
        this.submitButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit"));
        this.successModalTitle = page.getByText("Thanks for submitting the form");
        this.subjectsInput = page.locator("#subjectsInput");
        this.uploadPictureInput = page.locator("#uploadPicture");
    }

    public FormPage open() {
        page.navigate(UI_BASE_URL + "/automation-practice-form");
        return this;
    }

    public FormPage fillPersonalData(String firstName, String lastName, String email, String mobile) {
        firstNameInput.fill(firstName);
        lastNameInput.fill(lastName);
        genderMaleRadio.click();
        emailInput.fill(email);
        mobileInput.fill(mobile);
        return this;
    }

    public FormPage setSubject(String subject) {
        subjectsInput.scrollIntoViewIfNeeded();
        subjectsInput.fill(subject);
        subjectsInput.press("Enter");
        return this;
    }

    public FormPage selectHobbies() {
        hobbiesSportsCheckbox.check();
        return this;
    }

    public FormPage uploadFile(String filePath) {
        uploadPictureInput.scrollIntoViewIfNeeded();
        uploadPictureInput.setInputFiles(Paths.get(filePath));
        return this;
    }

    public FormPage fillAddress(String address, String state, String city) {
        currentAddressInput.scrollIntoViewIfNeeded();
        currentAddressInput.fill(address);
        stateDropdown.click();
        stateDropdown.getByText(state, new Locator.GetByTextOptions().setExact(true)).click();
        cityDropdown.click();
        cityDropdown.getByText(city, new Locator.GetByTextOptions().setExact(true)).click();
        return this;
    }

    public FormPage submitForm() {
        submitButton.scrollIntoViewIfNeeded();
        submitButton.click();
        return this;
    }

    public String getSuccessModalText() {
        successModalTitle.waitFor();
        return successModalTitle.innerText().trim();
    }

    public boolean isSuccessModalVisible() {
        return successModalTitle.isVisible();
    }
}
