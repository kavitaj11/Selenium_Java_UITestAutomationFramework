package org.k11techlab.framework.selenium.coreframework.retryanalyzer;

import org.k11techlab.framework.selenium.coreframework.exceptions.AutomationError;
import org.k11techlab.framework.selenium.coreframework.logger.Log;
import org.k11techlab.framework.selenium.coreframework.configManager.ConfigurationManager;
import org.k11techlab.framework.selenium.coreframework.commonUtil.StringUtil;
import org.k11techlab.framework.selenium.coreframework.enums.ApplicationProperties;
import org.testng.*;

import java.util.List;

public class RetryAnalyzer implements IRetryAnalyzer {

    public static String RETRY_INVOCATION_COUNT = "retry.invocation.count";

    @Override
    public boolean retry(ITestResult result) {
        boolean shouldRetry = shouldRetry(result);
        if (shouldRetry) {
            int retryInvocationCount = getRetryCount() + 1;
            Log.info(
                    "Retrying [" + result.getName() + "] " + StringUtil.toStringWithSufix(retryInvocationCount) + " time.", true);

            ConfigurationManager.getBundle().addProperty(RETRY_INVOCATION_COUNT, retryInvocationCount);

            // correct failed invocation numbers for data driven test case.
            List<Integer> failedInvocations = result.getMethod().getFailedInvocationNumbers();
            if (null != failedInvocations && !failedInvocations.isEmpty()) {
                int lastFailedIndex = failedInvocations.size() - 1;
                failedInvocations.remove(lastFailedIndex);
            }
        } else {
            ConfigurationManager.getBundle().clearProperty(RETRY_INVOCATION_COUNT);
        }
        return shouldRetry;
    }

    public boolean shouldRetry(ITestResult result) {
        Throwable reason = result.getThrowable();
        int retryCount = getRetryCount();
        boolean shouldRetry = (result.getStatus() == ITestResult.FAILURE) && reason != null
                && !(reason instanceof AutomationError)
                && !(reason instanceof AssertionError)
                && (ApplicationProperties.RETRY_CNT.getIntVal(0) > retryCount);
        return shouldRetry;
    }

    protected int getRetryCount() {
        return ConfigurationManager.getBundle().getInt(RETRY_INVOCATION_COUNT, 0);
    }
}
