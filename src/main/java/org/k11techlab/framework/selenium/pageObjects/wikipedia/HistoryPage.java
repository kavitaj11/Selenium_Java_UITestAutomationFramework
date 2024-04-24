package org.k11techlab.framework.selenium.pageObjects.wikipedia;

import org.k11techlab.framework.selenium.coreframework.baseclasses.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HistoryPage extends BasePage {
    @FindBy(css = "h1")
    private WebElement historyHeader;

    public HistoryPage(WebDriver driver) {
        super(driver);
    }

    public boolean isAtHistoryPage() {
        return historyHeader.getText().contains("Revision history");
    }
}
