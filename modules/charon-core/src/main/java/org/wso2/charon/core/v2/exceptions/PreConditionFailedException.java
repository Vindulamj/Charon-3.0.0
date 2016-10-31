package org.wso2.charon.core.v2.exceptions;


import org.wso2.charon.core.v2.protocol.ResponseCodeConstants;

public class PreConditionFailedException extends AbstractCharonException  {
    public PreConditionFailedException() {
        status = ResponseCodeConstants.CODE_PRECONDITION_FAILED;
        detail = ResponseCodeConstants.DESC_PRECONDITION_FAILED;
    }
}
