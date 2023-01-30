package com.dijta.common.multitenant

import be.janbols.spock.extension.dbunit.DbUnit
import com.dijta.common.EnableDijtaBase
import com.dijta.common.TestConstants
import com.dijta.common.identity.BaseEntityController
import com.dijta.common.identity.DijtaConverters
import com.dijta.common.security.ISecurityContext
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.json.JsonSlurper
import io.restassured.RestAssured
import org.apache.http.HttpStatus
import org.dozer.DozerBeanMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import spock.lang.Ignore
import spock.lang.Specification

import javax.sql.DataSource

import static io.restassured.RestAssured.given

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [MultitenantApp])
@ActiveProfiles("local")
class MultiTenantGeneratorSpec extends Specification implements TestConstants {

    @LocalServerPort
    private int localServerPort

    def setup() {
        RestAssured.port = localServerPort
    }

    def cleanup() {
        RestAssured.reset()
    }

    @DbUnit
    def content = {
        dijta_id(sequence_name: "employee.pk_id", next_val: 5)
        Employee(pk_id: 1, name: "Vasanthraj", tenant_id: 1, created_by: 1, created_at: '[NOW]', changed_by: 1, changed_at: '[NOW]', is_active: 0)
        Employee(pk_id: 2, name: "Ganesan", tenant_id: 1, created_by: 1, created_at: '[NOW]', changed_by: 1, changed_at: '[NOW]',is_active: 1)
        Employee(pk_id: 3, name: "Baskar", tenant_id: 1, created_by: 1, created_at: '[NOW]', changed_by: 1, changed_at: '[NOW]', is_active: 0)
        Employee(pk_id: 4, name: "Arun", tenant_id: 2, created_by: 2, created_at: '[NOW]', changed_by: 2, changed_at: '[NOW]', is_active: 1)
        Employee(pk_id: 5, name: "Vijay", tenant_id: 2, created_by: 2, created_at: '[NOW]', changed_by: 2, changed_at: '[NOW]', is_active: 0)

        dijta_id(sequence_name: "address.pk_id", next_val: 205)
        Address(pk_id: 1, city: "Chennai", district: "chennai")
        Address(pk_id: 2, city: "kkdi", district: "Sivagangai")

        dijta_id(sequence_name: "country.pk_id", next_val: 5)
        Country(pk_id: 1, code: "IN")
        Country(pk_id: 2, code: "US")
        Country(pk_id: 3, code: "AU")

        dijta_id(sequence_name: "contact.pk_id", next_val: 5)
        Contact(pk_id: 1, mobile_number: 95, email: "abc1@abc.co.in")
        Contact(pk_id: 2, mobile_number: 99, email: "abc2@abc.co.in")
    }

    @Autowired
    DataSource dataSource

    @Autowired
    private ObjectMapper objectMapper

