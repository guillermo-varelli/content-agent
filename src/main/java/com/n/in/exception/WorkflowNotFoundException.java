package com.n.in.exception;

public class WorkflowNotFoundException extends RuntimeException {
    public WorkflowNotFoundException(Integer workflowId) {
        super("Workflow not found: " + workflowId);
    }
}
