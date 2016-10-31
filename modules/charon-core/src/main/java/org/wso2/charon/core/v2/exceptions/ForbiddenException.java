package org.wso2.charon.core.v2.exceptions;

import org.wso2.charon.core.v2.protocol.ResponseCodeConstants;

/**
 * Created by vindula on 9/19/16.
 */
public class ForbiddenException extends AbstractCharonException  {

    public ForbiddenException() {
        status = ResponseCodeConstants.CODE_FORBIDDEN;
        detail = ResponseCodeConstants.DESC_CONFLICT;
    }

    public ForbiddenException(String exception) {
        status = ResponseCodeConstants.CODE_FORBIDDEN;
        detail = exception;
    }

}
