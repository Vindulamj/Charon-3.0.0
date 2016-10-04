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
               "  \"userName\": \"jayan@example.com\",\n" +
               "  \"password\": \"testpass\",\n" +
               "  \"name\": {\n" +
               "    \"formatted\": \"Ms. Barbara J Jensen, III\",\n" +
               "    \"familyName\": \"sachini\",\n" +
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
               "      \"value\": \"bjensen@example.com\",\n" +
               "      \"type\": \"work\",\n" +
               "      \"primary\": true\n" +
               "    },\n" +
               "    {\n" +
               "      \"value\": \"babs@jensen.org\",\n" +
               "      \"type\": \"home\"\n" +
               "    }\n" +
               "  ]}";

       String attributes="emails.value,name.familyName";
       String excludeAttributes="externalId,emails.value,name";

       //----CREATE USER --------
       //SCIMResponse res=um.create(array,new SCIMUserManager(),null,excludeAttributes);


       //-----GET USER  ---------
       //SCIMResponse res= um.get("af8cdc9c-d6b2-414c-82ab-ad02993151f3",new SCIMUserManager(),attributes,null);

       //-----DELETE USER  ---------
       //SCIMResponse res= um.delete("cf712155-e974-42ae-9e57-6c42f7bbadad",new SCIMUserManager());

       //-----LIST USER  ---------
       SCIMResponse res= um.list(new SCIMUserManager(),attributes,null);

       //-----LIST USER WITH PAGINATION ---------
       //SCIMResponse res= um.listWithPagination(1,1,new SCIMUserManager(),attributes,null);

       //-----UPDATE USER VIA PUT ---------
       //SCIMResponse res= um.updateWithPUT("0d5d76ff-5250-402e-af4f-029b60f871c8",array,new SCIMUserManager());

       System.out.println(res.getResponseStatus());
       System.out.println("");
       System.out.println(res.getHeaderParamMap());
       System.out.println("");
       System.out.println(res.getResponseMessage());
   }

}
