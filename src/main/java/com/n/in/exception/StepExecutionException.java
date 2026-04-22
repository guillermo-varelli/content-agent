package com.n.in.exception;

public class StepExecutionException extends RuntimeException {
    public StepExecutionException(String stepName, Throwable cause) {
        super("Step execution failed [" + stepName + "]: " + cause.getMessage(), cause);
    }
}
