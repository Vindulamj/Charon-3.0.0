package org.wso2.charon.core.utils.codeutils;

/**
 * Created by vindula on 10/10/16.
 */
public class OperationNode extends Node {

    private String operation;


    public OperationNode(String operation) {
        this.operation=operation;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
