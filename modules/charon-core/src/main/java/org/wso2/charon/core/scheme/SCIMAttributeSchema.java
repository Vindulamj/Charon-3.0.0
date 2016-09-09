package org.wso2.charon.core.scheme;

import java.util.List;
import java.util.Map;

/**
 * This defines the attributes schema as in SCIM Spec.
 */

public class SCIMAttributeSchema implements AttributeSchema {

    //name of the attribute
    private String name;
    //data type of the attribute
    private SCIMDefinitions.DataType type;
    //Boolean value indicating the attribute's plurality.
    private Boolean multiValued;
    //The attribute's human readable description
    private String description;
    //A Boolean value that specifies if the attribute is required
    private Boolean required;
    //A Boolean value that specifies if the String attribute is case sensitive
    private Boolean caseExact;
    //A SCIM defined value that specifies if the attribute's mutability.
    private SCIMDefinitions.Mutability mutability;
    //A SCIM defined value that specifies when the attribute's value need to be returned.
    private SCIMDefinitions.Returned returned;
    //A SCIM defined value that specifies the uniqueness level of an attribute.
    private SCIMDefinitions.Uniqueness uniqueness;
    //A list specifying the contained attributes. OPTIONAL.
    private List<SCIMAttributeSchema> subAttributes;
    //A collection of suggested canonical values that MAY be used -OPTIONAL
    private List<String> canonicalValues;
    //A multi-valued array of JSON strings that indicate the SCIM resource types that may be referenced
    //only applicable for attributes that are of type "reference"
    private Map<String, Object> referenceTypes;

    @Override
    public String getName() { return name; }

    @Override
    public void setName(String name) { this.name=name; }

    @Override
    public SCIMDefinitions.DataType getType() { return type; }

    @Override
    public void setType(SCIMDefinitions.DataType type) { this.type=type; }

    @Override
    public boolean getMultiValued() { return multiValued; }

    @Override
    public void setMultiValued(boolean isMultiValued) { this.multiValued=isMultiValued; }

    @Override
    public String getDescription() { return description; }

    @Override
    public void setDescription(String description) { this.description=description; }

    @Override
    public boolean getRequired() { return required; }

    @Override
    public void setRequired(boolean isRequired) { this.required=isRequired; }

    @Override
    public boolean getCaseExact() { return caseExact; }

    @Override
    public void setCaseExact(boolean isCaseExact) { this.caseExact=isCaseExact; }

    @Override
    public SCIMDefinitions.Mutability getMutability() { return mutability; }

    @Override
    public void setMutability(SCIMDefinitions.Mutability mutability) { this.mutability=mutability; }

    @Override
    public SCIMDefinitions.Returned getReturned() { return returned; }

    @Override
    public void setReturned(SCIMDefinitions.Returned returned) { this.returned=returned; }

    @Override
    public SCIMDefinitions.Uniqueness getUniqueness() { return uniqueness; }

    @Override
    public void setUniqueness(SCIMDefinitions.Uniqueness uniqueness) { this.uniqueness=uniqueness; }

    public List<SCIMAttributeSchema> getSubAttributes() { return subAttributes ;}

    public void setSubAttributes(List<SCIMAttributeSchema> subAttributes) { this.subAttributes = subAttributes; }

    public List<String> getCanonicalValues() { return canonicalValues; }

    public void setCanonicalValues(List<String> canonicalValues) { this.canonicalValues = canonicalValues; }

    public Map<String, Object> getReferenceTypes() { return referenceTypes; }

    public void setReferenceTypes(Map<String, Object> referenceTypes) { this.referenceTypes = referenceTypes; }
}