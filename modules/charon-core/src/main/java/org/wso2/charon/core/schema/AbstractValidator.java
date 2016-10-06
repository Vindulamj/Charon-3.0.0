package org.wso2.charon.core.schema;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.charon.core.attributes.*;
import org.wso2.charon.core.exceptions.BadRequestException;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.objects.AbstractSCIMObject;
import org.wso2.charon.core.protocol.ResponseCodeConstants;
import org.wso2.charon.core.utils.CopyUtil;

import java.util.*;

public abstract class AbstractValidator {

    private static Log logger= LogFactory.getLog(AbstractValidator.class);

    /**
     * Validate SCIMObject for required attributes given the object and the corresponding schema.
     *
     * @param scimObject
     * @param resourceSchema
     */
    public static void validateSCIMObjectForRequiredAttributes(AbstractSCIMObject scimObject,
                                                               ResourceTypeSchema resourceSchema)
            throws BadRequestException, CharonException {
        //get attributes from schema.
        List<AttributeSchema> attributeSchemaList = resourceSchema.getAttributesList();
        //get attribute list from scim object.
        Map<String, Attribute> attributeList = scimObject.getAttributeList();
        for (AttributeSchema attributeSchema : attributeSchemaList) {
            //check for required attributes.
            if (attributeSchema.getRequired()) {
                if (!attributeList.containsKey(attributeSchema.getName())) {
                    String error = "Required attribute " + attributeSchema.getName() + " is missing in the SCIM Object.";
                    throw new BadRequestException(error,ResponseCodeConstants.INVALID_VALUE);
                }
            }
            //check for required sub attributes.
            AbstractAttribute attribute = (AbstractAttribute) attributeList.get(attributeSchema.getName());
            if (attribute != null) {
                List<SCIMAttributeSchema> subAttributesSchemaList =
                        ((SCIMAttributeSchema) attributeSchema).getSubAttributeSchemas();

                if (subAttributesSchemaList != null) {
                    for (SCIMAttributeSchema subAttributeSchema : subAttributesSchemaList) {
                        if (subAttributeSchema.getRequired()) {

                            if (attribute instanceof ComplexAttribute) {
                                if (attribute.getSubAttribute(subAttributeSchema.getName()) == null) {
                                    String error = "Required sub attribute: " + subAttributeSchema.getName()
                                            + " is missing in the SCIM Attribute: " + attribute.getName();
                                    throw new BadRequestException(error,ResponseCodeConstants.INVALID_VALUE);
                                }
                            } else if (attribute instanceof MultiValuedAttribute) {
                                List<Attribute> values =
                                        ((MultiValuedAttribute) attribute).getAttributeValues();
                                for (Attribute value : values) {
                                    if (value instanceof ComplexAttribute) {
                                        if (value.getSubAttribute(subAttributeSchema.getName()) == null) {
                                            String error = "Required sub attribute: " + subAttributeSchema.getName()
                                                    + ", is missing in the SCIM Attribute: " + attribute.getName();
                                            throw new BadRequestException(error,ResponseCodeConstants.INVALID_VALUE);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    /**
     * Validate SCIMObject for schema list
     *
     * @param scimObject
     * @param resourceSchema
     */
    public static void validateSchemaList(AbstractSCIMObject scimObject,
                                          SCIMResourceTypeSchema resourceSchema) throws CharonException {
        //get resource schema list
        List<String> resourceSchemaList = resourceSchema.getSchemasList();
        //get the scim object schema list
        List<String> objectSchemaList = scimObject.getSchemaList();
        for (String schema : resourceSchemaList) {
            //check for schema.
            if (!objectSchemaList.contains(schema)) {
                throw new CharonException("Not all schemas are set");
            }
        }
    }

    /**
     *Check for readonlyAttributes and remove them if they have been modified. - (create method)
     *
     * @param scimObject
     * @param resourceSchema
     * @throws CharonException
     */
    public static void removeAnyReadOnlyAttributes(AbstractSCIMObject scimObject,
                                                   SCIMResourceTypeSchema resourceSchema) throws CharonException {
        //No need to check for immutable as immutable attributes can be defined at resource creation

        //get attributes from schema.
        List<AttributeSchema> attributeSchemaList = resourceSchema.getAttributesList();
        //get attribute list from scim object.
        Map<String, Attribute> attributeList = scimObject.getAttributeList();
        for (AttributeSchema attributeSchema : attributeSchemaList) {
            //check for read-only attributes.
            if (attributeSchema.getMutability()==SCIMDefinitions.Mutability.READ_ONLY) {
                if (attributeList.containsKey(attributeSchema.getName())) {
                    String error = "Read only attribute: " + attributeSchema.getName() +
                            " is set from consumer in the SCIM Object. " + "Removing it.";
                    logger.debug(error);
                    scimObject.deleteAttribute(attributeSchema.getName());
                }
            }
            //check for readonly sub attributes.
            AbstractAttribute attribute = (AbstractAttribute) attributeList.get(attributeSchema.getName());
            if (attribute != null) {
                List<SCIMAttributeSchema> subAttributesSchemaList =
                        ((SCIMAttributeSchema) attributeSchema).getSubAttributeSchemas();

                if (subAttributesSchemaList != null && !subAttributesSchemaList.isEmpty()) {
                    for (SCIMAttributeSchema subAttributeSchema : subAttributesSchemaList) {
                        if (subAttributeSchema.getMutability()==SCIMDefinitions.Mutability.READ_ONLY) {
                            if (attribute instanceof ComplexAttribute) {
                                if (attribute.getSubAttribute(subAttributeSchema.getName()) != null) {
                                    String error = "Readonly sub attribute: " + subAttributeSchema.getName()
                                            + " is set in the SCIM Attribute: " + attribute.getName() +
                                            ". Removing it.";
                                    ((ComplexAttribute) attribute).removeSubAttribute(subAttributeSchema.getName());
                                }
                            } else if (attribute instanceof MultiValuedAttribute) {
                                List<Attribute> values =
                                        ((MultiValuedAttribute) attribute).getAttributeValues();
                                for (Attribute value : values) {
                                    if (value instanceof ComplexAttribute) {
                                        if (value.getSubAttribute(subAttributeSchema.getName()) != null) {
                                            String error = "Readonly sub attribute: " + subAttributeSchema.getName()
                                                    + " is set in the SCIM Attribute: " + attribute.getName() +
                                                    ". Removing it.";
                                            ((ComplexAttribute) value).removeSubAttribute(subAttributeSchema.getName());

                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }

    }

    /**
     * This method is to remove any defined and requested attributes and include requested attributes.
     *
     * @param scimObject
     * @param requestedAttributes
     * @param requestedExcludingAttributes
     */
    public static void removeAttributesOnReturn(AbstractSCIMObject scimObject, String requestedAttributes,
                                                String requestedExcludingAttributes) {
        List<String> requestedAttributesList = null;
        List<String> requestedExcludingAttributesList = null;

        if(requestedAttributes!=null ){
            //make a list from the comma separated requestedAttributes
            requestedAttributesList = Arrays.asList(requestedAttributes.split(","));
        }
        if(requestedExcludingAttributes!=null){
            //make a list from the comma separated requestedExcludingAttributes
            requestedExcludingAttributesList = Arrays.asList(requestedExcludingAttributes.split(","));
        }
        Map<String, Attribute> attributeList = scimObject.getAttributeList();
        ArrayList<Attribute> attributeTemporyList= new ArrayList<Attribute>();
        for (Attribute attribute : attributeList.values()) {
            attributeTemporyList.add(attribute);
        }
        for(Attribute attribute : attributeTemporyList){
            //check for never/request attributes.
            if (attribute.getReturned().equals(SCIMDefinitions.Returned.NEVER)) {
                scimObject.deleteAttribute(attribute.getName());
            }
            //if the returned property is request, need to check whether is it specifically requested by the user.
            // If so return it.
            if(requestedAttributes ==null && requestedExcludingAttributes == null){
                if (attribute.getReturned().equals(SCIMDefinitions.Returned.REQUEST)){
                    scimObject.deleteAttribute(attribute.getName());
                }
            }
            else{
                //A request should only contains either attributes or exclude attribute params. Not both
                if(requestedAttributes !=null){
                    //if attributes are set, delete all the request and default attributes
                    //and add only the requested attributes
                    if ((attribute.getReturned().equals(SCIMDefinitions.Returned.DEFAULT)
                            || attribute.getReturned().equals(SCIMDefinitions.Returned.REQUEST))
                            && (!requestedAttributesList.contains(attribute.getName())
                            && !isSubAttributeExistsInList(requestedAttributesList,attribute))){
                        scimObject.deleteAttribute(attribute.getName());
                    }
                }
                else if(requestedExcludingAttributes !=null){
                    //removing attributes which has returned as request. This is because no request is made
                    if (attribute.getReturned().equals(SCIMDefinitions.Returned.REQUEST)) {
                        scimObject.deleteAttribute(attribute.getName());
                    }
                    //if exclude attribute is set, set of exclude attributes need to be
                    // removed from the default set of attributes
                    if ((attribute.getReturned().equals(SCIMDefinitions.Returned.DEFAULT))
                            && requestedExcludingAttributesList.contains(attribute.getName())){
                        scimObject.deleteAttribute(attribute.getName());
                    }
                }
            }
            // If the Returned type ALWAYS : no need to check and it will be not affected by
            // requestedExcludingAttributes parameter

            //check the same for sub attributes
            if(attribute.getType().equals(SCIMDefinitions.DataType.COMPLEX)){
                if(attribute.getMultiValued()){
                    List<Attribute> valuesList = ((MultiValuedAttribute)attribute).getAttributeValues();

                    for (Attribute subAttribute : valuesList) {
                        Map<String,Attribute> valuesSubAttributeList=((ComplexAttribute)subAttribute).getSubAttributesList();
                        ArrayList<Attribute> valuesSubAttributeTemporyList= new ArrayList<Attribute>();
                        //as we are deleting the attributes form the list, list size will change,
                        //hence need to traverse on a copy
                        for (Attribute subSimpleAttribute : valuesSubAttributeList.values()) {
                            valuesSubAttributeTemporyList.add(subSimpleAttribute);
                        }
                        for(Attribute subSimpleAttribute : valuesSubAttributeTemporyList){
                            removeValuesSubAttributeOnReturn(subSimpleAttribute, subAttribute, attribute,
                                    requestedAttributes, requestedExcludingAttributes, requestedAttributesList,
                                    requestedExcludingAttributesList, scimObject);
                        }
                    }
                }
                else{
                    Map<String, Attribute> subAttributeList = ((ComplexAttribute)attribute).getSubAttributesList();
                    ArrayList<Attribute> subAttributeTemporyList= new ArrayList<Attribute>();
                    for (Attribute subAttribute : subAttributeList.values()) {
                        subAttributeTemporyList.add(subAttribute);
                    }
                    for(Attribute subAttribute : subAttributeTemporyList){
                        removeSubAttributesOnReturn(subAttribute, attribute, requestedAttributes, requestedExcludingAttributes,
                                requestedAttributesList, requestedExcludingAttributesList,scimObject);
                    }
                }
            }
        }
    }

    /**
     * This method is to remove any defined and requested sub attributes and include requested sub attributes
     * from complex attributes.
     *
     * @param subAttribute
     * @param attribute
     * @param requestedAttributes
     * @param requestedExcludingAttributes
     * @param requestedAttributesList
     * @param requestedExcludingAttributesList
     * @param scimObject
     */

    private static void removeSubAttributesOnReturn(Attribute subAttribute, Attribute attribute, String requestedAttributes,
                                                    String requestedExcludingAttributes, List<String> requestedAttributesList,
                                                    List<String> requestedExcludingAttributesList, AbstractSCIMObject scimObject){
        //check for never/request attributes.
        if (subAttribute.getReturned().equals(SCIMDefinitions.Returned.NEVER)) {
            scimObject.deleteSubAttribute(attribute.getName(),subAttribute.getName());
        }
        //if the returned property is request, need to check whether is it specifically requested by the user.
        // If so return it.
        if(requestedAttributes ==null && requestedExcludingAttributes == null){
            if (subAttribute.getReturned().equals(SCIMDefinitions.Returned.REQUEST)){
                scimObject.deleteSubAttribute(attribute.getName(),subAttribute.getName());
            }
        }
        else{
            //A request should only contains either attributes or exclude attribute params. Not the both
            if(requestedAttributes !=null){
                //if attributes are set, delete all the request and default attributes
                // and add only the requested attributes
                if ((subAttribute.getReturned().equals(SCIMDefinitions.Returned.DEFAULT)
                        || subAttribute.getReturned().equals(SCIMDefinitions.Returned.REQUEST))
                        && (!requestedAttributesList.contains(
                        attribute.getName()+"."+subAttribute.getName()) &&
                        !requestedAttributesList.contains(attribute.getName()))){
                    scimObject.deleteSubAttribute(attribute.getName(),subAttribute.getName());
                }
            }
            else if(requestedExcludingAttributes !=null){
                //removing attributes which has returned as request. This is because no request is made
                if (subAttribute.getReturned().equals(SCIMDefinitions.Returned.REQUEST)) {
                    scimObject.deleteSubAttribute(attribute.getName(),subAttribute.getName());
                }
                //if exclude attribute is set, set of exclude attributes need to be
                // removed from the default set of attributes
                if ((subAttribute.getReturned().equals(SCIMDefinitions.Returned.DEFAULT))
                        && requestedExcludingAttributesList.contains(
                        attribute.getName()+"."+subAttribute.getName())){
                    scimObject.deleteSubAttribute(attribute.getName(),subAttribute.getName());
                }
            }
        }
    }

    /**
     * This method is to remove any defined and requested sub attributes and include requested sub attributes
     * from multivalued attributes
     *
     * @param subSimpleAttribute
     * @param subAttribute
     * @param attribute
     * @param requestedAttributes
     * @param requestedExcludingAttributes
     * @param requestedAttributesList
     * @param requestedExcludingAttributesList
     * @param scimObject
     */
    private static void removeValuesSubAttributeOnReturn(Attribute subSimpleAttribute, Attribute subAttribute,
                                                         Attribute attribute, String requestedAttributes,
                                                         String requestedExcludingAttributes,
                                                         List<String> requestedAttributesList,
                                                         List<String> requestedExcludingAttributesList,
                                                         AbstractSCIMObject scimObject){

        if(subSimpleAttribute.getReturned().equals(SCIMDefinitions.Returned.NEVER)){
            scimObject.deleteValuesSubAttribute(attribute.getName(),
                    subAttribute.getName(),subSimpleAttribute.getName());
        }
        if(requestedAttributes ==null && requestedExcludingAttributes == null){
            if (attribute.getReturned().equals(SCIMDefinitions.Returned.REQUEST)){
                scimObject.deleteValuesSubAttribute(attribute.getName(),
                        subAttribute.getName(), subSimpleAttribute.getName());
            }
        }
        else{
            //A request should only contains either attributes or exclude attribute params. Not the both
            if(requestedAttributes !=null){
                //if attributes are set, delete all the request and default attributes
                // and add only the requested attributes
                if ((subSimpleAttribute.getReturned().equals(SCIMDefinitions.Returned.DEFAULT)
                        || subSimpleAttribute.getReturned().equals(SCIMDefinitions.Returned.REQUEST))
                        && (!requestedAttributesList.contains(
                        attribute.getName()+"."+subSimpleAttribute.getName()) &&
                        !requestedAttributesList.contains(attribute.getName()))){
                    scimObject.deleteValuesSubAttribute(attribute.getName(),
                            subAttribute.getName(), subSimpleAttribute.getName());
                }
            }
            else if(requestedExcludingAttributes !=null){
                //removing attributes which has returned as request. This is because no request is made
                if (subSimpleAttribute.getReturned().equals(SCIMDefinitions.Returned.REQUEST)) {
                    scimObject.deleteValuesSubAttribute(attribute.getName(),
                            subAttribute.getName(), subSimpleAttribute.getName());
                }
                //if exclude attribute is set, set of exclude attributes need to be
                // removed from the default set of attributes
                if ((subSimpleAttribute.getReturned().equals(SCIMDefinitions.Returned.DEFAULT))
                        && requestedExcludingAttributesList.contains(
                        attribute.getName()+"."+subSimpleAttribute.getName())){
                    scimObject.deleteValuesSubAttribute(attribute.getName(),
                            subAttribute.getName(),subSimpleAttribute.getName());
                }
            }
        }


    }

    /**
     * This checks whether, within the 'requestedAttributes', is there a sub attribute of the 'attribute'.
     * If so we should not delete the 'attribute'
     *
     * @param requestedAttributes
     * @param attribute
     * @return boolean
     */
    private static boolean isSubAttributeExistsInList(List<String> requestedAttributes, Attribute attribute) {
        ArrayList<Attribute> subAttributes = null;
        if(attribute instanceof MultiValuedAttribute){
            subAttributes = (ArrayList<Attribute>)
                    ((MultiValuedAttribute)attribute).getAttributeValues();
            if(subAttributes != null){
                for(Attribute subAttribute : subAttributes){
                    ArrayList<Attribute> subSimpleAttributes =new ArrayList<Attribute>((
                            (ComplexAttribute)subAttribute).getSubAttributesList().values());
                    for(Attribute subSimpleAttribute : subSimpleAttributes){
                        if(requestedAttributes.contains(attribute.getName()+"."+subSimpleAttribute.getName())){
                            return true;
                        }
                    }

                }
            }

        }
        else if(attribute instanceof ComplexAttribute){
            //complex attributes have sub attribute map, hence need conversion to arraylist
            subAttributes = new ArrayList<Attribute>
                    (((HashMap)(((ComplexAttribute)attribute).getSubAttributesList())).values());
            if(subAttributes != null){
                for(Attribute subAttribute : subAttributes){
                    if(requestedAttributes.contains(attribute.getName()+"."+subAttribute.getName())){
                        return true;
                    }
                }
            }
            else{
                return false;
            }
        }
        return false;
    }

    /**
     * check for read only and immutable attributes which has been modified on update request
     *
     * @param oldObject
     * @param newObject
     * @param resourceSchema
     * @return
     * @throws BadRequestException
     * @throws CharonException
     */
    protected static AbstractSCIMObject checkIfReadOnlyAndImmutableAttributesModified(
            AbstractSCIMObject oldObject, AbstractSCIMObject newObject, SCIMResourceTypeSchema resourceSchema)
            throws BadRequestException, CharonException {

        //get attributes from schema.
        List<AttributeSchema> attributeSchemaList = resourceSchema.getAttributesList();
        //get attribute list from old scim object.
        Map<String, Attribute> oldAttributeList = oldObject.getAttributeList();
        //get attribute list from new scim object.
        Map<String, Attribute> newAttributeList = newObject.getAttributeList();

        for (AttributeSchema attributeSchema : attributeSchemaList) {
            if(attributeSchema.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY)){
                if(newAttributeList.containsKey(attributeSchema.getName()) &&
                        oldAttributeList.containsKey(attributeSchema.getName())){
                    String error = "Read only attribute: " + attributeSchema.getName() +
                            " is set from consumer in the SCIM Object. " +
                            "Removing it and updating from previous value.";
                    logger.debug(error);
                    newObject.deleteAttribute(attributeSchema.getName());
                    newObject.setAttribute((Attribute)(CopyUtil.deepCopy(oldObject.getAttribute(attributeSchema.getName()))));
                }
                else if(newAttributeList.containsKey(attributeSchema.getName()) &&
                        !oldAttributeList.containsKey(attributeSchema.getName())){
                    String error = "Read only attribute: " + attributeSchema.getName() +
                            " is set from consumer in the SCIM Object. " + "Removing it.";
                    logger.debug(error);
                    newObject.deleteAttribute(attributeSchema.getName());
                }
                else if(!newAttributeList.containsKey(attributeSchema.getName()) &&
                        oldAttributeList.containsKey(attributeSchema.getName())){
                    newObject.setAttribute((Attribute)(CopyUtil.deepCopy(oldObject.getAttribute(attributeSchema.getName()))));
                }
            }
            else if(attributeSchema.getMutability().equals(SCIMDefinitions.Mutability.IMMUTABLE)){
                if(newAttributeList.containsKey(attributeSchema.getName()) &&
                        oldAttributeList.containsKey(attributeSchema.getName())){
                    checkForSameValues(oldAttributeList, newAttributeList, attributeSchema);

                }
                else if(!newAttributeList.containsKey(attributeSchema.getName()) &&
                        oldAttributeList.containsKey(attributeSchema.getName())){
                    newObject.setAttribute((Attribute)(CopyUtil.deepCopy(oldObject.getAttribute(attributeSchema.getName()))));
                }
            }

            //check for sub attributes.
            AbstractAttribute newAttribute = (AbstractAttribute) newAttributeList.get(attributeSchema.getName());
            AbstractAttribute oldAttribute = (AbstractAttribute) oldAttributeList.get(attributeSchema.getName());
            List<SCIMAttributeSchema> subAttributeSchemaList= attributeSchema.getSubAttributeSchemas();

            if(subAttributeSchemaList != null ){
                if(newAttribute !=null && oldAttribute != null){
                    if(attributeSchema.getMultiValued()){
                        //this is complex multivalued case
                        List<Attribute> newSubValuesList = ((MultiValuedAttribute)newAttribute).getAttributeValues();
                        List<Attribute> oldSubValuesList = ((MultiValuedAttribute)oldAttribute).getAttributeValues();

                        for(Attribute subValue : newSubValuesList){
                            if(isListContains((((ComplexAttribute)subValue).getName()),oldSubValuesList)){
                                checkForReadOnlyAndImmutableInComplexAttributes(subValue,getRelatedSubValue(subValue,oldSubValuesList),subAttributeSchemaList);
                            }
                            else{
                                if(attributeSchema.getMutability().equals(SCIMDefinitions.Mutability.IMMUTABLE)){
                                    throw new BadRequestException(ResponseCodeConstants.MUTABILITY);
                                }
                            }
                        }
                    }
                    else {
                        //A complex attribute itself can not be immutable if it's sub variables are not immutable
                        checkForReadOnlyAndImmutableInComplexAttributes(newAttribute, oldAttribute, subAttributeSchemaList);
                    }
                }
                else if(newAttribute ==null && oldAttribute != null) {
                    if (attributeSchema.getMultiValued()) {
                        List<Attribute> oldSubValuesList = ((MultiValuedAttribute) oldAttribute).getAttributeValues();
                        Attribute clonedMultiValuedAttribute=(Attribute) CopyUtil.deepCopy(oldAttribute);
                        clonedMultiValuedAttribute.deleteSubAttributes();

                        for (Attribute subValue : oldSubValuesList) {
                            Attribute clonedSubValue=(Attribute) CopyUtil.deepCopy(subValue);
                            clonedSubValue.deleteSubAttributes();

                            for (AttributeSchema subAttributeSchema : subAttributeSchemaList) {
                                if (subAttributeSchema.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY)
                                        || subAttributeSchema.getMutability().equals(SCIMDefinitions.Mutability.IMMUTABLE)) {
                                    if(((ComplexAttribute)subValue).isSubAttributeExist(subAttributeSchema.getName())){
                                        Attribute clonedSubValuesAttribute=(Attribute) CopyUtil.deepCopy(
                                                ((ComplexAttribute)subValue).getSubAttribute(subAttributeSchema.getName()));
                                        ((ComplexAttribute)clonedSubValue).setSubAttribute(clonedSubValuesAttribute);
                                    }
                                }
                            }
                            ((MultiValuedAttribute)(clonedMultiValuedAttribute)).setAttributeValue(clonedSubValue);
                        }
                    }
                    else {
                        Map<String, Attribute> oldSubAttributeList = ((ComplexAttribute) (oldAttribute)).getSubAttributesList();
                        Attribute clonedAttribute=(Attribute) CopyUtil.deepCopy(oldAttribute);
                        clonedAttribute.deleteSubAttributes();
                        for (AttributeSchema subAttributeSchema : subAttributeSchemaList) {

                            if (subAttributeSchema.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY)
                                    || subAttributeSchema.getMutability().equals(SCIMDefinitions.Mutability.IMMUTABLE)) {
                                if (oldSubAttributeList.containsKey(subAttributeSchema.getName())) {
                                    ((ComplexAttribute)(clonedAttribute)).setSubAttribute(
                                            (Attribute) CopyUtil.deepCopy(oldSubAttributeList.get(subAttributeSchema.getName())));
                                }
                            }
                        }
                        newAttributeList.put(clonedAttribute.getName(),clonedAttribute);
                    }
                }
                else if(newAttribute !=null && oldAttribute == null){
                    if(attributeSchema.getMultiValued()) {
                        if (attributeSchema.getMultiValued()) {
                            List<Attribute> newSubValuesList = ((MultiValuedAttribute) newAttribute).getAttributeValues();

                            for (Attribute subValue : newSubValuesList) {
                                for (AttributeSchema subAttributeSchema : subAttributeSchemaList) {
                                    if (subAttributeSchema.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY)) {
                                        ((ComplexAttribute) (subValue)).removeSubAttribute(subAttributeSchema.getName());
                                    }
                                }
                            }
                        }
                    }
                    else{
                        //this is complex attribute case
                        Map<String,Attribute> newSubAttributeList= ((ComplexAttribute)(newAttribute)).getSubAttributesList();

                        for(AttributeSchema subAttributeSchema : subAttributeSchemaList) {

                            if (subAttributeSchema.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY)) {
                                if (newSubAttributeList.containsKey(subAttributeSchema.getName())){
                                    String error = "Read only attribute: " + subAttributeSchema.getName() +
                                            " is set from consumer in the SCIM Object. Removing it.";
                                    logger.debug(error);
                                    ((ComplexAttribute) newAttribute).removeSubAttribute(subAttributeSchema.getName());
                                }
                            }
                        }
                    }
                }
            }
        }
        return newObject;
    }

    /**
     * check whether the give attribute is in the given list
     *
     * @param attributeName
     * @param list
     * @return
     */
    private static boolean isListContains(String attributeName, List<Attribute> list){
        for(Attribute attribute :list){
            if(attribute.getName().equals(attributeName)){
                return true;
            }
        }
        return false;
    }

    /**
     * check for related sub value corresponding to the given sub value
     *
     * @param newSubValue
     * @param oldSubValuesList
     * @return
     */
    private static Attribute getRelatedSubValue(Attribute newSubValue, List<Attribute> oldSubValuesList){
        for(Attribute oldSubValue : oldSubValuesList){
            if(oldSubValue.getName().equals(newSubValue.getName())){
                return oldSubValue;
            }
        }
        return null;
    }

    /**
     * check for same values in a simple singular attributes or multivalued primitive type attributes
     *
     * @param oldAttributeList
     * @param newAttributeList
     * @param attributeSchema
     * @throws BadRequestException
     */
    private static void checkForSameValues(Map<String, Attribute> oldAttributeList, Map<String, Attribute> newAttributeList,
                                           AttributeSchema attributeSchema) throws BadRequestException {

        Attribute newTemporyAttribute = newAttributeList.get(attributeSchema.getName());
        Attribute oldTemporyAttribute = oldAttributeList.get(attributeSchema.getName());

        if(newTemporyAttribute instanceof SimpleAttribute){
            if(!((((SimpleAttribute) newTemporyAttribute).getValue()).equals(((SimpleAttribute) oldTemporyAttribute).getValue()))){
                throw new BadRequestException(ResponseCodeConstants.MUTABILITY);
            }
        }
        else if(newTemporyAttribute instanceof MultiValuedAttribute &&
                !attributeSchema.getType().equals(SCIMDefinitions.DataType.COMPLEX)){
            if(!checkListEquality(((MultiValuedAttribute)newTemporyAttribute).getAttributePrimitiveValues(),
                    ((MultiValuedAttribute)oldTemporyAttribute).getAttributePrimitiveValues())){
                throw new BadRequestException(ResponseCodeConstants.MUTABILITY);
            }

        }
    }

    /**
     * check whether the given two lists are equal from the content irrespective of the order
     * @param l1
     * @param l2
     * @return
     */
    private static boolean checkListEquality(List<Object> l1, List<Object> l2){
        final Set<Object> s1 = new HashSet(l1);
        final Set<Object> s2 = new HashSet(l2);

        return s1.equals(s2);
    }

    /**
     * check for read only and immutable attributes that has been modified in a complex type attribute
     *
     * @param newAttribute
     * @param oldAttribute
     * @param subAttributeSchemaList
     * @throws CharonException
     * @throws BadRequestException
     */
    private static void checkForReadOnlyAndImmutableInComplexAttributes(Attribute newAttribute, Attribute oldAttribute,
                                                                        List<SCIMAttributeSchema> subAttributeSchemaList
    ) throws CharonException, BadRequestException {
        //this is complex attribute case
        Map<String,Attribute> newSubAttributeList= ((ComplexAttribute)(newAttribute)).getSubAttributesList();
        Map<String,Attribute> oldSubAttributeList= ((ComplexAttribute)(oldAttribute)).getSubAttributesList();

        for(AttributeSchema subAttributeSchema : subAttributeSchemaList){

            if(subAttributeSchema.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY)){
                if(newSubAttributeList.containsKey(subAttributeSchema.getName()) &&
                        oldSubAttributeList.containsKey(subAttributeSchema.getName())){
                    String error = "Read only attribute: " + subAttributeSchema.getName() +
                            " is set from consumer in the SCIM Object. " +
                            "Removing it and updating from previous value.";
                    logger.debug(error);
                    ((ComplexAttribute)newAttribute).removeSubAttribute(subAttributeSchema.getName());
                    ((ComplexAttribute)newAttribute).setSubAttribute((Attribute)(CopyUtil.deepCopy(
                            (((ComplexAttribute)oldAttribute).getSubAttribute(subAttributeSchema.getName())))));
                }
                else if(newSubAttributeList.containsKey(subAttributeSchema.getName()) &&
                        !oldSubAttributeList.containsKey(subAttributeSchema.getName())){
                    String error = "Read only attribute: " + subAttributeSchema.getName() +
                            " is set from consumer in the SCIM Object. " + "Removing it.";
                    logger.debug(error);
                    ((ComplexAttribute)newAttribute).removeSubAttribute(subAttributeSchema.getName());
                }
                else if(!newSubAttributeList.containsKey(subAttributeSchema.getName()) &&
                        oldSubAttributeList.containsKey(subAttributeSchema.getName())){
                    ((ComplexAttribute)newAttribute).setSubAttribute((Attribute)(CopyUtil.deepCopy(
                            ((ComplexAttribute)oldAttribute).getSubAttribute(subAttributeSchema.getName()))));
                }
            }
            else if(subAttributeSchema.getMutability().equals(SCIMDefinitions.Mutability.IMMUTABLE)){

                if(newSubAttributeList.containsKey(subAttributeSchema.getName()) &&
                        oldSubAttributeList.containsKey(subAttributeSchema.getName())){
                    checkForSameValues(newSubAttributeList, oldSubAttributeList, subAttributeSchema);

                }
                else if(!newSubAttributeList.containsKey(subAttributeSchema.getName()) &&
                        oldSubAttributeList.containsKey(subAttributeSchema.getName())){
                    ((ComplexAttribute)newAttribute).setSubAttribute((Attribute)(CopyUtil.deepCopy(
                            ((ComplexAttribute)oldAttribute).getSubAttribute(subAttributeSchema.getName()))));
                }
            }

        }
    }

}
