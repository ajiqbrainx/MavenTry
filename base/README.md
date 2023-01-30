# LB_COMM: Base

Common Library 

## Getting Started

These Instructions show how to use the base library class in our project
for 
* Entities 
* CRUD Rest API.

````
Prerequisites 
````
1. You should have added the dependency for base in your project's build.gradle file


#### How to use Identity Entity


Identity: Identity class field will act as a primary key for all the entity, 
all class should extend this class or any of it's subclasses declared below

If you declare any entity class, you need to extend the Identity class
which will have pkId field which will be primary key where it will be used
  * pkId
It will follow a pattern to every table 

We have also defined the repository class for each entity, so when we extend 
Identity table, then we can also extend BaseEntityRepository.java

We have to use security to make our Rest Controller to filter out the resources
based on the JWT Token. We need to use below mentioned annotation in our spring 
boot application.

@EnableDijtaSecurity

Along with other required annotation in spring base main class we need to add @EnableDijtaSecurity


````
Example 

1. public class Employee extends BaseEntity

2. interface EmployeeRepository extends BaseEntityRepository<Employee> [Pass the Entity Class]

3. 
@SpringBootApplication
@EnableJpaRepositories
@EnableDijtaSecurity
class IdentifierGeneratorApp {

    static void main(String[] args) {
        SpringApplication.run(IdentifierGeneratorApp.class, args)
    }
}
```` 
#### How to use Auditing Entity

If we need to have the auditing fields in our entity class
then we need to extend the AuditingEntity.java in your entity class
which itself is child class of Identity Class, so we will have
 * pkId - Primary Key
 * createdBy - User info who created it
 * updatedBy - User info who updated it
 * createdAt - Timestamp which holds created timestamp
 * updatedAt - Timestamp which holds updated timestamp
 
 We have also defined the repository class for each entity, so when we extend 
 Auditing table, then we can also extend AbstractBaseAuditingEntityService.java
 
 
@EnableDijtaAuditing

Along with other required annotation in spring base main class we need to add @EnableDijtaAuditing
 
````
 1. public class Employee extends BaseAuditingEntity
 
 2. interface EmployeeRepository extends AbstractBaseAuditingEntityService<Employee> [Pass the Entity Class]

3. 
@SpringBootApplication
@EnableDijtaAuditing
@EnableJpaRepositories
class IdentifierGeneratorApp {

    static void main(String[] args) {
        SpringApplication.run(IdentifierGeneratorApp.class, args)
    }
}
```` 

#### How to use Multi Tenant Entity

If we need to have multi tenant entity fields in our entity class
then we need to extend the MultiTenant entity class, which is child class
of Auditing and Identity Class

 * pkId - Primary Key
 * createdBy - User info who created it
 * updatedBy - User info who updated it
 * createdAt - Timestamp which holds created timestamp
 * updatedAt - Timestamp which holds updated timestamp
 * tenantId  - tenant Identifier 
 
  
 We have also defined the repository class for each entity, so when we extend 
 MultiTenant table, then we can also extend MultiTenantEntity.java
  
 @EnableDijtaAuditing
  
 Along with other required annotation in spring base main class we need to add @EnableDijtaAuditing
 
````
1. public class Employee extends MultiTenantEntity
 
2. EmployeeRepository extends MultiTenantEntityRepository<Employee>

3.
@SpringBootApplication
@EnableJpaRepositories
@EnableDijtaAuditing
class AuditingApp {

    static void main(String[] args) {
        SpringApplication.run(AuditingApp.class, args)
    }
}

````
 
 ### Using Base Services
 
 Base Library comes with common services, which should be used for the CRUD Operations
 Following is the list of base services which you can use.
 
  * getAll - Get All Records of the entity class from the service class
  * getById - Get a single record by given id from the service class
  * save - Persist the data to the db from the service class
  * delete - Delete the entity from the db from the service class
  * update - Update the entity in the db from the service class
  * deleteById - Delete the record from the entity from the service class

