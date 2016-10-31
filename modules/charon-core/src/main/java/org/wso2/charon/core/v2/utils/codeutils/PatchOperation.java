package org.wso2.charon.core.v2.utils.codeutils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class represents the PATCH operations which are in the body of PATCH request
 */
public class PatchOperation {

    private String operation;
    private String path;
    private Object values;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Object getValues() {
        return values;
    }

    public void setValues(Object values) {
        this.values = values;
    }
}
