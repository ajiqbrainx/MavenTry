package com.dijta.common.validation

import com.dijta.common.EnableDijtaBase
import com.dijta.common.TestConstants
import com.dijta.common.exception.ErrorResponse
import com.dijta.common.message.MessageConstants
import com.dijta.common.service.validation.EmployeeDTO
import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.RestAssured
import org.apache.http.HttpStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import spock.lang.Specification

import javax.validation.Valid

import static io.restassured.RestAssured.given

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [ValidationApp])
@ActiveProfiles("local")
class ValidationSpec extends Specification implements TestConstants {

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

    def "verify employee validation"() {
        given:
        def token = asJwtWithTenant('SECURED', 1L)
        final EmployeeDTO dto = new EmployeeDTO()
        dto.firstName = "Ram"
        dto.otherName = ""
        dto.optionalName = "name"
        dto.gst="18AABCU9603R1ZM"
        dto.panCard="ALWPG5809L"
        dto.email="sai@gmail.com"
        dto.mobile="9999999999"
        dto.street = "Ramu"
        dto.companyName = "name"
        dto.engineNumber="U3K5C1KE060934"
        dto.chassisNumber="ME3U3K5C1KE959566"
        dto.regNumber="AP00AT888"
        dto.pincode="500035"
        
        expect:
        def response = given()
            .contentType("application/json")
            .when()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .body(objectMapper.writeValueAsString(dto))
            .post("/employees")
            .then()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .extract()
            .asString()

        def messageCode = objectMapper.readValue(response, ErrorResponse.class)
        messageCode.errors.size() == 3
        messageCode.errors[0].code == MessageConstants.NON_NULL
        messageCode.errors[0].field == 'name'
        messageCode.errors[0].defaultMessage == "DijtaNonNull"
        messageCode.errors[0].params.size() == 1
        with(messageCode.errors[0].params[0]) {
            code == 'EMP_NAME'
            defaultValue == 'Name'
        }
        messageCode.errors[1].code == MessageConstants.LENGTH
        messageCode.errors[1].field == 'otherName'
        messageCode.errors[1].defaultMessage == "DijtaLength"
        messageCode.errors[1].params.size() == 1
        with(messageCode.errors[1].params[0]) {
            code == 'EMP_OTHER_NAME_LENGTH'
            defaultValue == 'Other Name'
        }
        messageCode.errors[2].code == MessageConstants.NOT_EMPTY
        messageCode.errors[2].field == 'otherName'
        messageCode.errors[2].defaultMessage == "DijtaNotBlank"
        messageCode.errors[2].params.size() == 1
        with(messageCode.errors[2].params[0]) {
            code == 'EMP_OTHER_NAME'
            defaultValue == 'Other Name'
        }

    }

    def "verify employee validation with Length"() {
        given:
        def token = asJwtWithTenant('SECURED', 1L)
        final EmployeeDTO dto = new EmployeeDTO()
        dto.firstName = "Ram"
        dto.name = "Ram"
        dto.otherName = "Ra"
        dto.optionalName = "name"
        dto.gst="18AABCU9603R1ZM"
        dto.panCard="ALWPG5809L"
        dto.email="sai@gmail.com"
        dto.mobile="9999999999"
        dto.street = "Ramu"
        dto.companyName = "name"
        dto.engineNumber="U3K5C1KE060934"
        dto.chassisNumber="ME3U3K5C1KE959566"
        expect:
        def response = given()
            .contentType("application/json")
            .when()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .body(objectMapper.writeValueAsString(dto))
            .post("/employees")
            .then()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .extract()
            .asString()

        def messageCode = objectMapper.readValue(response, ErrorResponse.class)
        messageCode.errors.size() == 3
        messageCode.errors[0].code == MessageConstants.LENGTH
        messageCode.errors[0].field == 'otherName'
        messageCode.errors[0].params.size() == 1
        with(messageCode.errors[0].params[0]) {
            code == 'EMP_OTHER_NAME_LENGTH'
            defaultValue == 'Other Name'
        }
    }