Moreover we follow the DTO Pattern, where every request for an entity, should have DTO, 
our common library does the mapping of DTO to entity, for that common services use the 
Dozer Library. We can use the same in our custom implemetation, wherever we need to perform 
DTO to Entity and Entity to DTO conversion we can use converter present in the common library.

How to Use

````
DozerMapper mapper = new DozerMapper();

DijtaConverters.convert(mapper, DTO.class, ENTITY.class)

````
Use the above mentioned code in your service class, by replacing DTO and Entity class.

If we want to use the common library service classes, then we should extends following class as per need.

1) AbstractBaseEntityService - Use this class to extend when your entity needs only Identity Field for your table.
2) AbstractBaseAuditingEntityService - Use this class to extend when your entity needs Auditing Fields for your table
3) AbstractMultiTenantEntityService - Use this class to extend when your entity needs Multitenant Fields fo your table

Since Service class takes care of our DTO to entity class conversion we should pass DTO and Entity Class to the baseService
which can take care of rest.

If our Entity needs only BaseEntity <PK_ID Column in our table>

1) We need to define the interface for our entity which should extends BaseEntityService
2) We need to provide implementation for our service by implementing the interface defined and extending the \
abstractBaseEntityService class which has the base service class, which we can override, if we need to have our 
business needs.
3) We can pass instance of mapper along with service class constructor which will be forced to implement.

Following is the code examples

````
1. 

public interface EmployeeService extends BaseEntityService<EmployeeDTO, Employee>

public class EmployeeServiceImpl extends AbstractBaseEntityService<EmployeeDTO, Employee>
		implements EmployeeService<EmployeeDTO, Employee> {
    
    protected EmployeeServiceImpl(EmployeeRepository repository, DozerBeanMapper mapper) {
    		super(repository, DijtaConverters.converter(mapper, EmployeeDTO.class, Employee.class));
    
    }
}
````

If our Entity needs only BaseAuditingEntity <PK_ID, CREATED_BY, CREATED_AT, CHANGED_AT, CHANGED_BY>

1) We need to define the interface for our entity which should extends BaseAuditingEntityService
2) We need to provide implementation for our service by implementing the interface defined and also extending the 
   AbstractBaseAuditingEntityService class which has the base service class, which we can override, if we need to have our 
   business needs.
3) We can pass instance of mapper along with service class constructor which will be forced to implement.


Following is the code examples

````
1. 

public interface EmployeeService extends BaseAuditingEntityService<EmployeeDTO, Employee>

public class EmployeeServiceImpl extends AbstractBaseAuditingEntityService<EmployeeDTO, Employee>
		implements EmployeeService<EmployeeDTO, Employee> {
    
    protected EmployeeServiceImpl(EmployeeRepository repository, DozerBeanMapper mapper) {
    		super(repository, DijtaConverters.converter(mapper, EmployeeDTO.class, Employee.class));
    
    }
}
````


If our Entity needs only MultiTenantEntity <PK_ID, CREATED_BY, CREATED_AT, CHANGED_AT, CHANGED_BY, TENANT_ID>

1) We need to define the interface for our entity which should extends MultiTenantEntityService
2) We need to provide implementation for our service by implementing the interface defined and also extending the 
   AbstractMultiTenantEntityService class which has the base service class, which we can override, if we need to have our 
   business needs.
   
   This Service will have SecurityContext, which would be helpful to get the tenant identifier from the request
   payload, since all query depends on the tenant identifier, we need to make sure we use the security context
   which will restrict records based on tenant identifier. 
   
3) We can pass instance of mapper along with service class constructor which will be forced to implement.

Following is the code examples


````

