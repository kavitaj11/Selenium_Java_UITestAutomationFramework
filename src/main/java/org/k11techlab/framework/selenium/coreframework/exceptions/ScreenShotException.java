package org.k11techlab.framework.selenium.coreframework.exceptions;

public class ScreenShotException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * @param t The cause of the exception.
     */
    public ScreenShotException(Throwable t) {
        super("Error while taking screenshot", t);
    }
}
