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
                "     \"displayName\": \"Doctors\",\n" +
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

        String attributes="displayName";
        String excludeAttributes="members";

        //----CREATE Group--------
        //SCIMResponse res=um.create(array,new SCIMUserManager(),null,null);

        //-----GET GROUP ---------
        //SCIMResponse res= um.get("c2fa9b6d-5865-4378-948a-f349b64d1544",new SCIMUserManager(),null,excludeAttributes);

        //-----DELETE GROUP  ---------
        //SCIMResponse res= um.delete("c2fa9b6d-5865-4378-948a-f349b64d1544",new SCIMUserManager());

        //-----LIST GROUPS ---------
        //SCIMResponse res= um.list(new SCIMUserManager(),null,null);

        //-----LIST GROUPS WITH PAGINATION  ---------
        //SCIMResponse res= um.listWithPagination(2,1,new SCIMUserManager(),null,null);

        //-----FILTER GROUPS at Groups Endpoint  ---------
        //String filter ="members.value eq 2819c223-7f76-453a-919d-413861904646";
        String filter ="displayName eq Doctors";
        //SCIMResponse res= um.listByFilter(filter, new SCIMUserManager(), null, null);

        //-----SORT GROUPS  ---------
        SCIMResponse res= um.listBySort(null,"AsCEnding",new SCIMUserManager(),attributes,null);

        System.out.println(res.getResponseStatus());
        System.out.println("");
        System.out.println(res.getHeaderParamMap());
        System.out.println("");
        System.out.println(res.getResponseMessage());
    }
}
