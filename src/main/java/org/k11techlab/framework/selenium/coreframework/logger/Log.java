package org.k11techlab.framework.selenium.coreframework.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.testng.Reporter;
import org.k11techlab.framework.selenium.coreframework.enums.*;

import java.nio.file.Paths;

/**
 * This class encapsulates all logging operations and provides methods to log
 * messages in both the console and a file specific to each test run.
 */
public class Log {
    public static final Logger LOGGER = LogManager.getLogger(Log.class);

    static {
        setupLoggers();
    }

    private static void setupLoggers() {
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        builder.setStatusLevel(org.apache.logging.log4j.Level.ERROR);
        builder.setConfigurationName("LoggerConfig");

        // Define Pattern Layout
        String pattern = "%d{dd MMM yyyy HH:mm:ss} %5p %c{1} - %m%n";
        builder.newAppender("stdout", "Console")
               .addAttribute("target", "SYSTEM_OUT")
               .add(builder.newLayout("PatternLayout")
                            .addAttribute("pattern", pattern));

        // Define File Appender
        String logFilePath = Paths.get(ApplicationProperties.REPORT_DIR.getStringVal(), ApplicationProperties.REPORT_LOG_FILE_NAME.getStringVal()).toString();
        builder.newAppender("logFile", "File")
               .addAttribute("fileName", logFilePath)
               .addAttribute("append", true)
               .add(builder.newLayout("PatternLayout")
                            .addAttribute("pattern", pattern));

        // Add appenders to root logger
        builder.add(builder.newRootLogger(org.apache.logging.log4j.Level.INFO)
                           .add(builder.newAppenderRef("stdout"))
                           .add(builder.newAppenderRef("logFile")));

        Configurator.initialize(builder.build());
    }

    public static void info(String message) {
        LOGGER.info(message);
    }

    public static void info(String message, boolean logToReport) {
        LOGGER.info(message);
        if (logToReport) {
            Reporter.log(message);
        }
    }

    public static void error(String message) {
        LOGGER.error(message);
    }

    public static void debug(String message) {
        LOGGER.debug(message);
    }
}
