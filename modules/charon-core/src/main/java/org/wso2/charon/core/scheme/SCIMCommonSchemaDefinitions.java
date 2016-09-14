package org.wso2.charon.core.scheme;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class contains the common schema definitions in
 * https://tools.ietf.org/html/rfc7643 as AttributeSchemas.
 * These are used when constructing SCIMObjects from the decoded payload
 */

public abstract class SCIMCommonSchemaDefinitions {

                /*********** SCIM defined common attribute schemas****************************/

    /* the default set of sub-attributes for a multi-valued attribute */

    /* sub-attribute schemas of the attributes defined in SCIM common schema. */

    // sub attributes of the meta attributes

    //The name of the resource type of the resource.
    public static final SCIMAttributeSchema RESOURCE_TYPE =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.CommonSchemaConstants.RESOURCE_TYPE,
                    SCIMDefinitions.DataType.STRING,false,SCIMConstants.RESOURCE_TYPE_DESC,false,true,
                    SCIMDefinitions.Mutability.READ_ONLY,SCIMDefinitions.Returned.DEFAULT,
                    SCIMDefinitions.Uniqueness.NONE,null,null,null);

    //The "DateTime" that the resource was added to the service provider.
    public static final SCIMAttributeSchema CREATED =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.CommonSchemaConstants.CREATED,
                    SCIMDefinitions.DataType.DATE_TIME,false,SCIMConstants.CREATED_DESC,false,false,
                    SCIMDefinitions.Mutability.READ_ONLY,SCIMDefinitions.Returned.DEFAULT,
                    SCIMDefinitions.Uniqueness.NONE,null,null,null);

    //The most recent DateTime that the details of this resource were updated at the service provider.
    public static final SCIMAttributeSchema LAST_MODIFIED =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.CommonSchemaConstants.LAST_MODIFIED,
                    SCIMDefinitions.DataType.DATE_TIME,false,SCIMConstants.LAST_MODIFIED_DESC,false,false,
                    SCIMDefinitions.Mutability.READ_ONLY,SCIMDefinitions.Returned.DEFAULT,
                    SCIMDefinitions.Uniqueness.NONE,null,null,null);

    //The URI of the resource being returned
    public static final SCIMAttributeSchema LOCATION =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.CommonSchemaConstants.LOCATION,
                    SCIMDefinitions.DataType.STRING,false,SCIMConstants.LOCATION_DESC,false,false,
                    SCIMDefinitions.Mutability.READ_ONLY,SCIMDefinitions.Returned.DEFAULT,
                    SCIMDefinitions.Uniqueness.NONE,null,null,null);

    //The version of the resource being returned.
    //This value must be the same as the entity-tag (ETag) HTTP response header.
    public static final SCIMAttributeSchema VERSION =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.CommonSchemaConstants.VERSION,
                    SCIMDefinitions.DataType.STRING,false,SCIMConstants.VERSION_DESC,false,true,
                    SCIMDefinitions.Mutability.READ_ONLY,SCIMDefinitions.Returned.DEFAULT,
                    SCIMDefinitions.Uniqueness.NONE,null,null,null);

            /*---------------------------------------------------------------------------------------------*/

    /* attribute schemas of the attributes defined in common schema. */

    //A unique identifier for a SCIM resource as defined by the service provider
    public static final SCIMAttributeSchema ID =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.CommonSchemaConstants.ID,
                    SCIMDefinitions.DataType.STRING,false,SCIMConstants.ID_DESC,true,true,
                    SCIMDefinitions.Mutability.READ_ONLY,SCIMDefinitions.Returned.ALWAYS,
                    SCIMDefinitions.Uniqueness.SERVER,null,null,null);

    //A String that is an identifier for the resource as defined by the provisioning client.
    //The service provider MUST always interpret the externalId as scoped to the provisioning domain.
    public static final SCIMAttributeSchema EXTERNAL_ID =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.CommonSchemaConstants.EXTERNAL_ID,
                    SCIMDefinitions.DataType.STRING,false,SCIMConstants.EXTERNAL_ID_DESC,false,true,
                    SCIMDefinitions.Mutability.READ_WRITE,SCIMDefinitions.Returned.DEFAULT,
                    SCIMDefinitions.Uniqueness.NONE,null,null,null);

    //A complex attribute containing resource metadata.
    public static final SCIMAttributeSchema META =
            SCIMAttributeSchema.createSCIMAttributeSchema(SCIMConstants.CommonSchemaConstants.META,
                    SCIMDefinitions.DataType.COMPLEX,false,SCIMConstants.META_DESC,false,false,
                    SCIMDefinitions.Mutability.READ_ONLY,SCIMDefinitions.Returned.DEFAULT,
                    SCIMDefinitions.Uniqueness.NONE,null,null,
                    new ArrayList<SCIMAttributeSchema>(Arrays.asList(RESOURCE_TYPE,CREATED,LAST_MODIFIED,LOCATION,VERSION)));

}
