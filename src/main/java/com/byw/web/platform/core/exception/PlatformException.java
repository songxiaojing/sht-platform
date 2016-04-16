package com.byw.web.platform.core.exception;

public class PlatformException extends Exception {

    /**
     * Your field description in here.
     */
    private static final long serialVersionUID = -6028836965903726351L;
    private String _exceptionMassage = null;

    public PlatformException(String cause, Throwable able) {

        super(cause, able);
        this._exceptionMassage = cause;
    }

    public PlatformException(String exceptionMassage) {

        super(exceptionMassage);
        this._exceptionMassage = exceptionMassage;
    }

    public String getExceptionMassage() {

        return _exceptionMassage;
    }

}
