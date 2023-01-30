package com.dijta.common.additionalfilter

import be.janbols.spock.extension.dbunit.DbUnit
import com.dijta.common.TestConstants
import com.dijta.common.rest.RestObjectMapper
import com.dijta.common.security.ISecurityContext
import com.dijta.common.security.SecurityPrincipal
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.ImmutableList
import com.google.common.collect.Lists
import io.restassured.RestAssured
import io.vavr.API
import org.apache.http.HttpStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.security.core.context.SecurityContext
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import javax.sql.DataSource

import static io.restassured.RestAssured.given

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [MultiTenantAdditionalFilterApp])
@ActiveProfiles("tenant")
class MultiTenantAdditionalFilterSpec extends Specification implements TestConstants {

/*
    @Autowired
    private final MultiTenantAdditionalFilterRepository repository

    @Autowired
    private final MultiTenantAdditionalFilterService service
*/

    /*def ram = new PersonEntity(name: "Ram", tenantId: 1L)
    def ganesan = new PersonEntity(name: "Ganesan", tenantId: 1L)
    def vijay = new PersonEntity(name: "Vijay", tenantId: 2L)*/

    @LocalServerPort
    private int localServerPort

    @Autowired
    DataSource dataSource

    def setup() {
        RestAssured.port = localServerPort
    }

    def cleanup() {
        RestAssured.reset()
    }

    @DbUnit
    def content = {
        dijta_id(sequence_name: "PersonEntity.pk_id", next_val: 3)
        PERSON_ENTITY(pk_id: 1, name: "Ram", tenant_id: 1, created_by: 1, created_at: '[NOW]', changed_by: 1, changed_at: '[NOW]')
        PERSON_ENTITY(pk_id: 2, name: "Ganesan", tenant_id: 1, created_by: 1, created_at: '[NOW]', changed_by: 1, changed_at: '[NOW]')
        PERSON_ENTITY(pk_id: 3, name: "Vijay", tenant_id: 2, created_by: 1, created_at: '[NOW]', changed_by: 1, changed_at: '[NOW]')
    }

    def "validate additional pkId In two filters"() {
        given:
        def token = asJwtWithTenant('DIJTA_APP_READ', 1L)

        when:
        def response = given().contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .get("/multitenant-additional-filter-in?search=pkId:in:[\"1\",\"2\"]")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()

        then:
        println("response:" + response)
        def list = RestObjectMapper.INSTANCE.readValue(response, new TypeReference<ImmutableList<PersonEntity>>() {
        })
        println("list:" + list)
        //TODO check the change with ganesan
        list.size() == 2

    }

    def "validate additional tenant filter"() {
        given:
        def token = asJwtWithTenant('DIJTA_APP_READ', 1L)

        when:
        def response = given().contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .get("/multitenant-additional-filter")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()

        then:
        def list = RestObjectMapper.INSTANCE.readValue(response, new TypeReference<ImmutableList<PersonEntity>>() {
        })

        //TODO check the change with ganesan
        list.size() == 2
        
    }

    def "validate additional tenant In filter"() {
        given:
        def token = asJwtWithTenant('DIJTA_APP_READ', 1L)

        when:
        def response = given().contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .get("/multitenant-additional-filter-in?search=name:in:[\"Ram\",\"Vijay\"]")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()

        then:
        println("response:" + response)
        def list = RestObjectMapper.INSTANCE.readValue(response, new TypeReference<ImmutableList<PersonEntity>>() {
        })
        println("list:" + list)
        //TODO check the change with ganesan
        list.size() == 1

    }

    def "validate additional tenant In two filters"() {
        given:
        def token = asJwtWithTenant('DIJTA_APP_READ', 1L)

        when:
        def response = given().contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .get("/multitenant-additional-filter-in?search=name:in:[\"Ram\",\"Ganesan\"]")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()

        then:
        println("response:" + response)
        def list = RestObjectMapper.INSTANCE.readValue(response, new TypeReference<ImmutableList<PersonEntity>>() {
        })
        println("list:" + list)
        //TODO check the change with ganesan
        list.size() == 2

    }

}



