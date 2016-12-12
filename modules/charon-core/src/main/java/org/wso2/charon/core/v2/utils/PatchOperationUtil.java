/*
 * Copyright (c) 2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.charon.core.v2.utils;

import org.wso2.charon.core.v2.attributes.Attribute;
import org.wso2.charon.core.v2.attributes.ComplexAttribute;
import org.wso2.charon.core.v2.attributes.MultiValuedAttribute;
import org.wso2.charon.core.v2.attributes.SimpleAttribute;
import org.wso2.charon.core.v2.encoder.JSONDecoder;
import org.wso2.charon.core.v2.exceptions.BadRequestException;
import org.wso2.charon.core.v2.exceptions.CharonException;
import org.wso2.charon.core.v2.exceptions.NotImplementedException;
import org.wso2.charon.core.v2.objects.AbstractSCIMObject;
import org.wso2.charon.core.v2.objects.User;
import org.wso2.charon.core.v2.protocol.ResponseCodeConstants;
import org.wso2.charon.core.v2.schema.*;
import org.wso2.charon.core.v2.utils.codeutils.ExpressionNode;
import org.wso2.charon.core.v2.utils.codeutils.PatchOperation;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This provides the methods on the PATCH operation of any resource type.
 */
public class PatchOperationUtil {

    /*
     * This method corresponds to the remove operation in patch requests.
     * @param operation
     * @param decoder
     * @param oldResource
     * @param copyOfOldResource
     * @return
     * @throws BadRequestException
     * @throws IOException
     * @throws NotImplementedException
     * @throws CharonException
     */
    public static AbstractSCIMObject doPatchRemove(PatchOperation operation, AbstractSCIMObject oldResource,
                                                   User copyOfOldResource, SCIMResourceTypeSchema schema)
            throws BadRequestException, NotImplementedException, CharonException {

        if (operation.getPath() == null) {
            throw new BadRequestException
                    ("No path value specified for remove operation", ResponseCodeConstants.NO_TARGET);
        }

        String path = operation.getPath();
        //split the path to extract the filter if present.
        String[] parts = path.split("[\\[\\]]");

        if (parts.length != 1) {
            //currently we only support simple filters here.
            String[] filterParts = parts[1].split(" ");

            ExpressionNode expressionNode = new ExpressionNode();
            expressionNode.setAttributeValue(filterParts[0]);
            expressionNode.setOperation(filterParts[1]);
            expressionNode.setValue(filterParts[2]);

            if (expressionNode.getOperation().equalsIgnoreCase((SCIMConstants.OperationalConstants.EQ).trim())) {

                doPatchRemoveWithFilters(parts, oldResource, expressionNode);
            } else {
                throw new NotImplementedException("Only Eq filter is supported");
            }
        } else {

            doPatchRemoveWithoutFilters(parts, oldResource);
        }
        //validate the updated object
        AbstractSCIMObject validatedResource =  ServerSideValidator.validateUpdatedSCIMObject
                (copyOfOldResource, oldResource, schema);

        return validatedResource;

    }

    /*
     * This is the patch remove operation when the path is specified with a filter in it.
     * @param parts
     * @param oldResource
     * @param expressionNode
     * @throws BadRequestException
     * @throws CharonException
     */
    private static void doPatchRemoveWithFilters(String[] parts,
                                                 AbstractSCIMObject oldResource, ExpressionNode expressionNode)
            throws BadRequestException, CharonException {

        if (parts.length == 3) {
            parts[0] = parts[0] + parts[2];
        }
        String[] attributeParts = parts[0].split("[\\.]");

        if (attributeParts.length == 1) {

            doPatchRemoveWithFiltersForLevelOne(oldResource, attributeParts, expressionNode);

        } else if (attributeParts.length == 2) {

            doPatchRemoveWithFiltersForLevelTwo(oldResource, attributeParts, expressionNode);

        } else if (attributeParts.length == 3) {

            doPatchRemoveWithFiltersForLevelThree(oldResource, attributeParts, expressionNode);
        }
    }

