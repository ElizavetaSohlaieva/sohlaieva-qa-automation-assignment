package ui.base;

import com.microsoft.playwright.*;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.*;

public class BaseUITest {

    protected static Playwright playwright;
    protected static Browser browser;
    protected BrowserContext context;
    protected Page page;

    private static final Path SCREENSHOT_DIR = Paths.get("target", "screenshots");

    @RegisterExtension
    final AfterTestExecutionCallback screenshotOnFailure = context -> {
        if (context.getExecutionException().isEmpty() || page == null) {
            return;
        }
        attachScreenshotOnFailure(context.getRequiredTestMethod().getName());
    };

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        boolean headless = "true".equalsIgnoreCase(System.getenv("CI"));
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(headless));
    }

    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext(
                new Browser.NewContextOptions().setViewportSize(1920, 1080));
        page = context.newPage();
    }

    @AfterEach
    void tearDownAfterTest() {
        if (context != null) {
            context.close();
            context = null;
            page = null;
        }
    }

    private void attachScreenshotOnFailure(String testMethodName) {
        try {
            Files.createDirectories(SCREENSHOT_DIR);
            String fileName = testMethodName + "_failed.png";
            Path filePath = SCREENSHOT_DIR.resolve(fileName).toAbsolutePath();
            byte[] screenshotBytes = page.screenshot(new Page.ScreenshotOptions()
                    .setPath(filePath)
                    .setFullPage(true));
            Allure.addAttachment(
                    "Screenshot on failure",
                    "image/png",
                    new ByteArrayInputStream(screenshotBytes),
                    "png");
            System.out.println("Failure screenshot saved: " + filePath);
        } catch (IOException e) {
            System.err.println("Cannot save failure screenshot: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Screenshot skipped: " + e.getMessage());
        }
    }

    @AfterAll
    static void closeBrowser() {
        if (browser != null) {
            browser.close();
            browser = null;
        }
        if (playwright != null) {
            playwright.close();
            playwright = null;
        }
    }
}
