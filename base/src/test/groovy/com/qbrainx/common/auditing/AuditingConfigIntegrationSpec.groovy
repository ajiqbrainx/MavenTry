package com.dijta.common.auditing

import com.dijta.common.EnableDijtaBase
import com.dijta.common.TestConstants
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
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.Month
import java.time.format.DateTimeFormatter

import static io.restassured.RestAssured.given

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [AuditingApp])
@ActiveProfiles('local')
class AuditingConfigIntegrationSpec extends Specification implements TestConstants {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    @LocalServerPort
    private int localServerPort

    def setup() {
        RestAssured.port = localServerPort
    }

    def cleanup() {
        RestAssured.reset()
    }

    @Autowired
    private ObjectMapper objectMapper

    @Autowired
    private PersonRepository personRepository

    @Autowired
    private PersonService personService

    def 'test security with authorized and cookies propagated to downstream'() {
        given:
        def token = asJwt('DIJTA_APP_WRITE')

        expect:
        def response = given()
            .contentType("application/json")
            .when()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .body(objectMapper.writeValueAsString(new Person(name: "Ganesan")))
            .post("/persons")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .asString()

        def person = objectMapper.readValue(response, Person.class)
        person.pkId == 1
        person.name == 'Ganesan'
        person.createdAt
        person.createdBy == 1L
        person.changedAt
        person.changedBy == 1L
    }

    def 'test getAll for the persons with auditing'() {
        def token = asJwt('DIJTA_APP_READ')

        given:
        final Person person = new Person(name: "Vasanthraj")
        personRepository.save(person)

        expect:
        def response = given().contentType("application/json")
            .when()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .get("/persons?sortBy=name&search=name:lk:Vasan")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .asString()

        def responsePerson = new JsonSlurper().parse(response.getBytes())
        responsePerson.content[0].size() >= 1
        responsePerson.content[0].name == "Vasanthraj"
        responsePerson.content[0].createdAt
    }

    def 'test findById with value'() {

        given: "prepare  person object to persist"
        def token = asJwt('DIJTA_APP_READ')
        final Person person = new Person(name: "Vasanthraj")

        when: "persist the person in db"
        final Person newPerson = personRepository.save(person)
        Long perId = newPerson.pkId

        then:
        def response = given().contentType("application/json")
            .when()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .get("/persons/" + perId)
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .asString()

        def persons = new JsonSlurper().parse(response.getBytes())
        persons.pkId == perId
    }

    def 'test findById not available'() {
        def token = asJwt('DIJTA_APP_READ')
        given: "prepare a id which is not present"
        Long perId = 987651L

        expect:
        def response = given().contentType("application/json")
            .when()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .get("/persons/" + perId)
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .asString()

        def persons = new JsonSlurper().parse(response.getBytes())
        persons == null
    }

    def 'test save with value'() {
        def token = asJwt('DIJTA_APP_WRITE')
        given: "prepare a person which needs to be saved"
        final Person person = new Person(name: "Custom Name")

        expect:
        def response = given().contentType("application/json")
            .when()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .body(objectMapper.writeValueAsString(person))
            .post("/persons")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .asString()

        def persons = new JsonSlurper().parse(response.getBytes())
        persons.name == "Custom Name"
        persons.pkId != null
    }

    def 'test delete with value'() {
        def token = asJwt('DIJTA_APP_DELETE')
        def readtoken = asJwt('DIJTA_APP_WRITE')
        given: "prepare person object to persist"
        final Person person = new Person(name: "Person to be deleted")

        when: "persist the person in db"
        final Person newPerson = personRepository.save(person)

        then:
        def responseDel = given().contentType("application/json")
            .when()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .body(objectMapper.writeValueAsString(newPerson))
            .delete("/persons")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .asString()

        def responseGet = given().contentType("application/json")
            .when()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $readtoken")
            .get("/persons/" + newPerson.pkId)
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .asString()

        def persons = new JsonSlurper().parse(responseGet.getBytes())
        persons == null
    }

    def 'test update with valid'() {
        def token = asJwt("DIJTA_APP_WRITE")

        given: "prepare employee to save"
        final Person person = new Person(name: "Person")

        when: "persist employee in db"
        Person newPerson = personRepository.save(person)
        newPerson.setName("Person Update")

        then:
        def response = given().contentType("application/json")
            .when()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .body(objectMapper.writeValueAsString(newPerson))
            .put("/persons")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .asString()

        def responseGet = given().contentType("application/json")
            .when()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .get("/persons/" + newPerson.pkId)
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .asString()

        def persons = new JsonSlurper().parse(responseGet.getBytes())
        persons.name == "Person Update"
    }

