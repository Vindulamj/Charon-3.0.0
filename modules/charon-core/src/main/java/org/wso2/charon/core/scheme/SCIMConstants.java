package org.wso2.charon.core.scheme;

/**
 * This defines the constants which can be found in SCIM 2.0 core scheme
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
        public static final String RESOURCE_TYPE = "resourceType";
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

        //possible canonical values
        public static final String HOME = "home";
        public static final String WORK = "work";
        public static final String OTHER = "other";
        public static final String MOBILE = "mobile";
        public static final String FAX = "fax";
        public static final String PAGER = "pager";

        public static final String SKYPE = "skpye";
        public static final String YAHOO = "yahoo";
        public static final String AIM = "aim";
        public static final String GTALK = "gtalk";
        public static final String ICQ = "icq";
        public static final String XMPP = "xmpp";
        public static final String MSN = "msn";
        public static final String QQ = "qq";

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

    /*******Attributes Descriptions of the attributes found in Common Schema***************/

    public static final String ID_DESC = "Unique identifier for the SCIM Resource as defined by the Service Provider.";
    public static final String EXTERNAL_ID_DESC = "A String that is an identifier for the resource as defined by the provisioning client." +
            "The service provider MUST always interpret the externalId as scoped to the provisioning domain.";
    public static final String META_DESC = "A complex attribute containing resource metadata.";
    public static final String RESOURCE_TYPE_DESC = "The name of the resource type of the resource.";
    public static final String CREATED_DESC="The \"DateTime\" that the resource was added to the service provider.";
    public static final String LAST_MODIFIED_DESC="The most recent DateTime that the details of this resource were updated at the service provider.";
    public static final String LOCATION_DESC ="Location  The URI of the resource being returned";
    public static final String VERSION_DESC="The version of the resource being returned.";

    /*******Attributes Descriptions of the attributes found in User Schema***************/

    public static final String USERNAME_DESC="A service provider's unique identifier for the user, typically\n" +
            "used by the user to directly authenticate to the service provider.Each User MUST include a non-empty userName value.  This identifier\n" +
            "MUST be unique across the service provider's entire set of Users.";

    public static final String NAME_DESC="The components of the user's real name.Providers MAY return just the full name as a single string in the\n" +
            "formatted sub-attribute, or they MAY return just the individual component attributes using the other sub-attributes, or they MAY\n" +
            "return both.  If both variants are returned, they SHOULD be describing the same name, with the formatted name indicating how the\n" +
            "component attributes should be combined.";

    public static final String FORMATTED_NAME_DESC="The full name, including all middle names, titles, and suffixes as appropriate, formatted for display\n" +
            "(e.g., 'Ms. Barbara J Jensen, III').";
    public static final String FAMILY_NAME_DESC="The family name of the User, or last name in most Western languages (e.g., 'Jensen' given the full\n" +
            "name 'Ms. Barbara J Jensen, III').";
    public static final String GIVEN_NAME_DESC="The given name of the User, or first name in most Western languages (e.g., 'Barbara' given the\n" +
            "full name 'Ms. Barbara J Jensen, III').";
    public static final String MIDDLE_NAME_DESC="The middle name(s) of the User (e.g., 'Jane' given the full name 'Ms. Barbara J Jensen, III').";
    public static final String HONORIFIC_PREFIX_DESC="The honorific prefix(es) of the User, or title in most Western languages (e.g., 'Ms.' given the full name\n" +
            "'Ms. Barbara J Jensen, III').";
    public static final String HONORIFIC_SUFFIX_DESC="The honorific suffix(es) of the User, or suffix in most Western languages (e.g., 'III' given the full name\n" +
            "'Ms. Barbara J Jensen, III').";

    public static final String DISPLAY_NAME_DESC="The name of the User, suitable for display\n" +
            "to end-users.  The name SHOULD be the full name of the User being described, if known.";
    public static final String NICK_NAME_DESC="The casual way to address the user in real life, e.g., 'Bob' or 'Bobby' instead of 'Robert'.  This attribute\n" +
            "SHOULD NOT be used to represent a User's username (e.g., 'bjensen' or 'mpepperidge').";
    public static final String PROFILE_URL_DESC="A fully qualified URL pointing to a page\n" +
            "representing the User's online profile.";
    public static final String TITLE_DESC="The user's title, such as \\\"Vice President.\\\"";
    public static final String USER_TYPE_DESC="Used to identify the relationship between the organization and the user.  Typical values used might be\n" +
            "'Contractor', 'Employee', 'Intern', 'Temp', 'External', and 'Unknown', but any value may be used.";
    public static final String PREFERRED_LANGUAGE_DESC="Indicates the User's preferred written or\n" +
            "spoken language.  Generally used for selecting a localized user interface; e.g., 'en_US' specifies the language English and country";
    public static final String LOCALE_DESC="Used to indicate the User's default location\n" +
            "for purposes of localizing items such as currency, date time format, or numerical representations.";
    public static final String TIME_ZONE_DESC="The User's time zone in the 'Olson' time zone\n" +
            "database format, e.g., 'America/Los_Angeles'.";
    public static final String ACTIVE_DESC="A Boolean value indicating the User's administrative status.";
    public static final String PASSWORD_DESC="The User's cleartext password.  This attribute is intended to be used as a means to specify an initial\n" +
            "password when creating a new User or to reset an existing User's password.";

    public static final String EMAILS_DESC="Email addresses for the user.  The value SHOULD be canonicalized by the service provider, e.g.,\n" +
            "'bjensen@example.com' instead of 'bjensen@EXAMPLE.COM'.Canonical type values of 'work', 'home', and 'other'.";
    public static final String EMAIL_VALUE_DESC="Email addresses for the user.  The value SHOULD be canonicalized by the service provider, e.g.,\n" +
            "'bjensen@example.com' instead of 'bjensen@EXAMPLE.COM'.Canonical type values of 'work', 'home', and 'other'.";
    public static final String EMAIL_DISPLAY_DESC="A human-readable name, primarily used for display purposes.  READ-ONLY.";
    public static final String EMAIL_TYPE_DESC="A label indicating the attribute's function, e.g., 'work' or 'home'.";
    public static final String EMAIL_PRIMARY_DESC="A Boolean value indicating the 'primary' or preferred attribute value for this attribute, " +
            "e.g., the psreferred mailing address or primary email address.  The primary attribute value 'true' MUST appear no more than once.";

    public static final String PHONE_NUMBERS_DESC="Phone numbers for the User.  The value SHOULD be canonicalized by the service provider according to the\n" +
            "format specified in RFC 3966, e.g., 'tel:+1-201-555-0123'.Canonical type values of 'work', 'home', 'mobile', 'fax', 'pager";
    public static final String PHONE_NUMBERS_VALUE_DESC="Phone number of the User.";
    public static final String PHONE_NUMBERS_DISPLAY_DESC="A human-readable name, primarily used for display purposes.  READ-ONLY.";
    public static final String PHONE_NUMBERS_TYPE_DESC="A label indicating the attribute's function, e.g., 'work', 'home', 'mobile'.";
    public static final String PHONE_NUMBERS_PRIMARY_DESC="A Boolean value indicating the 'primary' or preferred attribute value for this attribute, e.g., the preferred\n" +
            "phone number or primary phone number.  The primary attribute value 'true' MUST appear no more than once.";

    public static final String IMS_DESC="Instant messaging addresses for the User.";
    public static final String IMS_VALUE_DESC="Instant messaging address for the User.";
    public static final String IMS_DISPLAY_DESC="A human-readable name, primarily used for display purposes.  READ-ONLY.";
    public static final String IMS_TYPE_DESC="A label indicating the attribute's function, e.g., 'aim', 'gtalk', 'xmpp'.";
    public static final String IMS_PRIMARY_DESC="A Boolean value indicating the 'primary' or preferred attribute value for this attribute, e.g., the preferred\n" +
            "messenger or primary messenger.  The primary attribute value 'true' MUST appear no more than once.";

    public static final String PHOTOS_DESC="URLs of photos of the User.";
    public static final String PHOTOS_VALUE_DESC="URLs of photos of the User.";
    public static final String PHOTOS_DISPLAY_DESC="A human-readable name, primarily used for display purposes.  READ-ONLY.";
    public static final String PHOTOS_TYPE_DESC="A label indicating the attribute's function, i.e., 'photo' or 'thumbnail'.";
    public static final String PHOTOS_PRIMARY_DESC="A Boolean value indicating the 'primary' or preferred attribute value for this attribute, e.g., the preferred\n" +
            "phone number or primary phone number. The primary attribute value 'true' MUST appear no more than once.";

    public static final String ADDRESSES_DESC="A physical mailing address for this User.\n" +
            "Canonical type values of 'work', 'home', and 'other'.  This attribute is a complex type with the following sub-attributes.";
    public static final String ADDRESSES_FORMATTED_DESC="The full mailing address, formatted for display or use with a mailing label.  This attribute MAY contain\n" +
            "newlines.";
    public static final String ADDRESSES_STREET_ADDRESS_DESC="The full street address component, which may include house number, street name, P.O. box, and multi-line\n" +
            "extended street address information.  This attribute MAY contain newlines.";
    public static final String ADDRESSES_LOCALITY_DESC="The city or locality component.";
    public static final String ADDRESSES_REGION_DESC="The state or region component.";
    public static final String ADDRESSES_POSTAL_CODE_DESC="The zip code or postal code component.";
    public static final String ADDRESSES_COUNTRY_DESC="The country name component.";
    public static final String ADDRESSES_TYPE_DESC="A label indicating the attribute's function, e.g., 'work' or 'home'.";

    public static final String GROUPS_DESC="A list of groups to which the user belongs,\n" +
            "either through direct membership, through nested groups, or dynamically calculated.";
    public static final String GROUP_VALUE_DESC="The identifier of the User's group.";
    public static final String GROUP_DISPLAY_DESC="A human-readable name, primarily used for display purposes. READ-ONLY.";
    public static final String GROUP_$REF_DESC="The URI of the corresponding 'Group' resource to which the user belongs.";
    public static final String GROUP_TYPE_DESC="A label indicating the attribute's function, e.g., 'direct' or 'indirect'.";

    public static final String ENTITLEMENTS_DESC="A list of entitlements for the User that represent a thing the User has.";
    public static final String ENTITLEMENTS_VALUE_DESC="The value of an entitlement.";
    public static final String ENTITLEMENTS_DISPLAY_DESC="A human-readable name, primarily used for display purposes.  READ-ONLY.";
    public static final String ENTITLEMENTS_TYPE_DESC="A label indicating the attribute's function.";
    public static final String ENTITLEMENTS_PRIMARY_DESC="A Boolean value indicating the 'primary' or preferred attribute value for this attribute.  The primary\n" +
            "attribute value 'true' MUST appear no more than once.";

    public static final String ROLES_DESC="A list of roles for the User that collectively represent who the User is, e.g., 'Student', 'Faculty'.";
    public static final String ROLES_VALUE_DESC="The value of a role.";
    public static final String ROLES_DISPLAY_DESC="A human-readable name, primarily used for display purposes.  READ-ONLY.";
    public static final String ROLES_TYPE_DESC="A label indicating the attribute's function.";
    public static final String ROLES_PRIMARY_DESC="A Boolean value indicating the 'primary' or preferred attribute value for this attribute.  The primary attribute value 'true' MUST appear no more than once.";

    public static final String X509CERTIFICATES_DESC="A list of certificates issued to the User.";
    public static final String X509CERTIFICATES_VALUE_DESC="The value of an X.509 certificate.";
    public static final String X509CERTIFICATES_DISPLAY_DESC="A human-readable name, primarily used for display purposes.  READ-ONLY.";
    public static final String X509CERTIFICATES_TYPE_DESC="A label indicating the attribute's function.";
    public static final String X509CERTIFICATES_PRIMARY_DESC="A Boolean value indicating the 'primary' or preferred attribute value for this attribute." +
            "The primary attribute value 'true' MUST appear no more than once.";
}