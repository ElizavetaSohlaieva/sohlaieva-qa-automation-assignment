package ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import static config.TestConfig.UI_BASE_URL;

public class WebTablesPage {

    private final Page page;

    private final Locator addButton;
    private final Locator typeToSearchInput;
    private final Locator firstNameInput;
    private final Locator lastNameInput;
    private final Locator emailInput;
    private final Locator ageInput;
    private final Locator salaryInput;
    private final Locator departmentInput;
    private final Locator submitButton;

    public WebTablesPage(Page page) {
        this.page = page;

        this.addButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add"));
        this.typeToSearchInput = page.getByPlaceholder("Type to search");
        this.firstNameInput = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("First Name"));
        this.lastNameInput = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Last Name"));
        this.emailInput = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("name@example.com"));
        this.ageInput = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Age"));
        this.salaryInput = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Salary"));
        this.departmentInput = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Department"));
        this.submitButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit"));
    }

    public WebTablesPage open() {
        page.navigate(UI_BASE_URL + "/webtables");
        return this;
    }

    public WebTablesPage fillPersonalData(String firstName, String lastName, String email, String age, String salary, String department) {
        firstNameInput.clear();
        firstNameInput.fill(firstName);
        lastNameInput.clear();
        lastNameInput.fill(lastName);
        emailInput.clear();
        emailInput.fill(email);
        ageInput.clear();
        ageInput.fill(age);
        salaryInput.clear();
        salaryInput.fill(salary);
        departmentInput.clear();
        departmentInput.fill(department);
        return this;
    }

    public WebTablesPage addRecord() {
        addButton.click();
        return this;
    }

    public WebTablesPage submitRecord() {
        submitButton.click();
        return this;
    }

    public Locator getRowByText(String uniqueText) {
        return page.getByRole(AriaRole.ROW).filter(new Locator.FilterOptions().setHasText(uniqueText));
    }

    public WebTablesPage editRecord(String uniqueText) {
        Locator row = getRowByText(uniqueText);
        row.getByTitle("Edit").click();
        return this;
    }

    public WebTablesPage deleteRecord(String uniqueText) {
        Locator row = getRowByText(uniqueText);
        row.getByTitle("Delete").click();
        return this;
    }

    public WebTablesPage searchFor(String query) {
        typeToSearchInput.clear();
        typeToSearchInput.fill(query);
        return this;
    }
}
