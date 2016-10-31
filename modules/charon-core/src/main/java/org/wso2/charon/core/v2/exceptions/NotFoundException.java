package org.wso2.charon.core.v2.exceptions;


import org.wso2.charon.core.v2.protocol.ResponseCodeConstants;

/**
 * If the requested resource is not found, this exception is thrown.
 * HTTP error code is: 404 NOT FOUND
 */
public class NotFoundException extends AbstractCharonException {

    public NotFoundException() {
        status = ResponseCodeConstants.CODE_RESOURCE_NOT_FOUND;
        detail = ResponseCodeConstants.DESC_RESOURCE_NOT_FOUND;
    }

    public NotFoundException(String detail) {
        status = ResponseCodeConstants.CODE_RESOURCE_NOT_FOUND;
        this.detail = detail;
    }
}
