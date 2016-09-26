package org.wso2.charon.core.exceptions;

import org.wso2.charon.core.protocol.ResponseCodeConstants;

/**
 * If the request is unparsable, syntactically incorrect, or violates schema., this exception is thrown.
 * HTTP error code is: 400 BAD REQUEST
 */
public class BadRequestException extends AbstractCharonException  {

    public BadRequestException(String scimType) {
        status = ResponseCodeConstants.CODE_BAD_REQUEST;
        detail = ResponseCodeConstants.DESC_BAD_REQUEST;
        this.scimType=scimType;
    }

}