    /*
     *
     * @param oldResource
     * @param attributeParts
     * @param expressionNode
     * @return
     * @throws BadRequestException
     * @throws CharonException
     */
    private static AbstractSCIMObject doPatchRemoveWithFiltersForLevelThree(AbstractSCIMObject oldResource,
                                                                            String[] attributeParts,
                                                                            ExpressionNode expressionNode)
            throws BadRequestException, CharonException {

        Attribute attribute = oldResource.getAttribute(attributeParts[0]);
        if (attribute != null) {

            Attribute subAttribute = attribute.getSubAttribute(attributeParts[1]);

            if (subAttribute != null) {
                if (subAttribute.getMultiValued()) {

                    List<Attribute> subValues = ((MultiValuedAttribute) subAttribute).getAttributeValues();
                    if (subValues != null) {
                        for (Attribute subValue: subValues) {
                            Map<String, Attribute> subSubAttributes =
                                    ((ComplexAttribute) subValue).getSubAttributesList();
                            //this map is to avoid concurrent modification exception.
                            Map<String, Attribute> tempSubSubAttributes = (Map<String, Attribute>)
                                    CopyUtil.deepCopy(subSubAttributes);

                            for (Iterator<Attribute> iterator = tempSubSubAttributes.values().iterator();
                                 iterator.hasNext();) {
                                Attribute subSubAttribute = iterator.next();

                                if (subSubAttribute.getName().equals(expressionNode.getAttributeValue())) {

                                    Attribute removingAttribute = subSubAttributes.get(attributeParts[2]);
                                    if (removingAttribute == null) {
                                        throw new BadRequestException("No such sub attribute with the name : " +
                                                attributeParts[2] + " " + "within the attribute " +
                                                attributeParts[1], ResponseCodeConstants.INVALID_PATH);
                                    }
                                    if (removingAttribute.getMutability().equals
                                            (SCIMDefinitions.Mutability.READ_ONLY) ||
                                            removingAttribute.getRequired().equals(true)) {
                                        throw new BadRequestException
                                                ("Can not remove a required attribute or a read-only attribute",
                                                        ResponseCodeConstants.MUTABILITY);
                                    } else {

                                        ((ComplexAttribute) subValue).removeSubAttribute
                                                (removingAttribute.getName());
                                    }
                                }
                            }
                        }
                        if (subValues.size() == 0) {
                            //if the attribute has no values, make it unassigned
                            ((ComplexAttribute) attribute).removeSubAttribute(subAttribute.getName());
                        }
                    }
                } else {
                    throw new BadRequestException("Attribute : " + attributeParts[1] + " " +
                            "is not a multivalued attribute.", ResponseCodeConstants.INVALID_PATH);
                }

            } else {
                throw new BadRequestException("No such sub attribute with the name : " + attributeParts[1] + " " +
                        "within the attribute " + attributeParts[0], ResponseCodeConstants.INVALID_PATH);
            }

        } else {
            throw new BadRequestException("No such attribute with the name : " + attributeParts[0] + " " +
                    "in the current resource", ResponseCodeConstants.INVALID_PATH);
        }
        return oldResource;
    }

