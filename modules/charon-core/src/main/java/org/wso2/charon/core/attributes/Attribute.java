package org.wso2.charon.core.attributes;

import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.schema.SCIMDefinitions;

import java.io.Serializable;

/**
 * Interface to represent Attribute defined in SCIM schema spec.
 */
//Attribute is extended from Serializable as later in org.wso2.charon.core.util.CopyUtil, it need to be serialized.
public interface Attribute extends Serializable {

    public String getName();

    public SCIMDefinitions.DataType getType();

    public Boolean getMultiValued();

    public String getDescription();

    public Boolean getCaseExact();

    public SCIMDefinitions.Mutability getMutability();

    public SCIMDefinitions.Returned getReturned();

    public SCIMDefinitions.Uniqueness getUniqueness();

    public Attribute getSubAttribute(String attributeName) throws CharonException;

}
