# Charon-3.0

Currently following features are supported.

#### /Users Endpoint
- [x] Create
- [x] Get
- [x] Delete
- [x] List
- [x] Pagination
- [x] attributes and exclude attribute support for all operations
- [x] Update with PUT
- [x] Sorting
- [x] Filtering including complex filters

#### /Groups Endpoint
- [x] Create
- [x] Get
- [x] Delete
- [x] List
- [x] Filtering including complex filters
- [x] Pagination
- [x] attributes and exclude attribute support for all operations
- [x] Sorting
- [x] Update with PUT

#### /Me Endpoint
- [x] Create
- [x] Get
- [x] Delete
- [x] attributes and exclude attribute support for all operations
- [x] Update with PUT

#### EnterpriseUser
- [x] Create
- [x] Get
- [x] Delete
- [x] List
- [x] Pagination
- [x] attributes and exclude attribute support for all operations
- [x] Update with PUT
- [x] Sorting
- [x] Filtering including complex filters

#### /ServiceProviderConfig Endpoint
- [x] Get

#####Following types of filters are supported.

filter=userName eq "bjensen"

filter=name.familyName co "O'Malley"

filter=userName sw "J"

filter=urn:ietf:params:scim:schemas:core:2.0:User:userName sw "J"

filter=title pr

filter=meta.lastModified gt "2011-05-13T04:42:34Z"

filter=meta.lastModified ge "2011-05-13T04:42:34Z"

filter=meta.lastModified lt "2011-05-13T04:42:34Z"

filter=meta.lastModified le "2011-05-13T04:42:34Z"

filter=title pr and userType eq "Employee"

filter=title pr or userType eq "Intern"

filter=
 schemas eq "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User"

filter=userType eq "Employee" and (emails co "example.com" or
  emails.value co "example.org")

filter=userType ne "Employee" and not (emails co "example.com" or
  emails.value co "example.org")

filter=userType eq "Employee" and (emails.type eq "work")

#####Following features are been developed.

#### /Users Endpoint
- Update with PATCH

#### /Me Endpoint
- Update with PATCH
