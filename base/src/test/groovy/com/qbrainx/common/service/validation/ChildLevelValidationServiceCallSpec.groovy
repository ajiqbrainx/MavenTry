package com.dijta.common.service.validation

import com.dijta.common.EnableDijtaBase
import com.dijta.common.TestConstants
import com.dijta.common.message.MessageCode
import com.dijta.common.validation.DijtaValidations
import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.RestAssured
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import javax.validation.ConstraintViolationException

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [ChildLevelValidationServiceCallApp])
@ActiveProfiles("local")
class ChildLevelValidationServiceCallSpec extends Specification implements TestConstants {

    @LocalServerPort
    private int localServerPort

    def setup() {
        RestAssured.port = localServerPort
    }

    def cleanup() {
        RestAssured.reset()
    }

    @Autowired
    private IEmployeeWithChildService employeeService

    def "Child Level Validation with Direct Service layer API call"() {

        given: "DTO Objects to validate before persist"
        final CountryDto countryDto = new CountryDto()
        //countryDto.code = ""
        final AddressDto addressDto = new AddressDto()
        addressDto.country = [countryDto]

        final EmployeeDTO dto = new EmployeeDTO()
        dto.otherName = ""
        dto.address = [addressDto]
        println("dto"+ dto)
        println("dto.pkId"+ dto.pkId)
        when: "call save method to do validate and persist"
        final EmployeeDTO dtoNew = employeeService.save(dto);
        println("dtoNew"+ dtoNew)
        println("dtoNew.pkId"+ dtoNew.pkId)

        then: "validate the persisting data and persisted DTO "
        def exception = thrown(ConstraintViolationException)
        final String message = exception.getMessage();
        println("------------Message:" + message)
        List<MessageCode> messageCodeList = DijtaValidations.getValidationErrors(exception)
        messageCodeList.stream().forEach({ messageCode -> println(messageCode) })
    }

}

@SpringBootApplication
@EnableDijtaBase
class ChildLevelValidationServiceCallApp {

    public static void main(String[] args) {
        SpringApplication.run(ChildLevelValidationServiceCallApp.class, args)
    }

}
