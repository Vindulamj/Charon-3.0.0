package org.wso2.charon.core.v2.utils.codeutils;

import org.wso2.charon.core.v2.attributes.Attribute;
import org.wso2.charon.core.v2.attributes.ComplexAttribute;
import org.wso2.charon.core.v2.attributes.MultiValuedAttribute;
import org.wso2.charon.core.v2.attributes.SimpleAttribute;
import org.wso2.charon.core.v2.encoder.JSONDecoder;
import org.wso2.charon.core.v2.exceptions.BadRequestException;
import org.wso2.charon.core.v2.exceptions.CharonException;
import org.wso2.charon.core.v2.exceptions.NotImplementedException;
import org.wso2.charon.core.v2.objects.User;
import org.wso2.charon.core.v2.protocol.ResponseCodeConstants;
import org.wso2.charon.core.v2.schema.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * This provides the methods on the PATCH operation of any resource type
 */
public class PatchOperationSupport {

    public static User doPatchRemove(PatchOperation operation, JSONDecoder decoder, User oldUser, User copyOfOldUser)
            throws BadRequestException, IOException, NotImplementedException, CharonException {

        if(operation.getPath() == null){
            throw new BadRequestException("No path value specified for remove operation", ResponseCodeConstants.NO_TARGET);
        }

        String path = operation.getPath();
        //split the path to extract the filter if present
        String[] parts = path.split("[\\[\\]]");

        if(parts.length != 1){
            //currently we only support simple filters here
            String[] filterParts = parts[1].split(" ");

            ExpressionNode expressionNode = new ExpressionNode();
            expressionNode.setAttributeValue(filterParts[0]);
            expressionNode.setOperation(filterParts[1]);
            expressionNode.setValue(filterParts[2]);

            if(expressionNode.getOperation().equalsIgnoreCase(SCIMConstants.OperationalConstants.EQ)){
                Attribute attribute = oldUser.getAttribute(expressionNode.getAttributeValue());

            } else {
                throw new NotImplementedException("Only Eq filter is supported");
            }
        } else {
            String[] attributeParts = parts[0].split("[\\.]");
            if(attributeParts.length == 0){

                Attribute attribute = oldUser.getAttribute(parts[0]);

                if(attribute != null){
                    if(attribute.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                            attribute.getRequired().equals(true)) {
                        throw new BadRequestException("Can not remove a required attribute or a read-only attribute",
                                ResponseCodeConstants.MUTABILITY);
                    } else {
                        String attributeName = attribute.getName();
                        oldUser.deleteAttribute(attributeName);
                    }
                } else{
                    throw new BadRequestException("No such attribute with the name : " + parts[0] +" " +
                            "in the current resource", ResponseCodeConstants.INVALID_VALUE);
                }

            } else{
                Attribute attribute = oldUser.getAttribute(attributeParts[0]);
                if (attribute.getMultiValued()){

                    List<Attribute> subValuesList = ((MultiValuedAttribute)attribute).getAttributeValues();

                    if(subValuesList != null){
                        for(Attribute subValue : subValuesList){
                            Map<String, Attribute> subSubAttributeList =
                                    ((ComplexAttribute)subValue).getSubAttributesList();

                            for(Attribute subSubAttribute : subSubAttributeList.values()){

                                if(subSubAttribute.getName().equals(attributeParts[1])){

                                    if(subSubAttribute.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                                            subSubAttribute.getRequired().equals(true)) {
                                        throw new BadRequestException
                                                ("Can not remove a required attribute or a read-only attribute",
                                                        ResponseCodeConstants.MUTABILITY);
                                    } else {
                                        String subSubAttributeName = subSubAttribute.getName();
                                        ((ComplexAttribute)subValue).removeSubAttribute(subSubAttributeName);
                                    }
                                }
                            }
                        }
                    }

                } else{
                    Attribute subAttribute = attribute.getSubAttribute(parts[1]);
                    if(parts.length == 3){

                        if(subAttribute.getMultiValued()){

                            List<Attribute> subSubValuesList = ((MultiValuedAttribute)subAttribute).getAttributeValues();

                            if(subSubValuesList != null){
                                for(Attribute subSubValue : subSubValuesList){
                                    Map<String, Attribute> subSubAttributeList =
                                            ((ComplexAttribute)subSubValue).getSubAttributesList();
                                    for(Attribute subSubAttribute : subSubAttributeList.values()){

                                        if(subSubAttribute.getName().equals(attributeParts[2])){

                                            if(subSubAttribute.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                                                    subSubAttribute.getRequired().equals(true)) {
                                                throw new BadRequestException
                                                        ("Can not remove a required attribute or a read-only attribute",
                                                                ResponseCodeConstants.MUTABILITY);
                                            } else {
                                                String subSubAttributeName = subSubAttribute.getName();
                                                ((ComplexAttribute)subSubValue).removeSubAttribute(subSubAttributeName);
                                            }
                                        }
                                    }
                                }
                            }

                        } else {
                            Attribute subSubAttribute = subAttribute.getSubAttribute(attributeParts[2]);
                            if(subSubAttribute.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                                    subSubAttribute.getRequired().equals(true)) {
                                throw new BadRequestException
                                        ("Can not remove a required attribute or a read-only attribute",
                                                ResponseCodeConstants.MUTABILITY);
                            } else {
                                String subSubAttributeName = subSubAttribute.getName();
                                ((ComplexAttribute)attribute).removeSubAttribute(subSubAttributeName);
                            }
                        }

                    } else {
                        if(subAttribute.getMutability().equals(SCIMDefinitions.Mutability.READ_ONLY) ||
                                subAttribute.getRequired().equals(true)) {
                            throw new BadRequestException
                                    ("Can not remove a required attribute or a read-only attribute",
                                            ResponseCodeConstants.MUTABILITY);
                        } else {
                            String subAttributeName = subAttribute.getName();
                            ((ComplexAttribute)attribute).removeSubAttribute(subAttributeName);
                        }
                    }
                }
            }
        }
        return oldUser;
    }

