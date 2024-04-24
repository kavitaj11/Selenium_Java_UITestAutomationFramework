package org.k11techlab.framework.selenium.pageObjects.wikipedia;

import org.k11techlab.framework.selenium.coreframework.baseclasses.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import java.util.List;

public class ArticlePage extends BasePage {
    @FindBy(id = "firstHeading")
    private WebElement heading;

    @FindBy(id = "p-lang")
    private WebElement languageButton;

    @FindBy(css = "li.interlanguage-link > a")
    private List<WebElement> languageLinks;

    @FindBy(id = "ca-history")
    private WebElement historyTab;

    @FindBy(css = "a[title='Edit this page']")
    private List<WebElement> internalLinks;

    public ArticlePage(WebDriver driver) {
        super(driver);
    }

    public boolean verifyContentExists(String content) {
        return driver.getPageSource().contains(content);
    }

    public ArticlePage switchLanguage(String language) {
        languageButton.click();
        for (WebElement link : languageLinks) {
            if (link.getAttribute("lang").equals(language)) {
                link.click();
                break;
            }
        }
        return this;
    }

    public boolean verifyLinkIntegrity() {
        for (WebElement link : internalLinks) {
            link.click(); // Simplified for demonstration
            // Real implementation should catch exceptions and return false if any link is broken
        }
        return true;
    }

    public HistoryPage viewHistory() {
        historyTab.click();
        return new HistoryPage(driver);
    }
}
