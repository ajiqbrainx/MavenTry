package com.dijta.common.mapparent

import com.dijta.common.EnableDijtaBase
import com.dijta.common.TestConstants
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = MappedParentApp)
@ActiveProfiles("local")
class MappedParentSpec extends Specification implements TestConstants {

    @Autowired
    private ObjectMapper objectMapper

    def 'MappedParent DeSerialization'() {
        setup:

        def addressList = new ArrayList()
        def addressListNew = new ArrayList()
        Country country = new Country()
        country.code = "IN"

        Address address1 = new Address(city: "Karaikkudi", district: "Ramnad")
        address1.country = [country]
        addressList.add(address1)
        Country country2 = new Country()
        country2.code = "US"
        Address address2 = new Address(city: "chennai", district: "Chennai")
        address2.country = [country2]
        addressList.add(address2)
        println(".....addressList:" + addressList)


        Person person = new Person(id: 200, firstName: "Ram", lastName: "Kumar")
        person.addressList=addressList
        println(".....person:" + person)
        def personJsonStr = objectMapper.writeValueAsString(person)
        def personNew = objectMapper.readValue(personJsonStr, Person.class)
        println(".....personNew:" + personNew)
        addressListNew = personNew.addressList
        println(".....addressListNew:" + addressListNew)

        User user = new User(id: 100, firstName: "Arockia", lastName: "Arul Sekar")
        user.address = new Address(city: "trichy", district: "Trichy");
        println(".....user:" + user)
        def userJsonStr1 = objectMapper.writeValueAsString(user)
        def userNew1 = objectMapper.readValue(userJsonStr1, User.class)
        expect:
        addressListNew.size() == 2
        personNew.addressList.get(0).person != null
        personNew.addressList.get(1).person != null
        personNew.addressList.get(0).person.id == personNew.addressList.get(1).person.id
        personNew.addressList.get(0).country != null
        personNew.addressList.get(0).country.get(0).code == "IN"
        personNew.addressList.get(1).country.get(0).code == "US"
        userNew1.address.user1 != null
        userNew1.address.user1.id == 100
    }

}

@SpringBootApplication
@EnableJpaRepositories
@EnableDijtaBase
class MappedParentApp {

    static void main(String[] args) {
        SpringApplication.run(MappedParentApp.class, args)
    }
}