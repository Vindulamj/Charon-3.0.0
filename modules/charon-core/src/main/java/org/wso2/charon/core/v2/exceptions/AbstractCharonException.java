package org.wso2.charon.core.v2.exceptions;


import org.wso2.charon.core.v2.protocol.ResponseCodeConstants;

/**
 * If an error occurs in SCIM operation,in addition to returning the HTTP response code,
 * an human readable explanation should also be returned in the body.
 * This class abstract out the Exceptions that should be thrown at a failure of SCIM operation and
 * implementers can use code property to decide which HTTP code needs to be set in header of the
 * response.
 */
public class AbstractCharonException extends Exception {

    //Error responses are identified using the following "schema" URI
    protected String schemas;

    //A detailed human-readable message.
    protected String detail;

    //The HTTP status code
    protected int status;

    public AbstractCharonException(int status,String detail, String scimType) {
        this.schemas= ResponseCodeConstants.ERROR_RESPONSE_SCHEMA_URI;
        this.status=status;
        this.detail=detail;
    }
    public AbstractCharonException() {
        this.schemas= ResponseCodeConstants.ERROR_RESPONSE_SCHEMA_URI;
        this.status = -1;
        this.detail = null;
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause. Note that the detail message associated with
     * causeis not automatically incorporated in this exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A null value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     */
    public AbstractCharonException(String message, Throwable cause) {
        super(message, cause);
        this.status = 600;
        this.detail = message;
    }

    public AbstractCharonException(String message) {
        this.status = 600;
        this.detail = message;
    }

    public String getSchemas() { return schemas; }

    public void setSchemas(String schemas) { this.schemas = schemas; }

    public String getDetail() { return detail; }

    public void setDetail(String detail) { this.detail = detail; }

    public int getStatus() { return status; }

    public void setStatus(int status) { this.status = status; }
}


