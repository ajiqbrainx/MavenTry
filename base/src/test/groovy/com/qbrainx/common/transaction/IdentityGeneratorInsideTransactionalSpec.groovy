package com.dijta.common.transaction

import com.dijta.common.TestConstants
import com.dijta.common.rest.RestObjectMapper
import com.fasterxml.jackson.core.type.TypeReference
import com.google.common.collect.ImmutableList
import com.google.common.collect.Lists
import io.restassured.RestAssured
import org.apache.http.HttpStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import static io.restassured.RestAssured.given

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [IdentityGeneratorInsideTransactionalApp])
@ActiveProfiles("tenant")
class IdentityGeneratorInsideTransactionalSpec extends Specification implements TestConstants {

    @Autowired
    private final IdentityGeneratorInsideTransactionalRepository repository
    @Autowired
    private final IdentityGeneratorInsideTransactionalService service

    def ram = new PersonEntity(name: "Ram")
    def ganesan = new PersonEntity(name: "Ganesan")
    def vijay = new PersonEntity(name: "Vijay")

    @LocalServerPort
    private int localServerPort

    def setup() {
        RestAssured.port = localServerPort
        Lists.newArrayList(ram, ganesan, vijay)
                .stream()
                .map { service.save(it) }
                .forEach { println(it) }
    }

    def cleanup() {
        repository.deleteAll()
        RestAssured.reset()
    }

    def "validate additional tenant filter"() {
        given:
        def token = asJwtWithTenant('DIJTA_APP_READ', 1L)

        when:
        def response = given()
                .contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .get("/identity-generator-inside")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()

        then:
        def list = RestObjectMapper.INSTANCE.readValue(response, new TypeReference<ImmutableList<PersonEntity>>() {
        })

        list.size() == 3
    }

}



