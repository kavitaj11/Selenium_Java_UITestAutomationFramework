package org.k11techlab.framework_unittests;

import org.k11techlab.framework.selenium.coreframework.configManager.*;
import org.testng.annotations.Test;

public class configurationManagerTests {

    @Test
    public void verifyConfigurations() {
        ConfigurationManager configManager = ConfigurationManager.getInstance();
        System.out.println("Application URL: " + configManager.getString("app.baseurl"));
        System.out.println("Database Host: " + configManager.getString("db.host"));

        PropertyUtil properties = new PropertyUtil();
        properties.loadConfiguration("config.properties");
        String title = properties.getString("app.title", "Default Title");
        System.out.println("Application Title: " + title);

    }
}