    def 'test delete by id valid'() {
        def token = asJwt("DIJTA_APP_DELETE")

        given: "prepare employee to persist"
        Person person = new Person(name: "Delete Name")

        when: "persist employee in db"
        Person delPerson = personRepository.save(person)

        then:
        def response = given().contentType("application/json")
            .when()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .delete("/persons/" + delPerson.pkId)
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
            .delete("/persons/" + empId)
            .then()
            .statusCode(HttpStatus.SC_NOT_FOUND)
            .extract()
            .asString()
        def responseError = new JsonSlurper().parse(response.getBytes());
        responseError.errors[0].code == "ID_NOT_FOUND"
        responseError.errors[0].field == "pkId"
        responseError.errors[0].defaultMessage == "Requested Id Not Found"
    }

    def 'test Dto JsonProperty and JsonIgnore Annotation with valid'() {
        def token = asJwt("DIJTA_APP_WRITE")

        given: "prepare employee to save"
        final PersonDTO person = new PersonDTO(name: "Person", sequence: 25)
        person.deletedAt=LocalDateTime.of(2021, Month.MARCH, 10, 11, 40, 25)
        person.createdAt=LocalDateTime.of(2021, Month.FEBRUARY, 15, 18, 20, 55)
        person.changedBy=3
        person.changedAt=LocalDateTime.of(2021, Month.MARCH, 15, 6, 30, 15)
        person.createdBy=5
        def personStr = person.toString()
        println(";;;;personStr"+personStr)

        def personJsonString = objectMapper.writeValueAsString(person)
        //println("....personStr"+personJsonString)
        final PersonDTO personFromJson = objectMapper.readValue(personJsonString, PersonDTO.class);


        expect:
        personFromJson.pkId == null
        personFromJson.createdAt == null
        personFromJson.createdBy == null
        personFromJson.changedAt == null
        personFromJson.changedBy == null
        personFromJson.sequence == 25
        personFromJson.name == "Person"
        personFromJson.deletedAt == null

        when: "persist employee in db"
        PersonDTO newPerson = personService.save(person)
        newPerson.setName("Person Update")
        final String newPersonStr = newPerson.toString()
        //println(";;;;newPersonStr:"+newPersonStr)
        final String newPersonJsonString = objectMapper.writeValueAsString(newPerson)
        //println(".....newPersonJsonString:"+newPersonJsonString)
        //{"pkId":1,"createdAt":"2021-03-11T15:33:36.3309616","createdBy":5,"changedAt":"2021-03-11T15:33:36.3309616","changedBy":3,"name":"Person"}
        final PersonDTO newPersonFromJson = objectMapper.readValue(personJsonString, PersonDTO.class);

        then:
        newPersonFromJson.pkId == null
        newPersonFromJson.createdAt == null
        newPersonFromJson.createdBy == null
        newPersonFromJson.changedAt == null
        newPersonFromJson.changedBy == null
        newPersonFromJson.sequence == 25
        newPersonFromJson.name == "Person"
        newPersonFromJson.deletedAt == null


    }

}


interface PersonRepository extends BaseAuditingEntityRepository<Person> {}

interface PersonService extends BaseAuditingEntityService<PersonDTO, Person> {}

@Service
class PersonServiceImpl extends AbstractBaseAuditingEntityService<PersonDTO, Person> implements PersonService {

    protected PersonServiceImpl(PersonRepository repository, DozerBeanMapper mapper) {
        super(repository, DijtaConverters.converter(mapper, PersonDTO.class, Person.class))
    }
}

@RestController
@RequestMapping("/persons")
class PersonController extends BaseAuditingEntityController<PersonDTO, Person> {

    protected PersonController(PersonService service, ISecurityContext securityContext) {
        super(service, securityContext, buildHttpSecurityPrivilege());
    }

    private static Map<HttpMethod, String> buildHttpSecurityPrivilege() {
        final HashMap<HttpMethod, String> map = new HashMap<>();
        map[HttpMethod.GET] =  "hasAnyAuthority('DIJTA_APP_READ', 'DIJTA_APP_WRITE')";
        map[HttpMethod.POST] = "hasAnyAuthority('DIJTA_APP_WRITE')";
        map[HttpMethod.PUT] = "hasAnyAuthority('DIJTA_APP_WRITE')";
        map[HttpMethod.DELETE] = "hasAnyAuthority('DIJTA_APP_READ','DIJTA_APP_DELETE')";
        return map;
    }
}


@SpringBootApplication
@EnableJpaRepositories
@EnableDijtaBase
class AuditingApp {

    static void main(String[] args) {
        SpringApplication.run(AuditingApp.class, args)
    }
}