    def "verify employee validation with Length for Number Success Case"() {
        given:
        def token = asJwtWithTenant('SECURED', 1L)
        final EmployeeDTO dto = new EmployeeDTO()
        dto.firstName = "Ram"
        dto.name = "Ram"
        dto.otherName = "Ra"
        dto.optionalName = "name"
        dto.empNumber = 1234
        dto.gst="18AABCU9603R1ZM"
        dto.panCard="ALWPG5809L"
        dto.email="sai@gmail.com"
        dto.mobile="9999999999"
        dto.street = "Ramu"
        dto.companyName = "name"
        dto.engineNumber="U3K5C1KE060934"
        dto.chassisNumber="ME3U3K5C1KE959566"
        expect:
        def response = given()
                .contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .body(objectMapper.writeValueAsString(dto))
                .post("/employees")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract()
                .asString()

        def messageCode = objectMapper.readValue(response, ErrorResponse.class)
        messageCode.errors.size() == 3
        messageCode.errors[0].code == MessageConstants.LENGTH
        messageCode.errors[0].field == 'otherName'
        messageCode.errors[0].params.size() == 1
        with(messageCode.errors[0].params[0]) {
            code == 'EMP_OTHER_NAME_LENGTH'
            defaultValue == 'Other Name'
        }
    }

    def "verify employee validation with Length for Number with Min Failure Case"() {
        given:
        def token = asJwtWithTenant('SECURED', 1L)
        final EmployeeDTO dto = new EmployeeDTO()
        dto.firstName = "Ram"
        dto.name = "Ram"
        dto.otherName = "Ra"
        dto.optionalName = "name"
        dto.empNumber = 12
        dto.gst="18AABCU9603R1ZM"
        dto.panCard="ALWPG5809L"
        dto.email="sai@gmail.com"
        dto.mobile="9999999999"
        dto.street = "Ramu"
        dto.companyName = "name"
        dto.engineNumber="U3K5C1KE060934"
        dto.chassisNumber="ME3U3K5C1KE959566"
        expect:
        def response = given()
                .contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .body(objectMapper.writeValueAsString(dto))
                .post("/employees")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract()
                .asString()

        def messageCode = objectMapper.readValue(response, ErrorResponse.class)
        messageCode.errors.size() == 4
        messageCode.errors[0].code == MessageConstants.LENGTH
        messageCode.errors[0].field == 'empNumber'
        messageCode.errors[0].params.size() == 1
        with(messageCode.errors[0].params[0]) {
            code == 'EMP_NUMBER'
            defaultValue == 'Emp Number'
        }
    }

    def "verify employee validation with Length for Number with Max Failure Case"() {
        given:
        def token = asJwtWithTenant('SECURED', 1L)
        final EmployeeDTO dto = new EmployeeDTO()
        dto.firstName = "Ram"
        dto.name = "Ram"
        dto.otherName = "Ra"
        //dto.optionalName = "name"
        dto.empNumber = 1234567
        dto.gst="18AABCU9603R1ZM"
        dto.panCard="ALWPG5809L"
        dto.email="sai@gmail.com"
        dto.mobile="9999999999"
        dto.street = "Ramu"
        dto.companyName = "name"
        dto.engineNumber="U3K5C1KE060934"
        dto.chassisNumber="ME3U3K5C1KE959566"
        expect:
        def response = given()
                .contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .body(objectMapper.writeValueAsString(dto))
                .post("/employees")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract()
                .asString()

        def messageCode = objectMapper.readValue(response, ErrorResponse.class)
        messageCode.errors.size() == 4
        messageCode.errors[0].code == MessageConstants.LENGTH
        messageCode.errors[0].field == 'empNumber'
        messageCode.errors[0].params.size() == 1
        with(messageCode.errors[0].params[0]) {
            code == 'EMP_NUMBER'
            defaultValue == 'Emp Number'
        }
    }