1. 
  public interface EmployeeService extends MultiTenantEntityService<EmployeeDTO, Employee>

  public class EmployeeServiceImpl extends AbstractMultiTenantEntityService<EmployeeDTO, Employee>
   		implements EmployeeService<EmployeeDTO, Employee> {
       
         protected AbstractMultiTenantEntityService(MultiTenantEntityRepository<T> repository, 
                        DozerBeanMapper mapper, 
                        ISecurityContext securityContext) {
               super(repository, DijtaConverters.converter(mapper, EmployeeDTO.class, Employee.class));
         }

       protected EmployeeServiceImpl(EmployeeRepository repository, DozerBeanMapper mapper) {
       		super(repository, DijtaConverters.converter(mapper, EmployeeDTO.class, Employee.class));
       
       }
   }

````

 ### Using Base Controllers

If we need to use above listed service classes, then we should follow the following
need to extend BaseEntityController.java  
 
 Base library comes with following base abstract controller following are the apis 
 
 * getAll - Get All Records of the entity class
 * getById - Get a single record by given id
 * save - Persist the data to the db
 * delete - Delete the entity from the db
 * update - Update the entity in the db 
 * deleteById - Delete the record from the entity

If we need to use above listed api for the basic crud operation happens on a table, then we 
need to extend BaseEntityController.java  
 
````
Example 

@RestController
@RequestMapping("/employees")
public class EmployeeController extends BaseEntityController<EmployeeDTO, Employee> {
    protected EmployeeController(EmployeeService service, ISecurityContext securityContext) {
        super(service, securityContext, "hasAuthority('SECURED')")
    }
}

````

If we need to use above listed api for the basic crud operation happens on a table, then we 
need to extend BaseAuditingEntityController.java  
 
````
Example 

@RestController
@RequestMapping("/employees")
public class EmployeeController extends BaseAuditingEntityController<EmployeeDTO, Employee> {
    protected EmployeeController(EmployeeService service, ISecurityContext securityContext) {
        super(service, securityContext, "hasAuthority('SECURED')")
    }
}
````
If we need to use above listed api for the basic crud operation happens on a table, then we 
need to extend BaseAuditingEntityController.java  
 
````
Example 

@RestController
@RequestMapping("/employees")
public class EmployeeController extends BaseMultiTenantEntityController<EmployeeDTO, Employee> {
    protected EmployeeController(EmployeeService service, ISecurityContext securityContext) {
        super(service, securityContext, "hasAuthority('SECURED')")
    }
}

````

We need to pass following in the constructor block 
1. Service Implementation of the Entity Class
2. SecurityContext with the privilege 


 ### Using Base Exception
 
 We follow the standard response json for all our Exceptions, so common library will have exception handler which we 
 need to imported to all the application which uses the lb_comm jars. We don't need to define any custom exception in our
 project, we can use the exception present in the common library.
 
 Following are the classes which we can use to build the Exception message
    
 1. DijtaException - is a custom exception which internally extends the RuntimeException.
 2. DijtaSystemException - is also a custom exception which intenally extends DijtaException.
 
 We can use 
 1) String, 
 2) MessageCode
 3) MessageParam 
 
 to build the error message in detail.

 
 Ways to throw the DijtaException.
 
````
 public DijtaException(Throwable e, HttpStatus status, MessageCode errorMessage)
 
 public DijtaException(HttpStatus status, MessageCode errorMessage)
 
 public DijtaException(Throwable e, HttpStatus status, String messageCode)

 public DijtaException(HttpStatus status, String messageCode)
 
 public DijtaException(HttpStatus status, String messageCode,  MessageParam... params) 

 public DijtaException(HttpStatus status, String messageCode, String field, String param, String paramDefaultVal)

````

If we want to use the Common Exception Handler, it comes in handy in the @EnableDijtaIdentity, @EnableDijtaAuditing

````

@SpringBootApplication
@EnableJpaRepositories
@EnableDijtaAuditing
public class MultitenantApp {
    public static void main(String[] args) {
        SpringApplication.run(MultitenantApp.class, args);
    }
}
````
Use the below annotation on all the entities, to exclude the deleted entries
Since we follow the soft delete for our application.

@Where(clause = "deleted_at is null")