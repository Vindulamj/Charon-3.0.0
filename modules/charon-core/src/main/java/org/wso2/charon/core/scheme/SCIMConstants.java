package org.wso2.charon.core.scheme;

/**
 * This defines the constants which can be found in SCIM core scheme
 * can be found at : https://tools.ietf.org/html/rfc7643
 */
public class SCIMConstants {

    public static final String CORE_SCHEMA_URI = "urn:ietf:params:scim:schemas:core:2.0";
    public static final String USER_CORE_SCHEMA_URI = "urn:ietf:params:scim:schemas:core:2.0:User";

    /*Data formats*/
    public static final String JSON = "json";
    public static final String XML = "xml";

    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_XML = "application/xml";

    public static final String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss";

    /*Constants found in core-common schema.*/

    public static class CommonSchemaConstants {

        public static final String SCHEMAS = "schemas";
        public static final String ID = "id";
        public static final String EXTERNAL_ID = "externalId";
        public static final String META = "meta";
        public static final String RESOURCE_TYPE="resourceType";
        public static final String CREATED = "created";
        public static final String LAST_MODIFIED = "lastModified";
        public static final String LOCATION = "location";
        public static final String VERSION = "version";

        //characteristics of multi valued attribute
        public static final String TYPE = "type";
        public static final String PRIMARY = "primary";
        public static final String DISPLAY = "display";
        public static final String $REF = "$ref";
        public static final String VALUE = "value";
    }

    /*Constants found in core-user schema.*/

    public static class UserSchemaConstants {

        public static final String USER_NAME = "userName";

        public static final String NAME = "name";
        public static final String FORMATTED_NAME = "formatted";
        public static final String FAMILY_NAME = "familyName";
        public static final String GIVEN_NAME = "givenName";
        public static final String MIDDLE_NAME = "middleName";
        public static final String HONORIFIC_PREFIX = "honorificPrefix";
        public static final String HONORIFIC_SUFFIX = "honorificSuffix";

        public static final String DISPLAY_NAME = "displayName";
        public static final String NICK_NAME = "nickName";
        public static final String PROFILE_URL = "profileUrl";
        public static final String TITLE = "title";
        public static final String USER_TYPE = "userType";
        public static final String PREFERRED_LANGUAGE = "preferredLanguage";
        public static final String LOCALE = "locale";
        public static final String TIME_ZONE = "timezone";
        public static final String ACTIVE = "active";
        public static final String PASSWORD = "password";

        //Multi-Valued Attributes
        public static final String EMAILS = "emails";
        public static final String PHONE_NUMBERS = "phoneNumbers";
        public static final String IMS = "ims";
        public static final String PHOTOS = "photos";
        public static final String ADDRESSES = "addresses";
        public static final String GROUPS = "groups";
        public static final String ENTITLEMENTS = "entitlements";
        public static final String ROLES = "roles";
        public static final String X509CERTIFICATES = "x509Certificates";

        //possible sub attributes for multi-valued attributes like emails,phoneNumbers
        public static final String HOME = "home";
        public static final String WORK = "work";
        public static final String OTHER = "other";
        public static final String MOBILE = "mobile";
        public static final String FAX = "fax";
        public static final String PAGER = "pager";

        public static final String DISPLAY = "display";

        public static final String SKYPE = "skpye";
        public static final String YAHOO = "yahoo";

        public static final String PHOTO = "photo";
        public static final String THUMBNAIL = "thumbnail";

        public static final String FORMATTED_ADDRESS = "formatted";
        public static final String STREET_ADDRESS = "streetAddress";
        public static final String LOCALITY = "locality";
        public static final String REGION = "region";
        public static final String POSTAL_CODE = "postalCode";
        public static final String COUNTRY = "country";

        public static final String DIRECT_MEMBERSHIP = "direct";
        public static final String INDIRECT_MEMBERSHIP = "indirect";
    }

    /*Resource names as defined in SCIM Schema spec*/
    public static final String USER = "User";

    /*Resource endpoints relative to the base SCIM URL*/
    public static final String USER_ENDPOINT = "/Users";

    //HTTP Headers used in SCIM request/response other than auth headers.
    public static final String LOCATION_HEADER = "Location";
    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String ACCEPT_HEADER = "Accept";


}