    def "verify employee validation success"() {
        given:
        def token = asJwtWithTenant('DIJTA_APP_WRITE', 1L)
        final EmployeeDTO dto = new EmployeeDTO()
        dto.firstName = "Ram"
        dto.name = "User"
        dto.otherName = "Test"
        dto.optionalName = "name"
        dto.gst="18AABCU9603R1ZM"
        dto.panCard="ALWPG5809L"
        dto.email="sai@gmail.com"
        dto.mobile="9999999999"
        dto.street = "Ramu"
        dto.companyName = "name"
        dto.engineNumber="U3K5C1KE060934"
        dto.chassisNumber="ME3U3K5C1KE959566"
        dto.regNumber="AP00AT888"
        dto.pincode="500035"
        expect:
        def response = given()
            .contentType("application/json")
            .when()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .body(objectMapper.writeValueAsString(dto))
            .post("/employees")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .asString()

        def employee = objectMapper.readValue(response, EmployeeDTO.class)
        employee.name == 'User'
    }

    def "verify employee validation with optionalName as null Success"() {
        given:
        def token = asJwtWithTenant('DIJTA_APP_WRITE', 1L)
        final EmployeeDTO dto = new EmployeeDTO()
        dto.firstName = "Ram"
        dto.name = "Ram"
        dto.otherName = "Ramu"
        dto.optionalName = null
        dto.gst="18AABCU9603R1ZM"
        dto.panCard="ALWPG5809L"
        dto.email="sai@gmail.com"
        dto.mobile="9999999999"
        dto.street = "Ramu"
        dto.companyName = "name"
        dto.engineNumber="U3K5C1KE060934"
        dto.chassisNumber="ME3U3K5C1KE959566"
        dto.regNumber="AP00AT888"
        dto.pincode="500035"
        expect:
        def response = given()
                .contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .body(objectMapper.writeValueAsString(dto))
                .post("/employees")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()

        def employee = objectMapper.readValue(response, EmployeeDTO.class)
        employee.optionalName == null

    }

    def "verify employee validation with optionalName exceeded max length"() {
        given:
        def token = asJwtWithTenant('SECURED', 1L)
        final EmployeeDTO dto = new EmployeeDTO()
        dto.firstName = "Ram"
        dto.name = "Ram"
        dto.otherName = "Ramu"
        dto.optionalName = "testOptionalName"
        dto.gst="18AABCU9603R1ZM"
        dto.panCard="ALWPG5809L"
        dto.email="sai@gmail.com"
        dto.mobile="9999999999"
        dto.street = "Ramu"
        dto.companyName = "name"
        dto.engineNumber="U3K5C1KE060934"
        dto.chassisNumber="ME3U3K5C1KE959566"
        dto.regNumber="AP00AT888"
        dto.pincode="500035"
        expect:
        def response = given()
                .contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .body(objectMapper.writeValueAsString(dto))
                .post("/employees")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract()
                .asString()

        def messageCode = objectMapper.readValue(response, ErrorResponse.class)
        messageCode.errors.size() == 1
        messageCode.errors[0].code == MessageConstants.LENGTH
        messageCode.errors[0].field == 'optionalName'
        messageCode.errors[0].params.size() == 1
        with(messageCode.errors[0].params[0]) {
            code == 'EMP_OPTIONAL_NAME'
            defaultValue == 'Optional Name'
        }

    }

    def "validate gst number"() {
        given:
        def token = asJwtWithTenant('DIJTA_APP_WRITE', 1L)
        final EmployeeDTO dto = new EmployeeDTO()
        dto.firstName = "Ram"
        dto.name = "Ram"
        dto.otherName = "Ramu"
        dto.optionalName = "name"
        dto.gst="18AABCU9603R1ZM"
        dto.mobile="9999999999"
        dto.email="sai@gmail.com"
        dto.panCard="ALWPG5809L"
        dto.street = "Ramu"
        dto.companyName = "name"
        dto.engineNumber="U3K5C1KE060934"
        dto.chassisNumber="ME3U3K5C1KE959566"
        dto.regNumber="AP00AT888"
        dto.pincode="500035"
        expect:
        def response = given()
                .contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .body(objectMapper.writeValueAsString(dto))
                .post("/employees")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()

        def gst = objectMapper.readValue(response, EmployeeDTO.class)
        gst.gst=="18AABCU9603R1ZM"
    }


