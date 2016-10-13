package org.wso2.charon.core;

import org.wso2.charon.core.encoder.JSONDecoder;
import org.wso2.charon.core.encoder.JSONEncoder;
import org.wso2.charon.core.protocol.SCIMResponse;
import org.wso2.charon.core.protocol.endpoints.GroupResourceManager;
import org.wso2.charon.core.protocol.endpoints.UserResourceManager;
import org.wso2.charon.core.schema.SCIMConstants;

import java.util.HashMap;

/**
 * This class is only for testing purpose
 */
public class GroupTest {
    public static void main(String [] args) {
        GroupResourceManager um = new GroupResourceManager();
        um.setEncoder(new JSONEncoder());
        um.setDecoder(new JSONDecoder());
        HashMap hmp = new HashMap<String, String>();
        hmp.put(SCIMConstants.GROUP_ENDPOINT, "http://localhost:8080/scim/v2/Groups");
        um.setEndpointURLMap(hmp);

        String array ="{\n" +
                "     \"schemas\": [\"urn:ietf:params:scim:schemas:core:2.0:Group\"],\n" +
                "     \"id\": \"e9e30dba-f08f-4109-8486-d5c6a331660a\",\n" +
                "     \"displayName\": \"Tour Guides\",\n" +
                "     \"members\": [\n" +
                "       {\n" +
                "         \"value\": \"2819c223-7f76-453a-919d-413861904646\",\n" +
                "         \"$ref\":\n" +
                "   \"https://example.com/v2/Users/2819c223-7f76-453a-919d-413861904646\",\n" +
                "         \"display\": \"Babs Jensen\"\n" +
                "       },\n" +
                "       {\n" +
                "         \"value\": \"902c246b-6245-4190-8e05-00816be7344a\",\n" +
                "         \"$ref\":\n" +
                "   \"https://example.com/v2/Users/902c246b-6245-4190-8e05-00816be7344a\",\n" +
                "         \"display\": \"Mandy Pepperidge\"\n" +
                "       }\n" +
                "     ]\n"+
                "     }";

        String attributes="id";
        String excludeAttributes="members";

        //----CREATE Group--------
        //SCIMResponse res=um.create(array,new SCIMUserManager(),null,null);

        //-----GET GROUP ---------
        //SCIMResponse res= um.get("c2fa9b6d-5865-4378-948a-f349b64d1544",new SCIMUserManager(),null,excludeAttributes);

        //-----DELETE GROUP  ---------
        //SCIMResponse res= um.delete("c2fa9b6d-5865-4378-948a-f349b64d1544",new SCIMUserManager());

        //-----LIST USER  ---------
        SCIMResponse res= um.list(new SCIMUserManager(),attributes,null);

        System.out.println(res.getResponseStatus());
        System.out.println("");
        System.out.println(res.getHeaderParamMap());
        System.out.println("");
        System.out.println(res.getResponseMessage());
    }
}
