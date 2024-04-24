package org.k11techlab.framework.selenium.coreframework.retryanalyzer;

import org.k11techlab.framework.selenium.coreframework.enums.ApplicationProperties;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.testng.ITestListener;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.TestListenerAdapter;
import org.testng.*;

import java.util.Set;
import java.util.HashSet;

public class RetryTestListenerAdapter extends TestListenerAdapter implements ITestListener{

    public void onTestFailure(ITestResult result) {

        IRetryAnalyzer analyzer = result.getMethod().getRetryAnalyzer(result);

        if(!(ApplicationProperties.RETRY_CNT.getIntVal()==0)) {
            if (analyzer!=null && analyzer instanceof RetryAnalyzer) {
                RetryAnalyzer retryAnalyzer = (RetryAnalyzer) analyzer;
                if (retryAnalyzer.getRetryCount()>0) {
                    result.setStatus(ITestResult.SKIP);
                } else {
                 result.setStatus(ITestResult.FAILURE);
                }
                Reporter.setCurrentTestResult(result);
            }
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        Set<ITestResult> failedTests = context.getFailedTests().getAllResults();
        Set<ITestResult> testsToRemove = new HashSet<>();

        for (ITestResult temp : failedTests) {
            ITestNGMethod method = temp.getMethod();
            if (context.getFailedTests().getResults(method).size() > 1 || context.getPassedTests().getResults(method).size() > 0) {
                testsToRemove.add(temp);
            }
        }

        failedTests.removeAll(testsToRemove); // Safely remove all collected tests at once
    }



}