    def "validate emailId"() {
        given:
        def token = asJwtWithTenant('DIJTA_APP_WRITE', 1L)
        final EmployeeDTO dto = new EmployeeDTO()
        dto.firstName = "Ram"
        dto.name = "Ram"
        dto.otherName = "Ramu"
        dto.optionalName = "name"
        dto.gst="18AABCU9603R1ZM"
        dto.mobile="9999999999"
        dto.panCard="ALWPG5809L"
        dto.email="sai@gmail.com"
        dto.street = "Ramu"
        dto.companyName = "name"
        dto.engineNumber="U3K5C1KE060934"
        dto.chassisNumber="ME3U3K5C1KE959566"
        dto.regNumber="AP00AT888"
        dto.pincode="500035"
        expect:
        def response = given()
                .contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .body(objectMapper.writeValueAsString(dto))
                .post("/employees")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()

        def email = objectMapper.readValue(response, EmployeeDTO.class)
        email.email=="sai@gmail.com"
    }


    def "validate panCard"() {
        given:
        def token = asJwtWithTenant('DIJTA_APP_WRITE', 1L)
        final EmployeeDTO dto = new EmployeeDTO()
        dto.firstName = "Ram"
        dto.name = "Ram"
        dto.otherName = "Ramu"
        dto.optionalName = "name"
        dto.gst="18AABCU9603R1ZM"
        dto.mobile="9999999999"
        dto.email="sai@gmail.com"
        dto.panCard="ALWPG5809L"
        dto.street = "Ramu"
        dto.companyName = "name"
        dto.engineNumber="U3K5C1KE060934"
        dto.chassisNumber="ME3U3K5C1KE959566"
        dto.regNumber="AP00AT888"
        dto.pincode="500035"
        expect:
        def response = given()
                .contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .body(objectMapper.writeValueAsString(dto))
                .post("/employees")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()

        def panCard = objectMapper.readValue(response, EmployeeDTO.class)
        panCard.panCard=="ALWPG5809L"
    }

    def "validate mobileNumber"() {
        given:
        def token = asJwtWithTenant('DIJTA_APP_WRITE', 1L)
        final EmployeeDTO dto = new EmployeeDTO()
        dto.firstName = "Ram"
        dto.name = "Ram"
        dto.otherName = "Ramu"
        dto.optionalName = "name"
        dto.panCard="ALWPG5809L"
        dto.gst="18AABCU9603R1ZM"
        dto.email="sai@gmail.com"
        dto.mobile="9999999999"
        dto.panCard="ALWPG5809L"
        dto.street = "Ramu"
        dto.companyName = "name"
        dto.engineNumber="U3K5C1KE060934"
        dto.chassisNumber="ME3U3K5C1KE959566"
        dto.regNumber="AP00AT888"
        dto.pincode="500035"
        expect:
        def response = given()
                .contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .body(objectMapper.writeValueAsString(dto))
                .post("/employees")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()

        def mobile = objectMapper.readValue(response, EmployeeDTO.class)
        mobile.mobile=="9999999999"
    }
    
    
    def "validate regex"() {
        given:
        def token = asJwtWithTenant('DIJTA_APP_WRITE', 1L)
        final EmployeeDTO dto = new EmployeeDTO()
        dto.firstName = "Ram"
        dto.name = "sai"
        dto.otherName = "sai"
        dto.optionalName = "name"
        dto.street = "street"
        dto.companyName = "name"
        dto.engineNumber="U3K5C1KE060934"
        dto.chassisNumber="ME3U3K5C1KE959566"
        dto.email="sai@gmail.com"
        dto.mobile="9999999999"
        dto.panCard="ALWPG5809L"
        dto.gst="18AABCU9603R1ZM"
        dto.pincode="500035"
        dto.regNumber="AP00AT888"
        expect:
        def response = given()
                .contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .body(objectMapper.writeValueAsString(dto))
                .post("/employees")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()

        def valid = objectMapper.readValue(response, EmployeeDTO.class)
        valid.firstName=="Ram"
        valid.otherName=="sai"
        valid.optionalName=="name"
        valid.mobile=="9999999999"
        valid.name=="sai"
        valid.street=="street"
        valid.companyName=="name"
        valid.engineNumber=="U3K5C1KE060934"
        valid.chassisNumber=="ME3U3K5C1KE959566"
        valid.email=="sai@gmail.com"
        valid.panCard=="ALWPG5809L"
        valid.gst=="18AABCU9603R1ZM"
        valid.pincode=="500035"
        valid.regNumber=="AP00AT888"
    }
    
