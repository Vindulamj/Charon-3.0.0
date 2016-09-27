package org.wso2.charon.core.objects;

import org.wso2.charon.core.attributes.Attribute;
import org.wso2.charon.core.exceptions.NotFoundException;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * SCIM Object is a collection of attributes that may come from different schemas.
 * This interface supports that concept, which will be implemented by SCIM objects.
 * In server-side, there should be a way to map the storage to these attributes.
 */
//SCIMObject is extended from Serializable as later in org.wso2.charon.core.util.CopyUtil, it need to be serialized.
public interface SCIMObject extends Serializable {

    public Attribute getAttribute(String attributeName) throws NotFoundException;

    public void deleteAttribute(String attributeName) throws NotFoundException;

    public List<String> getSchemaList();

    public Map<String, Attribute> getAttributeList();

    public void setSchema(String schema);

}
