package org.slos.battle;

public class ExccededAllowedTimeRuntimeException extends RuntimeException {

    public ExccededAllowedTimeRuntimeException() {
        super("Process has exceeded allowed time.");
    }
}