    public static User doPatchAdd(PatchOperation operation, JSONDecoder decoder, User oldUser, User copyOfOldUser) {

        SCIMResourceTypeSchema schema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        User attributeHoldingSCIMUser = decoder.decode(operation.getValues().toString(), schema);
        try {
            if(oldUser != null){
                for(String attributeName : attributeHoldingSCIMUser.getAttributeList().keySet()){
                    Attribute oldAttribute = oldUser.getAttribute(attributeName);
                    if( oldAttribute != null){
                        // if the attribute is there, append it.
                        if(oldAttribute.getMultiValued() &&
                                oldAttribute.getType().equals(SCIMDefinitions.DataType.COMPLEX)){
                            //this is multivalued complex case.
                            MultiValuedAttribute attributeValue = (MultiValuedAttribute)
                                    attributeHoldingSCIMUser.getAttribute(attributeName);

                            for(Attribute attribute : attributeValue.getAttributeValues()){
                                ((MultiValuedAttribute)oldAttribute).setAttributeValue(attribute);
                            }

                        } else if (oldAttribute.getMultiValued()){

                            //this is multivalued primitive case.
                            MultiValuedAttribute attributeValue = (MultiValuedAttribute)
                                    attributeHoldingSCIMUser.getAttribute(attributeName);

                            for(Object obj : attributeValue.getAttributePrimitiveValues()){
                                ((MultiValuedAttribute)oldAttribute).setAttributePrimitiveValue(obj);
                            }

                        } else if (oldAttribute.getType().equals(SCIMDefinitions.DataType.COMPLEX)){
                            //this is the complex attribute case.
                            Map<String, Attribute> subAttributeList =
                                    ((ComplexAttribute)attributeHoldingSCIMUser.
                                            getAttribute(attributeName)).getSubAttributesList();

                            for(String subAttributeName : subAttributeList.keySet()){
                                Attribute subAttribute = oldAttribute.getSubAttribute(subAttributeName);

                                if(subAttribute != null){
                                    if(subAttribute.getType().equals(SCIMDefinitions.DataType.COMPLEX)) {
                                        if(subAttribute.getMultiValued()){
                                            //extension schema is the only one who reaches here.
                                            MultiValuedAttribute attributeSubValue = (MultiValuedAttribute)
                                                    ((ComplexAttribute) attributeHoldingSCIMUser.
                                                            getAttribute(attributeName)).
                                                            getSubAttribute(subAttributeName);

                                            for(Attribute attribute : attributeSubValue.getAttributeValues()){
                                                ((MultiValuedAttribute)subAttribute).setAttributeValue(attribute);
                                            }
                                        } else {
                                            //extension schema is the only one who reaches here.
                                            Map<String, Attribute> subSubAttributeList = ((ComplexAttribute)
                                                    (attributeHoldingSCIMUser.getAttribute(attributeName).
                                                            getSubAttribute(subAttributeName))).getSubAttributesList();

                                            for(String subSubAttributeName : subSubAttributeList.keySet()) {
                                                Attribute subSubAttribute = oldAttribute.getSubAttribute(subAttributeName).
                                                        getSubAttribute(subSubAttributeName);

                                                if(subSubAttribute != null){
                                                    if(subSubAttribute.getMultiValued()){
                                                        List<Object> items = ((MultiValuedAttribute)
                                                                (subSubAttributeList.get(subSubAttributeName))).
                                                                getAttributePrimitiveValues();
                                                        for(Object item : items){
                                                            ((MultiValuedAttribute)subSubAttribute).
                                                                    setAttributePrimitiveValue(item);
                                                        }
                                                    } else {
                                                        ((SimpleAttribute)subSubAttribute).setValue(((SimpleAttribute)
                                                                subSubAttributeList.get(subSubAttributeName)).getValue());
                                                    }
                                                } else {
                                                    ((ComplexAttribute)(subAttribute)).setSubAttribute(
                                                            subSubAttributeList.get(subSubAttributeName));
                                                }
                                            }
                                        }
                                    } else {
                                        if(subAttribute.getMultiValued()){
                                            List<Object> items = ((MultiValuedAttribute)
                                                    (subAttributeList.get(subAttributeName))).
                                                    getAttributePrimitiveValues();
                                            for(Object item : items){
                                                ((MultiValuedAttribute)subAttribute).setAttributePrimitiveValue(item);
                                            }
                                        } else {
                                            ((SimpleAttribute)subAttribute).setValue(((SimpleAttribute)
                                                    subAttributeList.get(subAttributeName)).getValue());
                                        }
                                    }
                                } else {
                                    ((ComplexAttribute)oldAttribute).setSubAttribute
                                            (subAttributeList.get(subAttributeName));
                                }
                            }
                        } else {
                            // this is the simple attribute case.replace the value
                            ((SimpleAttribute)oldAttribute).setValue
                                    (((SimpleAttribute)attributeHoldingSCIMUser.getAttribute
                                            (oldAttribute.getName())).getValue());
                        }
                    } else {
                        //if the attribute is not already set, set it.
                        oldUser.setAttribute(attributeHoldingSCIMUser.getAttribute(attributeName));
                    }
                }
                User validatedUser = (User) ServerSideValidator.validateUpdatedSCIMObject
                        (copyOfOldUser, oldUser, schema);

                return validatedUser;
            }
        } catch (CharonException e) {
            e.printStackTrace();
        } catch (BadRequestException e) {
            e.printStackTrace();
        }
        return null;
    }

}
