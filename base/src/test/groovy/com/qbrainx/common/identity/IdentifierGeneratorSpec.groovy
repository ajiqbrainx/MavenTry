package com.dijta.common.identity

import com.dijta.common.EnableDijtaBase
import com.dijta.common.TestConstants
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
import org.springframework.data.domain.Page
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import spock.lang.Ignore
import spock.lang.Specification

import javax.transaction.Transactional

import static io.restassured.RestAssured.given

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = IdentifierGeneratorApp)
@ActiveProfiles("local")
class IdentifierGeneratorSpec extends Specification implements TestConstants {


    @LocalServerPort
    private int localServerPort

    def setup() {
        RestAssured.port = localServerPort
        employeeRepository.deleteAll()
    }

    def cleanup() {
        RestAssured.reset()
    }

    @Autowired
    private ObjectMapper objectMapper

    @Autowired
    private EmployeeRepository employeeRepository


    def "validate the id generator for the employee table"() {
        given: "prepare person object to persist"
        final EmployeeEntity employee = new EmployeeEntity(name: "Ganesan")

        when: "persist the person in db"
        final EmployeeEntity newEmployee = employeeRepository.save(employee)

        then: "verify the persisted person information"
        newEmployee
        newEmployee.pkId == 1
        newEmployee.sequence == 1
        newEmployee.name == 'Ganesan'

        when: "save second employee"
        def ram = employeeRepository.save(new EmployeeEntity(name: "Ram"))

        then: "verify second employee "
        ram
        ram.pkId == 2L
        ram.sequence == 2L
        ram.name == "Ram"
    }

    def 'test base controller, base service, base repo'() {
        given:
        def token = asJwt('DIJTA_APP_WRITE')
        employeeRepository.deleteAll()
        employeeRepository.save(new EmployeeEntity(name: "Ganesan"))

        expect:
        def response = given()
                .contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .get("/identity_employees")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()

        def employees = new JsonSlurper().parse(response.bytes)
        employees.content.size() == 1
        employees.content[0].pkId
        employees.content[0].sequence >= 1
        employees.content[0].name == 'Ganesan'
    }

