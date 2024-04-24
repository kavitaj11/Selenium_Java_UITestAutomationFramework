package org.k11techlab.webUITests.wikipedia;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.k11techlab.framework.selenium.coreframework.driverUtil.*;
import org.k11techlab.framework.selenium.coreframework.baseclasses.*;
import java.util.concurrent.TimeUnit;
import org.k11techlab.framework.selenium.pageObjects.wikipedia.*;
import org.testng.annotations.BeforeTest;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class WikipediaTests extends BaseSeleniumTest{
    private WebDriver driver;
    HomePage homePage;

    @BeforeMethod
    public void start(){
        driver= getDriver();
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        driver.get("https://www.wikipedia.org/");
        homePage= new HomePage(driver);
    }

    @Test
    public void testSearch() {
            WebElement searchInput = driver.findElement(By.id("searchInput"));
            searchInput.sendKeys("Selenium (software)");
            WebElement searchButton = driver.findElement(By.xpath("//button[@type='submit']"));
            searchButton.click();

            WebElement heading = driver.findElement(By.id("firstHeading"));
            assert(heading.getText().contains("Selenium"));
    }

    @Test
    public void testContentVerification() {
        String articleName = "Selenium (software)";
        String expectedContent = "Selenium is a portable framework for testing web applications.";
        ArticlePage articlePage = homePage.searchForArticle(articleName);
        assertTrue(articlePage.verifyContentExists(expectedContent),
                   "The article does not contain the expected text.");
    }

    @Test
    public void testLanguageSwitching() {
        String articleName = "Selenium (software)";
        ArticlePage articlePage = homePage.searchForArticle(articleName);
        articlePage = articlePage.switchLanguage("de");
        assertTrue(articlePage.verifyContentExists("Selenium ist ein Framework"),
                   "The article did not switch to the expected language.");
    }

    @Test
    public void testLinkIntegrity() {
        String articleName = "Selenium (software)";
        ArticlePage articlePage = homePage.searchForArticle(articleName);
        assertTrue(articlePage.verifyLinkIntegrity(),
                   "One or more internal links are broken.");
    }

    @Test
    public void testHistoryPageVerification() {
        String articleName = "Selenium (software)";
        ArticlePage articlePage = homePage.searchForArticle(articleName);
        HistoryPage historyPage = articlePage.viewHistory();
        assertTrue(historyPage.isAtHistoryPage(),
                   "The history page does not show the expected content.");
    }

    @AfterSuite
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
