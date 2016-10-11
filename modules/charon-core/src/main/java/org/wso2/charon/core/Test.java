package org.wso2.charon.core;

import org.wso2.charon.core.encoder.JSONDecoder;
import org.wso2.charon.core.encoder.JSONEncoder;
import org.wso2.charon.core.protocol.SCIMResponse;
import org.wso2.charon.core.protocol.endpoints.AbstractResourceManager;
import org.wso2.charon.core.protocol.endpoints.UserResourceManager;
import org.wso2.charon.core.schema.SCIMConstants;

import java.util.HashMap;

/**
 * This class is only for testing purpose
 */
public class Test {

   public static void main(String [] args){
       UserResourceManager um =new UserResourceManager();
       um.setEncoder(new JSONEncoder());
       um.setDecoder(new JSONDecoder());
       HashMap hmp=new HashMap<String,String>();
       hmp.put(SCIMConstants.USER_ENDPOINT,"http://localhost:8080/scim/v2/Users");
       um.setEndpointURLMap(hmp);
       String array ="{\n" +
               "  \"schemas\": [\"urn:ietf:params:scim:schemas:core:2.0:User\"],\n" +
               "  \"id\":\"23232\",\n"+
               "  \"externalId\": \"701984\",\n" +
               "  \"userName\": \"Shankar\",\n" +
               "  \"password\": \"testpass\",\n" +
               "  \"name\": {\n" +
               "    \"formatted\": \"Ms. Barbara J Jensen, III\",\n" +
               "    \"familyName\": \"Sachini\",\n" +
               "    \"givenName\": \"VJ\",\n" +
               "    \"middleName\": \"Jane\",\n" +
               "    \"honorificPrefix\": \"Ms.\",\n" +
               "    \"honorificSuffix\": \"III\"\n" +
               "  },\n" +
               "  \"displayName\": \"Babs Jensen\",\n" +
               "  \"nickName\": \"Babs\",\n" +
               "  \"profileUrl\": \"https://login.example.com/bjensen\",\n" +
               "  \"emails\": [\n" +
               "    {\n" +
               "      \"value\": \"yy@example.com\",\n" +
               "      \"type\": \"work\",\n" +
               "      \"primary\": true\n" +
               "    },\n" +
               "    {\n" +
               "      \"value\": \"uu@jensen.org\",\n" +
               "      \"type\": \"work\"\n" +
               "    }\n" +
               "  ]}";

       String attributes="userName";
       String excludeAttributes="externalId,emails.value";

       //----CREATE USER --------
       //SCIMResponse res=um.create(array,new SCIMUserManager(),null,null);


       //-----GET USER  ---------
       //SCIMResponse res= um.get("a713e12b-0364-4d54-b939-6d1230d40251",new SCIMUserManager(),null,null);

       //-----DELETE USER  ---------
       //SCIMResponse res= um.delete("cf712155-e974-42ae-9e57-6c42f7bbadad",new SCIMUserManager());

       //-----LIST USER  ---------
       //SCIMResponse res= um.list(new SCIMUserManager(),attributes,null);

       //-----LIST USER WITH PAGINATION ---------
       //SCIMResponse res= um.listWithPagination(1,7,new SCIMUserManager(),null,null);

       //-----UPDATE USER VIA PUT ---------
       //SCIMResponse res= um.updateWithPUT("a713e12b-0364-4d54-b939-6d1230d40251",array,new SCIMUserManager(),null,null);

       //-----FILTER AT USER ENDPOINT ---------
       String filter ="userType ne Employee and (not (emails co example.com) or emails.value co example.org)";
       SCIMResponse res= um.listByFilter(filter, new SCIMUserManager(), attributes, null);

       System.out.println(res.getResponseStatus());
       System.out.println("");
       System.out.println(res.getHeaderParamMap());
       System.out.println("");
       System.out.println(res.getResponseMessage());
   }

}