    def "non valid gst number"() {
        given:
        def token = asJwtWithTenant('DIJTA_APP_WRITE', 1L)
        final EmployeeDTO dto = new EmployeeDTO()
        dto.firstName = "Ram"
        dto.name = "Ram"
        dto.otherName = "Ramu"
        dto.optionalName = "name"
        dto.gst="123456789"
        dto.street = "Ramu"
        dto.companyName = "name"
        dto.engineNumber="U3K5C1KE060934"
        dto.chassisNumber="ME3U3K5C1KE959566"
        dto.email="sai@gmail.com"
        dto.mobile="9999999999"
        dto.panCard="ALWPG5809L"
        dto.pincode="500035"
        dto.regNumber="AP00AT888"
        expect:
        def response = given()
                .contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .body(objectMapper.writeValueAsString(dto))
                .post("/employees")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract()
                .asString()

        def errorCode = objectMapper.readValue(response, ErrorResponse.class)
        
    }


    def "non valid emailId"() {
        given:
        def token = asJwtWithTenant('DIJTA_APP_WRITE', 1L)
        final EmployeeDTO dto = new EmployeeDTO()
        dto.name = "Ram"
        dto.otherName = "Ramu"
        dto.optionalName = "name"
        dto.email="saigmail.com"
        expect:
        def response = given()
                .contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .body(objectMapper.writeValueAsString(dto))
                .post("/employees")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract()
                .asString()

        def errorCode = objectMapper.readValue(response, ErrorResponse.class)
        
    }


    def "non valid panCard"() {
        given:
        def token = asJwtWithTenant('DIJTA_APP_WRITE', 1L)
        final EmployeeDTO dto = new EmployeeDTO()
        dto.name = "Ram"
        dto.otherName = "Ramu"
        dto.optionalName = "name"
        dto.panCard="12345678"
        expect:
        def response = given()
                .contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .body(objectMapper.writeValueAsString(dto))
                .post("/employees")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract()
                .asString()

        def errorCode = objectMapper.readValue(response, ErrorResponse.class)
        
    }

    def "non valid mobileNumber"() {
        given:
        def token = asJwtWithTenant('DIJTA_APP_WRITE', 1L)
        final EmployeeDTO dto = new EmployeeDTO()
        dto.name = "Ram"
        dto.otherName = "Ramu"
        dto.optionalName = "name"
        dto.mobile="999999999"
        expect:
        def response = given()
                .contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .body(objectMapper.writeValueAsString(dto))
                .post("/employees")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract()
                .asString()

        def errorCode = objectMapper.readValue(response, ErrorResponse.class)
        
    }
}

@RestController
class EmployeeController {

    @PostMapping("/employees")
    @PreAuthorize("hasAuthority('DIJTA_APP_WRITE')")
    public EmployeeDTO save(@Valid @RequestBody final EmployeeDTO dto) {
        return dto
    }
}

@SpringBootApplication
@EnableDijtaBase
class ValidationApp {

    public static void main(String[] args) {
        SpringApplication.run(ValidationApp.class, args)
    }
}