    /*
     *
     * @param oldResource
     * @param attributeParts
     * @param expressionNode
     * @return
     * @throws BadRequestException
     * @throws CharonException
     */
    private static AbstractSCIMObject doPatchRemoveWithFiltersForLevelTwo(AbstractSCIMObject oldResource,
                                                                          String[] attributeParts,
                                                                          ExpressionNode expressionNode)
            throws BadRequestException, CharonException {

        Attribute attribute = oldResource.getAttribute(attributeParts[0]);
        if (attribute != null) {

            if (attribute.getMultiValued()) {

                List<Attribute> subValues = ((MultiValuedAttribute) attribute).getAttributeValues();
                if (subValues != null) {
                    for (Attribute subValue: subValues) {
                        Map<String, Attribute> subAttributes = ((ComplexAttribute) subValue).getSubAttributesList();
                        //this map is to avoid concurrent modification exception.
                        Map<String, Attribute> tempSubAttributes = (Map<String, Attribute>)
                                CopyUtil.deepCopy(subAttributes);

                        for (Iterator<Attribute> iterator = tempSubAttributes.values().iterator();
                             iterator.hasNext();) {
                            Attribute subAttribute = iterator.next();

                            if (subAttribute.getName().equals(expressionNode.getAttributeValue())) {

                                Attribute removingAttribute = subAttributes.get(attributeParts[1]);
                                if (removingAttribute == null) {
                                    throw new BadRequestException
                                            ("No such sub attribute with the name : " + attributeParts[1] + " " +
                                                    "within the attribute " + attributeParts[0],
                                                    ResponseCodeConstants.INVALID_PATH);
                                }
                                if (removingAttribute.getMutability().
                                        equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                                        removingAttribute.getRequired().equals(true)) {
                                    throw new BadRequestException
                                            ("Can not remove a required attribute or a read-only attribute",
                                                    ResponseCodeConstants.MUTABILITY);
                                } else {

                                    ((ComplexAttribute) subValue).removeSubAttribute(removingAttribute.getName());
                                }
                            }
                        }
                    }
                    if (subValues.size() == 0) {
                        //if the attribute has no values, make it unassigned
                        oldResource.deleteAttribute(attribute.getName());
                    }
                }
            } else if (attribute.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                //this is only valid for extension
                Attribute subAttribute  = attribute.getSubAttribute(attributeParts[1]);
                if (subAttribute == null) {
                    throw new BadRequestException("No such sub attribute with the name : "
                            + attributeParts[1] + " " + "within the attribute " + attributeParts[0],
                            ResponseCodeConstants.INVALID_PATH);
                }

                List<Attribute> subValues = ((MultiValuedAttribute) (subAttribute)).getAttributeValues();
                if (subValues != null) {
                    for (Iterator<Attribute> subValueIterator = subValues.iterator(); subValueIterator.hasNext();) {
                        Attribute subValue = subValueIterator.next();

                        Map<String, Attribute> subValuesSubAttribute =
                                ((ComplexAttribute) subValue).getSubAttributesList();
                        for (Iterator<Attribute> iterator =
                             subValuesSubAttribute.values().iterator(); iterator.hasNext();) {

                            Attribute subSubAttribute = iterator.next();
                            if (subSubAttribute.getName().equals(expressionNode.getAttributeValue())) {
                                if (((SimpleAttribute) (subSubAttribute)).getValue().equals
                                        (expressionNode.getValue())) {
                                    if (subValue.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                                            subValue.getRequired().equals(true)) {

                                        throw new BadRequestException
                                                ("Can not remove a required attribute or a read-only attribute",
                                                        ResponseCodeConstants.MUTABILITY);
                                    } else {
                                        iterator.remove();
                                    }
                                }
                            }
                        }
                    }
                    //if the attribute has no values, make it unassigned
                    if (((MultiValuedAttribute) (subAttribute)).getAttributeValues().size() == 0) {
                        ((ComplexAttribute) attribute).removeSubAttribute(subAttribute.getName());
                    }
                }
            } else {
                throw new BadRequestException("Attribute : " + expressionNode.getAttributeValue() + " " +
                        "is not a multivalued attribute.", ResponseCodeConstants.INVALID_PATH);
            }
        } else {
            throw new BadRequestException("No such attribute with the name : " + attributeParts[0] + " " +
                    "in the current resource", ResponseCodeConstants.INVALID_PATH);
        }
        return oldResource;
    }


