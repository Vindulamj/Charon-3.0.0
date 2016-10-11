package org.wso2.charon.core.schema;

import java.util.List;

/*
This interface defines the common schema base for SCIM attributes and SCIM sub attributes.
In such defines as the 'attributes' attribute in Resource Schema Representation in SCIM 2.0

 */
public interface AttributeSchema {

    public void setURI(String URI);

    public String getURI();

    public String getName();

    public void setName(String name);

    public SCIMDefinitions.DataType getType();

    public void setType(SCIMDefinitions.DataType type);

    public boolean getMultiValued();

    public void setMultiValued(boolean isMultiValued);

    public String getDescription();

    public void setDescription(String description);

    public boolean getRequired();

    public void setRequired(boolean isRequired);

    public boolean getCaseExact();

    public void setCaseExact(boolean isCaseExact);

    public SCIMDefinitions.Mutability getMutability();

    public void setMutability(SCIMDefinitions.Mutability mutability);

    public SCIMDefinitions.Returned getReturned ();

    public void setReturned(SCIMDefinitions.Returned returned);

    public SCIMDefinitions.Uniqueness getUniqueness();

    public void setUniqueness(SCIMDefinitions.Uniqueness uniqueness);

    public List<SCIMAttributeSchema> getSubAttributeSchemas();

    public AttributeSchema getSubAttributeSchema(String subAttribute);

}

