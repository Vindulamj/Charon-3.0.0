package org.wso2.charon.core.exceptions;

import org.wso2.charon.core.protocol.ResponseCodeConstants;

/**
 * If the request is unparsable, syntactically incorrect, or violates schema., this exception is thrown.
 * HTTP error code is: 400 BAD REQUEST
 */
public class BadRequestException extends AbstractCharonException  {

    //A SCIM detail error keyword.
    protected String scimType;

    public BadRequestException(String scimType) {
        status = ResponseCodeConstants.CODE_BAD_REQUEST;
        detail = ResponseCodeConstants.DESC_BAD_REQUEST;
        this.scimType=scimType;
    }

    public BadRequestException(String details, String scimType) {
        status =ResponseCodeConstants.CODE_BAD_REQUEST;
        this.detail=details;
        this.scimType=scimType;
    }

    public String getScimType() { return scimType; }

    public void setScimType(String scimType) { this.scimType = scimType; }

}
