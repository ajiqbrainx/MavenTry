package com.dijta.common.dijtasequence.transaction

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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [UserApp])
@ActiveProfiles("tenant")
class UserSpec extends Specification implements TestConstants {


    @Autowired
    private final UserRepository repository
    @Autowired
    private final IUserService service

    def ramTown = new TownDto(code: "ARANDKI")
    def ramAddress = new AddressDto(city: "NewYork", pin: 22222222)
    def ramContact = new ContactDto(email: "ram@gmail.com")
    def ramDto = new UserDto(name: "Ram")

    def samTown = new TownDto(code: "KKI")
    def samAddress = new AddressDto(city: "Madurai", pin: 610333)
    def samContact = new ContactDto(email: "sssam@gmail.com")
    def samDto = new UserDto(name: "SSam")

    def tamTown = new TownEntity(code: "TTTTI")
    def tamAddress = new AddressEntity(city: "Ntttt", pin: 22255555)
    def tamContact = new ContactEntity(email: "tttttam@gmail.com")
    def tam = new UserEntity(name: "TTTTTam")

    def lamTown = new TownEntity(code: "LLLLLLLIIII")
    def lamAddress = new AddressEntity(city: "Maduraifgggg", pin: 6145555)
    def lamContact = new ContactEntity(email: "llllllam@gmail.com")
    def lam = new UserEntity(name: "LLKKLLLLLLam")

    @LocalServerPort
    private int localServerPort

    def setup() {
        RestAssured.port = localServerPort
        ramAddress.setTownList([ramTown])
        ramDto.address = ramAddress
        ramDto.setContactList([ramContact])

        samAddress.setTownList([samTown])
        samDto.address = samAddress
        samDto.setContactList([samContact])

        tamAddress.setTownList([tamTown])
        tam.address = tamAddress
        tam.setContactList([tamContact])

        lamAddress.setTownList([lamTown])
        lam.address = lamAddress
        lam.setContactList([lamContact])

        List list = Lists.newArrayList(ramDto, samDto);
        List listEntity = Lists.newArrayList(tam, lam);
        list.stream().forEach { println(".................Before save of UserEntity: name=" + it.name + ",sequence=" + it.sequence + "; AddressEntity: city=" + it.address.city + ", pin=" + it.address.pin + ", sequence=" + it.address.sequence) }
        list.stream().map { service.save(it) }
                .forEach { println(".................After save of UserEntity: name=" + it.name + ",sequence=" + it.sequence + "; AddressEntity: city=" + it.address.city + ", pin=" + it.address.pin + ", sequence=" + it.address.sequence) }
        listEntity.stream().forEach { println(".................Before save of UserEntity: name=" + it.name + ",sequence=" + it.sequence + "; AddressEntity: city=" + it.address.city + ", pin=" + it.address.pin + ", sequence=" + it.address.sequence) }
        listEntity.stream().map { service.save(it) }
                .forEach { println(".................After save of UserEntity: name=" + it.name + ",sequence=" + it.sequence + "; AddressEntity: city=" + it.address.city + ", pin=" + it.address.pin + ", sequence=" + it.address.sequence) }
    }

    def cleanup() {
        repository.deleteAll()
        RestAssured.reset()
    }

    def "validate DijtaSequence Transactional case using Entity Directly"() {
        given:
        def token = asJwtWithTenant('DIJTA_APP_READ', 1L)

        when:
        def response = given()
                .contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .get("/dijta-sequence-transaction-with-entity")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()

        then:
        def list = RestObjectMapper.INSTANCE.readValue(response, new TypeReference<ImmutableList<UserEntity>>() {
        })
        list.stream().forEach({ user -> println("..................After get of UserEntity: name=" + user.name + ",sequence=" + user.sequence + "; AddressEntity: city=" + user.address.city + ", pin=" + user.address.pin + ", sequence=" + user.address.sequence) })

        list.size() == 4
    }

    def "validate DijtaSequence Transactional case using DTO"() {
        given:
        def token = asJwtWithTenant('DIJTA_APP_READ', 1L)
        TownDto ganesanTown = new TownDto(code: "NAGI")
        TownDto ganesanTown2 = new TownDto(code: "VELGANI")
        AddressDto ganesanAddress = new AddressDto(city: "Nagai", pin: 620115)
        ganesanAddress.townList = [ganesanTown, ganesanTown2]
        ContactDto ganesanContact = new ContactDto(email: "ganesan@email.com")
        UserDto ganesanDto = new UserDto(name: "Ganesan")
        ganesanDto.setAddress(ganesanAddress)
        ganesanDto.contactList = [ganesanContact]

        TownDto vijayTown = new TownDto(code: "PORUR")
        AddressDto vijayAddress = new AddressDto(city: "Chennai", pin: 600015)
        vijayAddress.townList = [vijayTown]
        ContactDto vijayContact = new ContactDto(email: "vijay")
        UserDto vijayDto = new UserDto(name: "Vijay")
        vijayDto.setAddress(vijayAddress)
        vijayDto.contactList = [vijayContact]

        when:
        List list1 = Lists.newArrayList(ramDto, ganesanDto, vijayDto);
        list1.stream().forEach { println(".................Before save of UserDto: name=" + it.name + ",sequence=" + it.sequence + "; AddressDto: city=" + it.address.city + ", pin=" + it.address.pin + ", sequence=" + it.address.sequence) }
        then:
        list1.stream().map { service.saveDto(it) }
                .forEach { println(".................After save of UserDto: name=" + it.name + ",sequence=" + it.sequence + "; AddressDto: city=" + it.address.city + ", pin=" + it.address.pin + ", address.sequence=" + it.address.sequence + "; townList.townId=" + it.address.townList.get(0).townId + ", townList.code=" + it.address.townList.get(0).code  + "; Contact.contactId=" + it.contactList.get(0).contactId + ", email=" + it.contactList.get(0).email) }

        when:
        def response = given()
                .contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .get("/dijta-sequence-transaction-with-dto-child")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()

        then:
        def list2 = RestObjectMapper.INSTANCE.readValue(response, new TypeReference<ImmutableList<UserDto>>() {
        })
        list2.stream().forEach({
            user -> println("..................After get of UserDto: name=" + user.name + ",sequence=" + user.sequence + ",pkId=" + user.pkId + ";")
                if (user.address != null) {
                    println("AddressDto: city=" + user.address.city + ", pin=" + user.address.pin + ", sequence=" + user.address.sequence + ",pkId=" + user.address.pkId + ";")
                }
                if (user.address.townList != null && user.address.townList[0] != null) {
                    println("TownDto: pkId=" + user.address.townList[0].pkId + ", townId=" + user.address.townList[0].townId + ", code=" + user.address.townList[0].code)
                }
                if (user.contactList != null && user.contactList[0] != null) {
                    println("ContactDto: pkId=" + user.contactList[0].pkId + ", email=" + user.contactList[0].email + ", contactId=" + user.contactList[0].contactId + ";")
                }
        })

        //list2.size() == 6
    }


}