    /*
     *
     * @param oldResource
     * @param attributeParts
     * @param expressionNode
     * @return
     * @throws BadRequestException
     * @throws CharonException
     */
    private static AbstractSCIMObject doPatchRemoveWithFiltersForLevelOne(AbstractSCIMObject oldResource,
                                                                          String[] attributeParts,
                                                                          ExpressionNode expressionNode)
            throws BadRequestException, CharonException {

        Attribute attribute = oldResource.getAttribute(attributeParts[0]);

        if (attribute != null) {
            if (!attribute.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                //this is for paths value as 'attributeX[attributeX EQ yyy]'
                //this is multivalued primitive case
                if (attribute == null) {
                    throw new BadRequestException("No such attribute with the name : " +
                            expressionNode.getAttributeValue() + " " +
                            "in the current resource", ResponseCodeConstants.INVALID_VALUE);
                } else {
                    if (attribute.getMultiValued()) {

                        List<Object> valuesList  = ((MultiValuedAttribute)
                                (attribute)).getAttributePrimitiveValues();
                        for (Iterator<Object> iterator =
                             valuesList.iterator(); iterator.hasNext();) {
                            Object item = iterator.next();
                            //we only support "EQ" filter
                            if (item.equals(expressionNode.getValue())) {
                                if (attribute.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                                        attribute.getRequired().equals(true)) {
                                    throw new BadRequestException
                                            ("Can not remove a required attribute or a read-only attribute",
                                                    ResponseCodeConstants.MUTABILITY);
                                } else {
                                    iterator.remove();
                                }
                            }
                        }
                        //if the attribute has no values, make it unassigned
                        if (((MultiValuedAttribute) (attribute)).getAttributePrimitiveValues().size() == 0) {
                            oldResource.deleteAttribute(attribute.getName());
                        }

                    } else {
                        throw new BadRequestException("Attribute : " + expressionNode.getAttributeValue() + " " +
                                "is not a multivalued attribute.", ResponseCodeConstants.INVALID_PATH);
                    }
                }

            } else {
                if (attribute.getMultiValued()) {
                    //this is for paths value as 'emails[value EQ vindula@wso2.com]'
                    //this is multivalued complex case

                    List<Attribute> subValues = ((MultiValuedAttribute) (attribute)).getAttributeValues();
                    if (subValues != null) {
                        for (Iterator<Attribute> subValueIterator = subValues.iterator();
                             subValueIterator.hasNext();) {
                            Attribute subValue = subValueIterator.next();

                            Map<String, Attribute> subValuesSubAttribute =
                                    ((ComplexAttribute) subValue).getSubAttributesList();
                            for (Iterator<Attribute> iterator =
                                 subValuesSubAttribute.values().iterator(); iterator.hasNext();) {

                                Attribute subAttribute = iterator.next();
                                if (subAttribute.getName().equals(expressionNode.getAttributeValue())) {
                                    if (((SimpleAttribute)
                                            (subAttribute)).getValue().equals(expressionNode.getValue())) {
                                        if (subValue.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                                                subValue.getRequired().equals(true)) {
                                            throw new BadRequestException
                                                    ("Can not remove a required attribute or a read-only attribute",
                                                            ResponseCodeConstants.MUTABILITY);
                                        } else {
                                            subValueIterator.remove();
                                        }
                                    }
                                }
                            }
                        }
                        //if the attribute has no values, make it unassigned
                        if (((MultiValuedAttribute) (attribute)).getAttributeValues().size() == 0) {
                            oldResource.deleteAttribute(attribute.getName());
                        }
                    }
                } else {
                    //this is complex attribute which has multi valued primitive sub attribute.
                    Attribute subAttribute = attribute.getSubAttribute(expressionNode.getAttributeValue());
                    if (subAttribute != null) {

                        if (subAttribute.getMultiValued()) {
                            List<Object> valuesList  = ((MultiValuedAttribute)
                                    (subAttribute)).getAttributePrimitiveValues();
                            for (Iterator<Object> iterator =
                                 valuesList.iterator(); iterator.hasNext();) {
                                Object item = iterator.next();
                                //we only support "EQ" filter
                                if (item.equals(expressionNode.getValue())) {
                                    if (subAttribute.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                                            subAttribute.getRequired().equals(true)) {
                                        throw new BadRequestException
                                                ("Can not remove a required attribute or a read-only attribute",
                                                        ResponseCodeConstants.MUTABILITY);
                                    } else {
                                        iterator.remove();
                                    }
                                }
                            }
                            //if the subAttribute has no values, make it unassigned
                            if (((MultiValuedAttribute) (subAttribute)).getAttributePrimitiveValues().size() == 0) {
                                ((ComplexAttribute) (attribute)).removeSubAttribute(subAttribute.getName());
                            }

                        } else {
                            throw new BadRequestException("Sub attribute : " +
                                    expressionNode.getAttributeValue() + " " +
                                    "is not a multivalued attribute.", ResponseCodeConstants.INVALID_PATH);
                        }

                    } else {
                        throw new BadRequestException("No sub attribute with the name : " +
                                expressionNode.getAttributeValue() + " " +
                                "in the attribute : " + attributeParts[0], ResponseCodeConstants.INVALID_PATH);
                    }
                }
            }
        } else {
            throw new BadRequestException("No such attribute with the name : " + attributeParts[0] + " " +
                    "in the current resource", ResponseCodeConstants.INVALID_PATH);
        }
        return oldResource;
    }

