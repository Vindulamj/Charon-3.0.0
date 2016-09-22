package org.wso2.charon.core.attributes;

import org.wso2.charon.core.schema.SCIMDefinitions;


public abstract class AbstractAttribute implements Attribute {

    //name of the attribute
    protected String name;
    //data type of the attribute
    protected SCIMDefinitions.DataType type;
    //Boolean value indicating the attribute's plurality.
    protected Boolean multiValued;
    //The attribute's human readable description
    protected String description;
    //A Boolean value that specifies if the attribute is required
    protected Boolean required;
    //A Boolean value that specifies if the String attribute is case sensitive
    protected Boolean caseExact;
    //A SCIM defined value that specifies if the attribute's mutability.
    protected SCIMDefinitions.Mutability mutability;
    //A SCIM defined value that specifies when the attribute's value need to be returned.
    protected SCIMDefinitions.Returned returned;
    //A SCIM defined value that specifies the uniqueness level of an attribute.
    protected SCIMDefinitions.Uniqueness uniqueness;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SCIMDefinitions.DataType getType() {
        return type;
    }

    public void setType(SCIMDefinitions.DataType type) {
        this.type = type;
    }

    public Boolean getMultiValued() {
        return multiValued;
    }

    public void setMultiValued(Boolean multiValued) {
        this.multiValued = multiValued;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean getCaseExact() {
        return caseExact;
    }

    public void setCaseExact(Boolean caseExact) {
        this.caseExact = caseExact;
    }

    public SCIMDefinitions.Mutability getMutability() {
        return mutability;
    }

    public void setMutability(SCIMDefinitions.Mutability mutability) {
        this.mutability = mutability;
    }

    public SCIMDefinitions.Returned getReturned() { return returned; }

    public void setReturned(SCIMDefinitions.Returned returned) {
        this.returned = returned;
    }

    public SCIMDefinitions.Uniqueness getUniqueness() {
        return uniqueness;
    }

    public void setUniqueness(SCIMDefinitions.Uniqueness uniqueness) {
        this.uniqueness = uniqueness;
    }

}
