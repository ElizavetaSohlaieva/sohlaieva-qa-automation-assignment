package ui.tests;

import org.junit.jupiter.api.*;
import ui.base.BaseUITest;
import ui.pages.WebTablesPage;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("ui")
public class WebTablesTest extends BaseUITest {

    @Test
    @DisplayName("Add a new record to Web Tables")
    void testAddNewRecord() {
        WebTablesPage webTablesPage = new WebTablesPage(page);
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String firstName = "Auto" + uniqueId;
        String email = "auto" + uniqueId + "@test.com";

        webTablesPage.open()
                .addRecord()
                .fillPersonalData(firstName, "Tester", email, "30", "50000", "QA")
                .submitRecord();

        assertThat(webTablesPage.getRowByText(firstName).isVisible());
        assertThat(webTablesPage.getRowByText(email).isVisible());
    }

    @Test
    @DisplayName("Edit an existing record in Web Tables")
    void testEditExistingRecord() {
        WebTablesPage webTablesPage = new WebTablesPage(page);
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String originalFirstName = "Edit" + uniqueId;
        String updatedFirstName = "Updated" + uniqueId;
        String email = "edit" + uniqueId + "@test.com";

        webTablesPage.open()
                .addRecord()
                .fillPersonalData(originalFirstName, "Before", email, "25", "40000", "Dev")
                .submitRecord();

        webTablesPage.editRecord(email)
                .fillPersonalData(updatedFirstName, "After", email, "26", "45000", "Dev")
                .submitRecord();

        assertThat(webTablesPage.getRowByText(updatedFirstName).isVisible())
                .as("New first name should appear in table")
                .isTrue();

        assertThat(webTablesPage.getRowByText(originalFirstName).isHidden())
                .as("Old first name should not appear in any table row");
    }

    @Test
    @DisplayName("Delete a record from Web Tables")
    void testDeleteRecord() {
        WebTablesPage webTablesPage = new WebTablesPage(page);
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String firstName = "Delete" + uniqueId;
        String email = "delete" + uniqueId + "@test.com";

        webTablesPage.open()
                .addRecord()
                .fillPersonalData(firstName, "Remove", email, "28", "42000", "QA")
                .submitRecord();

        assertThat(webTablesPage.getRowByText(firstName).isVisible());

        webTablesPage.deleteRecord(firstName);

        assertThat(webTablesPage.getRowByText(firstName).isHidden());
    }

    @Test
    @DisplayName("Search filters rows in Web Tables")
    void testSearchFunctionality() {
        WebTablesPage webTablesPage = new WebTablesPage(page);
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String firstName = "Search" + uniqueId;
        String email = "search" + uniqueId + "@test.com";
        String nonExistingName = "NonExistentName12345";

        webTablesPage.open()
                .addRecord()
                .fillPersonalData(firstName, "Search", email, "28", "42000", "QA")
                .submitRecord();

        webTablesPage.searchFor(firstName);

        assertThat(webTablesPage.getRowByText(email).isVisible())
                .as("Row with email '%s' should be visible after searching", email)
                .isTrue();

        webTablesPage.searchFor(nonExistingName);
        assertThat(webTablesPage.getRowByText("NonExistentName12345").isVisible())
                .as("Row with name '%s' should be visible after searching", nonExistingName)
                .isFalse();
    }
}