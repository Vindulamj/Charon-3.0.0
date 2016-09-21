package org.wso2.charon.core.schema;

/**
 * this defines the pre-defined values specified in https://tools.ietf.org/html/rfc7643
 */
public class SCIMDefinitions {

    /*data types that an attribute can take, according to the SCIM spec. */

    public static enum DataType {
        STRING, BOOLEAN, DECIMAL, INTEGER, DATE_TIME, BINARY, REFERENCE, COMPLEX
    }

    /*values that an attributes' mutability attribute can take*/

    public static enum Mutability {
        READ_WRITE, READ_ONLY, IMMUTABLE, WRITE_ONLY
    }

    /*values that an attributes' returned attribute can take*/

    public static enum Returned {
        ALWAYS, NEVER, DEFAULT, REQUEST
    }

    /*values that an attributes' uniqueness attribute can take*/

    public static enum Uniqueness {
        NONE, SERVER, GLOBAL
    }

    /*SCIM resource types that a referenceType attribute that may be referenced*/
    public static enum ReferenceType {
        USER, GROUP, EXTERNAL, URI
    }

}
