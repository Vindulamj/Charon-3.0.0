package org.wso2.charon.core.attributes;

import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.schema.SCIMDefinitions;

/**
 * Interface to represent Attribute defined in SCIM schema spec.
 */
public interface Attribute {

    public String getName();

    public SCIMDefinitions.DataType getType();

    public Boolean getMultiValued();

    public String getDescription();

    public Boolean getRequired();

    public Boolean getCaseExact();

    public SCIMDefinitions.Mutability getMutability();

    public SCIMDefinitions.Returned getReturned();

    public SCIMDefinitions.Uniqueness getUniqueness();

    public Attribute getSubAttribute(String attributeName) throws CharonException;

}
