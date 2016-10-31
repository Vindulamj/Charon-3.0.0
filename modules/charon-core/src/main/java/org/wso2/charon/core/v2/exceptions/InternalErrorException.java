package org.wso2.charon.core.v2.exceptions;

import org.wso2.charon.core.v2.protocol.ResponseCodeConstants;

/**
 * Created by vindula on 9/19/16.
 */
public class InternalErrorException extends AbstractCharonException  {
    public InternalErrorException(String error) {
        this.schemas= ResponseCodeConstants.ERROR_RESPONSE_SCHEMA_URI;
        this.status=ResponseCodeConstants.CODE_INTERNAL_ERROR;
        this.detail=error;
    }
}