    def 'test base controller, base service, base repo without enough previlege'() {
        given:
        def token = asJwt('UNSECURED')

        expect:
        given()
                .contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .get("/identity_employees")
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN)
    }

    def 'test base controller, base service, base repo without token'() {
        expect:
        given()
                .contentType("application/json")
                .when()
                .get("/identity_employees")
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
    }

    def 'test findById with value'() {

        def token = asJwt('DIJTA_APP_WRITE')

        given: "prepare person object to persist"
        final EmployeeEntity employee = new EmployeeEntity(name: "Vasanth")

        when: "persist the person in db"
        final EmployeeEntity newEmployee = employeeRepository.save(employee)
        Long empId = newEmployee.pkId

        then:
        def response = given().contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .get("/identity_employees/" + empId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()

        def employeeJson = objectMapper.readValue(response, EmployeeDTO.class)
        employeeJson.pkId == empId
    }

    def 'test findById not available'() {
        def token = asJwt('DIJTA_APP_WRITE')
        given: "prepare a id which is not present"
        Long empId = 987651L
        expect:
        def response = given().contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .get("/identity_employees/" + empId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()

        def employees = new JsonSlurper().parse(response.bytes)
        employees == null
    }

    def 'test save with value'() {
        def token = asJwt('DIJTA_APP_WRITE')
        given: "prepare a employee which needs to be saved"
        final EmployeeEntity employee = new EmployeeEntity(name: "Vasanth")

        expect:
        def response = given().contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .body(objectMapper.writeValueAsString(employee))
                .post("/identity_employees")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()

        def employeeJson = objectMapper.readValue(response, EmployeeDTO.class)
        employeeJson.pkId
        employeeJson.sequence
        employeeJson.name == "Vasanth"
    }

    def 'test saveAll with values'() {
        def token = asJwt('DIJTA_APP_WRITE')
        given: "prepare a employee list which needs to be saved"
        List<EmployeeDTO> employeeDTOList = new ArrayList<>();
        final EmployeeDTO employee1 = new EmployeeDTO(name: "Anand")
        employeeDTOList.add(employee1)
        final EmployeeDTO employee2 = new EmployeeDTO(name: "Sudheer")
        employeeDTOList.add(employee2)

        expect:
        def response = given().contentType("application/json")
            .when()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .body(objectMapper.writeValueAsString(employeeDTOList))
            .post("/identity_employees/saveAll")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .asString()

        def employeeList = objectMapper.readValue(response, List.class)

        employeeList.size() == 2
        EmployeeDTO employeeDTO1 = employeeList.get(0)
        employeeDTO1.pkId
        employeeDTO1.sequence
        employeeDTO1.name == "Anand"

        EmployeeDTO employeeDTO2 = employeeList.get(1)
        employeeDTO2.pkId
        employeeDTO2.sequence
        employeeDTO2.name == "Sudheer"

    }

    @Ignore //TODO to be corrected.
    def 'test delete with value'() {
        def deltoken = asJwt('DIJTA_APP_DELETE')
        def readtoken = asJwt('DIJTA_APP_WRITE')
        given: "prepare person object to persist"
        final EmployeeEntity employee = new EmployeeEntity(name: "Vasanth")

        when: "persist the person in db"
        final EmployeeEntity newEmployee = employeeRepository.save(employee)

        then:
        def responseDel = given().contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $deltoken")
                .body(objectMapper.writeValueAsString(newEmployee))
                .delete("/identity_employees")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()

        def responseGet = given().contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $readtoken")
                .get("/identity_employees/" + newEmployee.pkId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()

        def employees = new JsonSlurper().parse(responseGet.getBytes())
        employees.deletedAt
    }

    def 'test update with valid'() {
        def puttoken = asJwt('DIJTA_APP_WRITE')
        def readtoken = asJwt('DIJTA_APP_WRITE')
        given: "prepare employee to save"
        final EmployeeEntity employee = new EmployeeEntity(name: "Employee")
        employee.lastName = "Vasanthraj"

        when: "persist employee in db"
        EmployeeEntity newEmployee = employeeRepository.save(employee)
        newEmployee.setName("Employee Update")

        then:
        def response = given().contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $puttoken")
                .body(objectMapper.writeValueAsString(newEmployee))
                .put("/identity_employees")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()

        def responseGet = given().contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $readtoken")
                .get("/identity_employees/" + newEmployee.pkId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()

        def employees = new JsonSlurper().parse(responseGet.getBytes())
        employees.name == "Employee Update"
        employees.lastName == "Vasanthraj"

    }

    def 'test delete by id valid'() {
        def token = asJwt('DIJTA_APP_DELETE')

        given: "prepare employee to persist"
        EmployeeEntity employee = new EmployeeEntity(name: "Delete Name")

        when: "persist employee in db"
        EmployeeEntity delEmployee = employeeRepository.save(employee)

        then:
        def response = given().contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .delete("/identity_employees/" + delEmployee.pkId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()

    }

    def 'test delete by id invalid'() {
        def token = asJwt('DIJTA_APP_DELETE')
        given: "prepare a id which is not present"
        Long empId = 987651L
        expect:
        def response = given().contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .delete("/identity_employees/" + empId)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .extract()
                .asString()
    }

    def 'Get valid user with pagination parameters'() {
        given:
        def token = asJwt('DIJTA_APP_WRITE')
        EmployeeEntity employee = new EmployeeEntity(name: "Vasanth")
        EmployeeEntity newEmployee = employeeRepository.save(employee)
        EmployeeEntity employee1 = new EmployeeEntity(name: "Lakshmanan")
        EmployeeEntity newEmployee1 = employeeRepository.save(employee1)
        when:
        def response = given()
                .contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .get("/identity_employees?pageNo=0&pageSize=2&sortBy=name&sortOrder=asc&search=name:eq:Vasanth")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()
        then:
        def responseMessage = new JsonSlurper().parse(response.getBytes())
        responseMessage
        responseMessage.content[0].pkId
    }

    def 'Get valid user search'() {
        given:
        def token = asJwt('DIJTA_APP_WRITE')

        EmployeeEntity employee = new EmployeeEntity(name: "Vasanth")
        employeeRepository.save(employee)

        EmployeeEntity employee1 = new EmployeeEntity(name: "Lakshmanan")
        employeeRepository.save(employee1)
        when:
        def response = given()
                .contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .get("/identity_employees?search=name:eq:Vasanth")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()
        then:
        def responseMessage = new JsonSlurper().parse(response.getBytes())
        responseMessage != null
        responseMessage.content[0].name == "Vasanth"
    }

    def 'Get valid user search using  pkId'() {
        given:
        def token = asJwt('DIJTA_APP_WRITE')
        EmployeeEntity employee = new EmployeeEntity(name: "Vasanth")
        EmployeeEntity newEmployee = employeeRepository.save(employee)
        EmployeeEntity employee1 = new EmployeeEntity(name: "Lakshmanan")
        EmployeeEntity newEmployee1 = employeeRepository.save(employee1)
        when:
        def response = given()
                .contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .get("/identity_employees?search=pkId:gt:1")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()
        then:
        def responseMessage = new JsonSlurper().parse(response.getBytes())
        responseMessage != null
        responseMessage.content[0].pkId
        responseMessage.content[0].name
    }

    def 'getAll with Pre Authorize'() {
        given:
        def token = asJwt('DIJTA_APP_WRITE_ALL')
        expect:
        def response = given()
                .contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .get("/identity_employees/identity")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()

        def employees = new JsonSlurper().parse(response.bytes)
        employees.content.size() == 0
    }
}

@SpringBootApplication
@EnableDijtaBase
class IdentifierGeneratorApp {

    static void main(String[] args) {
        SpringApplication.run(IdentifierGeneratorApp.class, args)
    }
}

interface EmployeeRepository extends BaseEntityRepository<EmployeeEntity> {}

interface IEmployeeService extends BaseEntityService<EmployeeDTO, EmployeeEntity> {

}

@Service
@Transactional
class IdentityEmployeeService extends AbstractBaseEntityService<EmployeeDTO, EmployeeEntity> implements IEmployeeService {

    IdentityEmployeeService(EmployeeRepository repository, DozerBeanMapper dozerBeanMapper) {
        super(repository, DijtaConverters.converter(dozerBeanMapper, EmployeeDTO.class, EmployeeEntity.class))
    }

}

@RestController
@RequestMapping("/identity_employees")
class IdentityEmployeeController extends BaseEntityController<EmployeeDTO, EmployeeEntity> {

    private IEmployeeService iEmployeeService;

    protected IdentityEmployeeController(IEmployeeService service, ISecurityContext securityContext) {
        super(service, securityContext, buildHttpSecurityPrivilege());
        this.iEmployeeService = service;
    }

    @RequestMapping("/identity")
    @PreAuthorize("hasAuthority('DIJTA_APP_WRITE_ALL')")
    Page<EmployeeEntity> getEmployee() {
        println("PRE AUTHORIZE FAILED");
        return iEmployeeService.getAll(new PaginationRequest());
    }

    private static Map<HttpMethod, String> buildHttpSecurityPrivilege() {
        final HashMap<HttpMethod, String> map = new HashMap<>();
        map[HttpMethod.GET] = "hasAnyAuthority('DIJTA_APP_READ', 'DIJTA_APP_WRITE', 'DIJTA_APP_WRITE_ALL')";
        map[HttpMethod.POST] = "hasAnyAuthority('DIJTA_APP_WRITE')";
        map[HttpMethod.PUT] = "hasAnyAuthority('DIJTA_APP_WRITE')";
        map[HttpMethod.DELETE] = "hasAnyAuthority('DIJTA_APP_READ','DIJTA_APP_DELETE')";
        return map;
    }
}