    /*
     * This is the patch remove operation when the path is specified without a filter in it.
     * @param parts
     * @param oldResource
     * @return
     * @throws BadRequestException
     * @throws CharonException
     */
    private static AbstractSCIMObject doPatchRemoveWithoutFilters
            (String[] parts, AbstractSCIMObject oldResource) throws BadRequestException, CharonException {

        String[] attributeParts = parts[0].split("[\\.]");
        if (attributeParts.length == 1) {

            Attribute attribute = oldResource.getAttribute(parts[0]);

            if (attribute != null) {
                if (attribute.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                        attribute.getRequired().equals(true)) {
                    throw new BadRequestException("Can not remove a required attribute or a read-only attribute",
                            ResponseCodeConstants.MUTABILITY);
                } else {
                    String attributeName = attribute.getName();
                    oldResource.deleteAttribute(attributeName);
                }
            } else {
                throw new BadRequestException("No such attribute with the name : " + attributeParts[0] + " " +
                        "in the current resource", ResponseCodeConstants.INVALID_PATH);
            }

        } else {
            Attribute attribute = oldResource.getAttribute(attributeParts[0]);
            if (attribute != null) {
                if (attribute.getMultiValued()) {
                    //this is multivalued complex case
                    List<Attribute> subValuesList = ((MultiValuedAttribute) attribute).getAttributeValues();

                    if (subValuesList != null) {

                        for (Attribute subValue : subValuesList) {
                            Map<String, Attribute> subSubAttributeList =
                                    ((ComplexAttribute) subValue).getSubAttributesList();
                            //need to remove attributes while iterating through the list.
                            for (Iterator<Attribute> iterator =
                                 subSubAttributeList.values().iterator(); iterator.hasNext();) {
                                Attribute subSubAttribute = iterator.next();

                                if (subSubAttribute.getName().equals(attributeParts[1])) {

                                    if (subSubAttribute.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                                            subSubAttribute.getRequired().equals(true)) {
                                        throw new BadRequestException
                                                ("Can not remove a required attribute or a read-only attribute",
                                                        ResponseCodeConstants.MUTABILITY);
                                    } else {
                                        iterator.remove();
                                    }
                                }
                            }
                        }
                    }

                } else {
                    Attribute subAttribute = attribute.getSubAttribute(attributeParts[1]);
                    if (subAttribute != null) {
                        if (attributeParts.length == 3) {

                            if (subAttribute.getMultiValued()) {

                                List<Attribute> subSubValuesList = ((MultiValuedAttribute)
                                        subAttribute).getAttributeValues();

                                if (subSubValuesList != null) {
                                    for (Attribute subSubValue : subSubValuesList) {
                                        Map<String, Attribute> subSubAttributeList =
                                                ((ComplexAttribute) subSubValue).getSubAttributesList();
                                        //need to remove attributes while iterating through the list.
                                        for (Iterator<Attribute> iterator =
                                             subSubAttributeList.values().iterator(); iterator.hasNext();) {
                                            Attribute subSubAttribute = iterator.next();

                                            if (subSubAttribute.getName().equals(attributeParts[2])) {

                                                if (subSubAttribute.getMutability().equals
                                                        (SCIMDefinitions.Mutability.READ_ONLY) ||
                                                        subSubAttribute.getRequired().equals(true)) {
                                                    throw new BadRequestException
                                                            ("Can not remove a required attribute or a read-only " +
                                                                    "attribute", ResponseCodeConstants.MUTABILITY);
                                                } else {
                                                    iterator.remove();
                                                }
                                            }
                                        }
                                    }
                                }

                            } else {
                                Attribute subSubAttribute = subAttribute.getSubAttribute(attributeParts[2]);
                                if (subSubAttribute.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                                        subSubAttribute.getRequired().equals(true)) {
                                    throw new BadRequestException
                                            ("Can not remove a required attribute or a read-only attribute",
                                                    ResponseCodeConstants.MUTABILITY);
                                } else {
                                    String subSubAttributeName = subSubAttribute.getName();
                                    ((ComplexAttribute) subAttribute).removeSubAttribute(subSubAttributeName);
                                }
                            }

                        } else {
                            //this is complex attribute's sub attribute check
                            if (subAttribute.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                                    subAttribute.getRequired().equals(true)) {
                                throw new BadRequestException
                                        ("Can not remove a required attribute or a read-only attribute",
                                                ResponseCodeConstants.MUTABILITY);
                            } else {
                                String subAttributeName = subAttribute.getName();
                                ((ComplexAttribute) attribute).removeSubAttribute(subAttributeName);
                            }
                        }
                    } else {
                        throw new BadRequestException("No such sub attribute with the name : " +
                                attributeParts[1] + " " + "in the attribute : " +
                                attributeParts[0], ResponseCodeConstants.INVALID_PATH);
                    }

                }
            } else {
                throw new BadRequestException("No such attribute with the name : " + attributeParts[0] + " " +
                        "in the current resource", ResponseCodeConstants.INVALID_PATH);
            }
        }
        return oldResource;
    }


