package com.dijta.common.subtransaction

import be.janbols.spock.extension.dbunit.DbUnit
import com.dijta.common.TestConstants
import com.dijta.common.rest.RestObjectMapper
import com.dijta.common.security.ISecurityContext
import com.dijta.common.security.SecurityPrincipal
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.Lists
import io.restassured.RestAssured
import io.vavr.API
import org.apache.http.HttpStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import javax.sql.DataSource

import static io.restassured.RestAssured.given

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [SubTransactionApp])
@ActiveProfiles("tenant")
class SubTransactionSpec extends Specification implements TestConstants {

    @Autowired
    PersonEntityRepository personEntityRepository

    @Autowired
    PersonEntityService personEntityService

    @Autowired
    ISecurityContext iSecurityContext

    @Autowired
    private ObjectMapper objectMapper

    @Autowired
    DataSource dataSource

    def ram = new PersonEntityDto(name: "Ram")
    def ganesan = new PersonEntityDto(name: "Ganesan")
    def vijay = new PersonEntityDto(name: "Vijay")

    @LocalServerPort
    private int localServerPort

    def setup() {
        iSecurityContext.principal = SecurityPrincipal
                .builder()
                .id(1L)
                .name("Test User")
                .tenantId(1L)
                .privileges(API.Set('DIJTA_APP_READ'))
                .build()
        RestAssured.port = localServerPort
        //personEntityService.saveAll(Lists.newArrayList(ram/*, ganesan, vijay*/))

    }

    @DbUnit
    def content = {
        dijta_id(sequence_name: "person_entity.pk_id", next_val: 1)
        dijta_id(sequence_name: "person_entity.sequence", next_val: 1005)
        dijta_id(sequence_name: "address_entity.pk_id", next_val: 1)
        dijta_id(sequence_name: "address_entity.sequence", next_val: 1010)
        person_entity(pk_id: 0, name: "Ram", sequence: 1000)
        person_entity(pk_id: 1, name: "Ram", sequence: 1001)
        address_entity(pk_id: 0, name: "Chennai", parent_pk_id: 0, sequence: 1000)
    }

    def cleanup() {
        personEntityRepository.deleteAll()
        RestAssured.reset()
    }

    def "save and Update ChildEntity"() {
        given:

        def personEntityDto = personEntityRepository.findById(1L) as Optional<PersonEntityDto>;

        def token = asJwtWithTenant('DIJTA_APP_WRITE', 1L)
        AddressEntityDto addressEntityDto = new AddressEntityDto(name: "Address One")

        personEntityDto.get().addressEntityList = Lists.newArrayList(addressEntityDto);

        when:
        def response = given().contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .body(objectMapper.writeValueAsString(personEntityDto))
                .post("/person-entity")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()

        then:
        def list = RestObjectMapper.INSTANCE.readValue(
                response, new TypeReference<PersonEntityDto>() {
        })

        println("PersonEntityDto.sequence..................." + list.getSequence())
        //TODO check the change with ganesan

        list.addressEntityList.forEach({ it ->
            it.sequence
            println(".......................................................Address.sequence" + it.sequence)
        })

    }

}