    def 'Test for MappedParentRef with Employee having Address and Contact'() {
        given:
        def token = asJwtWithTenant('DIJTA_APP_WRITE', 1L)

        CountryDto countryDto = new CountryDto()
        countryDto.code = "IN"

        AddressDto addressDto = new AddressDto()
        addressDto.city = "asasa"
        addressDto.district = "bbbb"
        addressDto.country = [countryDto]

        ContactDto contactDto = new ContactDto()
        contactDto.mobileNumber = 9922233300L
        contactDto.email = "abcabc@abc.com"

        EmployeeDTO employeeDTO = new EmployeeDTO()
        employeeDTO.name = "Raj"
        employeeDTO.address = [addressDto]
        employeeDTO.contact = contactDto

        expect:
        def response = given()
                .contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .body(objectMapper.writeValueAsString(employeeDTO))
                .post("/multitenant_employees")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()
        def employeeResult = objectMapper.readValue(response, EmployeeDTO.class)
        employeeResult
        employeeResult.address != null
        employeeResult.contact != null
        employeeResult.contact.email == "abcabc@abc.com"
        employeeResult.address[0].country != null
        employeeResult.address[0].country[0].code == "IN"
        //employeeResult.address[0].city = "changed city"

        and:
        def getResponse = given()
                .contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .get("/multitenant_employees/" + employeeResult.pkId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()

        def response1 = objectMapper.readValue(getResponse, EmployeeDTO.class)
        when:
        response1.address[0].city = "changed city"

        def updatedResponse = given()
                .contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .body(objectMapper.writeValueAsString(response1))
                .put("/multitenant_employees")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()
        def updatedEmployeeResult = objectMapper.readValue(response, EmployeeDTO.class)
        then:
        updatedEmployeeResult.address[0].city == "changed city"

    }

    def 'test multitenant with authorized and secured key'() {
        given:
        def token = asJwtWithTenant('DIJTA_APP_WRITE', 1L)
        Employee employee = new Employee(name: "Ganesan")
        employee.tenantId = 1L
        expect:
        def response = given()
                .contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .body(objectMapper.writeValueAsString(employee))
                .post("/multitenant_employees")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()
        def employeeResult = objectMapper.readValue(response, EmployeeDTO.class)
        employeeResult
    }

    def 'test saveAll multitenant with authorized and secured key'() {
        given:
        def token = asJwtWithTenant('DIJTA_APP_WRITE', 1L)

        List<EmployeeDTO> employeeDTOList = new ArrayList<>();
        EmployeeDTO employee1 = new EmployeeDTO(name: "Anand")
        employeeDTOList.add(employee1)
        EmployeeDTO employee2 = new EmployeeDTO(name: "Sudheer")
        employeeDTOList.add(employee2)

        expect:
        def response = given()
            .contentType("application/json")
            .when()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .body(objectMapper.writeValueAsString(employeeDTOList))
            .post("/multitenant_employees/saveAll")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .asString()
        def employeeList = objectMapper.readValue(response, List.class)
        employeeList.size() == 2
    }

    def 'test getAll for the Employee with auditing'() {
        given:
        final Employee employee = new Employee(name: "Vasanthraj")
        def token = asJwtWithTenant('DIJTA_APP_WRITE', 1L)
        expect:
        def response = given().contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .get("/multitenant_employees")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()

        def responseEmployee = new JsonSlurper().parse(response.bytes)
        responseEmployee.content[0].name == "Vasanthraj"
        responseEmployee.content[0].createdAt
        responseEmployee.content[0].tenantId == 1

    }

    def 'test findById with value'() {
        given: "prepare  employee object to persist"
        def token = asJwtWithTenant('DIJTA_APP_READ', 1L)
        Long perId = 1L

        when:
        def response =
                given()
                        .contentType("application/json")
                        .when()
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                        .get("/multitenant_employees/" + perId)
                        .then()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .asString()

        then:
        def employeeRes = objectMapper.readValue(response, EmployeeDTO.class)
        employeeRes.pkId == perId
    }

    def 'test findById not available'() {
        def token = asJwtWithTenant('DIJTA_APP_READ', 1L)
        given: "prepare a id which is not present"
        Long perId = 987651L

        expect:
        def response = given().contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .get("/multitenant_employees/" + perId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()

        def employee = new JsonSlurper().parse(response.bytes)
        employee == null
    }

    def 'test save with value'() {
        def token = asJwtWithTenant('DIJTA_APP_WRITE', 1L)
        given: "prepare a employee which needs to be saved"
        final Employee employee = new Employee(name: "Custom Name")
        employee.tenantId = 1
        expect:
        def response = given().contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .body(objectMapper.writeValueAsString(employee))
                .post("/multitenant_employees")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()

        def employees = new JsonSlurper().parse(response.bytes)
        employees.name == "Custom Name"
        employees.pkId != null
    }

    def 'test getById with value'() {
        def token = asJwtWithTenant('DIJTA_APP_READ', 1L)
        given: "prepare employee object to persist"
        Long pkId = 1L;
        expect:
        def responseGet = given().contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .get("/multitenant_employees/" + pkId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()

        def employees = new JsonSlurper().parse(responseGet.bytes)
        employees.pkId == 1L
        employees.name == "Vasanthraj"
    }

    def 'test delete with Invalid values'() {
        def token = asJwtWithTenant('DIJTA_APP_DELETE', 1L)
        def readToken = asJwtWithTenant('DIJTA_APP_READ', 1L)
        given: "prepare employee object to persist"
        final Employee employee = new Employee(name: "Anand Chintam")
        employee.setPkId(91)
        employee.setTenantId(1)
        expect:
        def responseDel = given().contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .body(objectMapper.writeValueAsString(employee))
                .delete("/multitenant_employees")
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .extract()
                .asString()
        def result = new JsonSlurper().parse(responseDel.getBytes())
        result.errors[0].field == "pkId"
        result.errors[0].defaultMessage == "Request Id Not Found"
    }


    def 'test update with valid'() {
        given: "prepare employee to save"
        def token = asJwtWithTenant('DIJTA_APP_WRITE', 1L)
        final Employee employee = new Employee(name: "Employee")
        employee.tenantId = 1
        employee.name = "Employee Update"
        employee.pkId = 1
        expect:
        def response = given().contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .body(objectMapper.writeValueAsString(employee))
                .put("/multitenant_employees")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()

        def employees = new JsonSlurper().parse(response.bytes)
        employees.name == "Employee Update"
    }

    @Ignore// Case to be corrected
    def 'test update with invalid tenantId'() {
        given: "prepare employee to save"
        def token = asJwtWithTenant('DIJTA_APP_WRITE', 1L)
        final Employee employee = new Employee(name: "Employee")
        employee.tenantId = 2
        employee.name = "Employee Update"
        employee.pkId = 1
        expect:
        def response = given().contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .body(objectMapper.writeValueAsString(employee))
                .put("/multitenant_employees")
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .extract()
                .asString()

        def result = new JsonSlurper().parse(response.getBytes())
        result.errors[0].code == "Forbidden Action"
        result.errors[0].type == "ERROR"
        result.errors[0].params[0].defaultValue == "Requested Tenant user can't perform the action intended"
    }


    def 'test update with invalid tenantId in token'() {

        given: "prepare employee to save"
        def token = asJwtWithTenant('DIJTA_APP_WRITE', 2L)
        final Employee employee = new Employee(name: "Employee")
        employee.tenantId = 1
        employee.name = "Employee Update"
        employee.pkId = 1L
        expect:
        def response = given().contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .body(objectMapper.writeValueAsString(employee))
                .put("/multitenant_employees")
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .extract()
                .asString()

        def result = new JsonSlurper().parse(response.getBytes())
        result.errors[0].code == "DJCOMM-FRBDN-ACTN"
        result.errors[0].defaultMessage == "Forbidden Action: {}"
        result.errors[0].type == "ERROR"
        result.errors[0].params[0].defaultValue == "Requested Tenant user can't perform the action intended"
    }

    def 'test delete by id valid'() {
        def token = asJwtWithTenant('DIJTA_APP_DELETE', 1L)
        given: "prepare employee to persist"
        Long pkId = 1L
        expect:
        def response = given().contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .delete("/multitenant_employees/" + pkId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()
    }

    def 'test delete all by id valid'() {
        def token = asJwtWithTenant('DIJTA_APP_DELETE', 1L)
        given: "prepare employee to persist"
        Long pkId = 1L
        expect:
        def response = given().contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .body(objectMapper.writeValueAsString(Arrays.asList(pkId)))
                .delete("multitenant_employees/all")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()
    }

    def 'test delete by id invalid'() {
        def token = asJwtWithTenant('DIJTA_APP_DELETE', 1L)
        given: "prepare a id which is not present"
        Long empId = 987651L
        when:
        def response = given().contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .delete("/multitenant_employees/" + empId)
                .then()
                .statusCode(404)
                .extract()
                .asString()
        then:
        def result = new JsonSlurper().parse(response.getBytes())
        result.errors[0].code == "NOT_FOUND"
        result.errors[0].defaultMessage == "Not Found: {}"
        result.errors[0].type == "ERROR"
    }

}

interface EmployeeRepository extends MultiTenantEntityRepository<Employee> {}

interface IEmployeeService extends MultiTenantEntityService<EmployeeDTO, Employee> {}

@Service
class IEmployeeServiceImpl extends AbstractMultiTenantEntityService<EmployeeDTO, Employee> implements IEmployeeService {

    protected IEmployeeServiceImpl(EmployeeRepository repository,
                                   ISecurityContext securityContext,
                                   DozerBeanMapper mapper) {
        super(repository, DijtaConverters.converter(mapper, EmployeeDTO.class, Employee.class), securityContext)
    }

    @Override
    protected Employee getEntityForExample() {
        return new Employee()
    }
}

@RestController
@RequestMapping("/multitenant_employees")
class MultiTenantEmployeeController extends BaseEntityController<EmployeeDTO, Employee> {
    protected MultiTenantEmployeeController(IEmployeeService service, ISecurityContext securityContext) {
        super(service, securityContext, buildHttpSecurityPrivilege())
    }

    private static Map<HttpMethod, String> buildHttpSecurityPrivilege() {
        final HashMap<HttpMethod, String> map = new HashMap<>()
        map[HttpMethod.GET] = "hasAnyAuthority('DIJTA_APP_READ', 'DIJTA_APP_WRITE')"
        map[HttpMethod.POST] = "hasAnyAuthority('DIJTA_APP_WRITE')"
        map[HttpMethod.PUT] = "hasAnyAuthority('DIJTA_APP_WRITE')"
        map[HttpMethod.DELETE] = "hasAnyAuthority('DIJTA_APP_READ','DIJTA_APP_DELETE')"
        return map
    }
}

@SpringBootApplication
@EnableJpaRepositories
@EnableDijtaBase
class MultitenantApp {
    static void main(String[] args) {
        SpringApplication.run(MultitenantApp.class, args)
    }
}
