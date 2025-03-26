package org.exceptions;

public class JadePlatformInitializationException extends RuntimeException {

    public JadePlatformInitializationException(final Throwable e){
        super("JADE initialization failed ...", e);
    }
}