    /*
     * This method corresponds to the add operation in patch requests.
     * @param operation
     * @param decoder
     * @param oldResource
     * @param copyOfOldResource
     * @return
     */
    public static AbstractSCIMObject doPatchAdd(PatchOperation operation, JSONDecoder decoder,
                                                AbstractSCIMObject oldResource, AbstractSCIMObject copyOfOldResource,
                                                SCIMResourceTypeSchema schema) throws CharonException {
        try {
            User attributeHoldingSCIMUser = decoder.decode(operation.getValues().toString(), schema);
            if (oldResource != null) {
                for (String attributeName : attributeHoldingSCIMUser.getAttributeList().keySet()) {
                    Attribute oldAttribute = oldResource.getAttribute(attributeName);
                    if (oldAttribute != null) {
                        // if the attribute is there, append it.
                        if (oldAttribute.getMultiValued() &&
                                oldAttribute.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                            //this is multivalued complex case.
                            MultiValuedAttribute attributeValue = (MultiValuedAttribute)
                                    attributeHoldingSCIMUser.getAttribute(attributeName);

                            for (Attribute attribute : attributeValue.getAttributeValues()) {
                                ((MultiValuedAttribute) oldAttribute).setAttributeValue(attribute);
                            }

                        } else if (oldAttribute.getMultiValued()) {

                            //this is multivalued primitive case.
                            MultiValuedAttribute attributeValue = (MultiValuedAttribute)
                                    attributeHoldingSCIMUser.getAttribute(attributeName);

                            for (Object obj : attributeValue.getAttributePrimitiveValues()) {
                                ((MultiValuedAttribute) oldAttribute).setAttributePrimitiveValue(obj);
                            }

                        } else if (oldAttribute.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                            //this is the complex attribute case.
                            Map<String, Attribute> subAttributeList =
                                    ((ComplexAttribute) attributeHoldingSCIMUser.
                                            getAttribute(attributeName)).getSubAttributesList();

                            for (String subAttributeName : subAttributeList.keySet()) {
                                Attribute subAttribute = oldAttribute.getSubAttribute(subAttributeName);

                                if (subAttribute != null) {
                                    if (subAttribute.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                                        if (subAttribute.getMultiValued()) {
                                            //extension schema is the only one who reaches here.
                                            MultiValuedAttribute attributeSubValue = (MultiValuedAttribute)
                                                    ((ComplexAttribute) attributeHoldingSCIMUser.
                                                            getAttribute(attributeName)).
                                                            getSubAttribute(subAttributeName);

                                            for (Attribute attribute : attributeSubValue.getAttributeValues()) {
                                                ((MultiValuedAttribute) subAttribute).setAttributeValue(attribute);
                                            }
                                        } else {
                                            //extension schema is the only one who reaches here.
                                            Map<String, Attribute> subSubAttributeList = ((ComplexAttribute)
                                                    (attributeHoldingSCIMUser.getAttribute(attributeName).
                                                            getSubAttribute(subAttributeName))).getSubAttributesList();

                                            for (String subSubAttributeName : subSubAttributeList.keySet()) {
                                                Attribute subSubAttribute = oldAttribute.getSubAttribute
                                                        (subAttributeName).getSubAttribute(subSubAttributeName);

                                                if (subSubAttribute != null) {
                                                    if (subSubAttribute.getMultiValued()) {
                                                        List<Object> items = ((MultiValuedAttribute)
                                                                (subSubAttributeList.get(subSubAttributeName))).
                                                                getAttributePrimitiveValues();
                                                        for (Object item : items) {
                                                            ((MultiValuedAttribute) subSubAttribute).
                                                                    setAttributePrimitiveValue(item);
                                                        }
                                                    } else {
                                                        ((SimpleAttribute) subSubAttribute).setValue(
                                                                ((SimpleAttribute) subSubAttributeList.get
                                                                        (subSubAttributeName)).getValue());
                                                    }
                                                } else {
                                                    ((ComplexAttribute) (subAttribute)).setSubAttribute(
                                                            subSubAttributeList.get(subSubAttributeName));
                                                }
                                            }
                                        }
                                    } else {
                                        if (subAttribute.getMultiValued()) {
                                            List<Object> items = ((MultiValuedAttribute)
                                                    (subAttributeList.get(subAttributeName))).
                                                    getAttributePrimitiveValues();
                                            for (Object item : items) {
                                                ((MultiValuedAttribute) subAttribute).setAttributePrimitiveValue(item);
                                            }
                                        } else {
                                            ((SimpleAttribute) subAttribute).setValue(((SimpleAttribute)
                                                    subAttributeList.get(subAttributeName)).getValue());
                                        }
                                    }
                                } else {
                                    ((ComplexAttribute) oldAttribute).setSubAttribute
                                            (subAttributeList.get(subAttributeName));
                                }
                            }
                        } else {
                            // this is the simple attribute case.replace the value
                            ((SimpleAttribute) oldAttribute).setValue
                                    (((SimpleAttribute) attributeHoldingSCIMUser.getAttribute
                                            (oldAttribute.getName())).getValue());
                        }
                    } else {
                        //if the attribute is not already set, set it.
                        oldResource.setAttribute(attributeHoldingSCIMUser.getAttribute(attributeName));
                    }
                }
                AbstractSCIMObject validatedResource = ServerSideValidator.validateUpdatedSCIMObject
                        (copyOfOldResource, oldResource, schema);

                return validatedResource;
            } else  {
                throw new CharonException("Error in getting the old resource.");
            }
        } catch (BadRequestException | CharonException e) {
           throw new CharonException("Error in performing the add operation", e);
        }
    }

    public static AbstractSCIMObject doPatchReplace(PatchOperation operation, AbstractSCIMObject oldResource,
                                                    AbstractSCIMObject copyOfOldResource,
                                                    SCIMResourceTypeSchema schema) {

        return null;
    }
}
