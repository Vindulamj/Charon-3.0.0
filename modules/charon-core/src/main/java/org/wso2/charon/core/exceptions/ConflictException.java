package org.wso2.charon.core.exceptions;

import org.wso2.charon.core.protocol.ResponseCodeConstants;

/**
 * The specified version number does not match the resource's
 "latest version number, or a service provider refused to create a new, duplicate resource.
 */
public class ConflictException extends AbstractCharonException  {

    public ConflictException() {
        status = ResponseCodeConstants.CODE_CONFLICT;
        detail = ResponseCodeConstants.DESC_CONFLICT;
    }
}
