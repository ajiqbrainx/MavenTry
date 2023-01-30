package com.dijta.common.exception

import com.dijta.common.EnableDijtaBase
import com.dijta.common.TestConstants
import com.dijta.common.message.MessageCode
import com.dijta.common.message.MessageParam
import com.dijta.common.security.ISecurityContext
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.json.JsonSlurper
import io.restassured.RestAssured
import org.apache.http.HttpStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.*
import spock.lang.Specification

import javax.validation.Valid

import static io.restassured.RestAssured.given

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [ExceptionGeneratorApp])
class ExceptionGeneratorSpec extends Specification implements TestConstants {
    @LocalServerPort
    private int localServerPort

    @Autowired
    private ObjectMapper objectMapper

    def setup() {
        RestAssured.port = localServerPort
    }

    def cleanup() {
        RestAssured.reset()
    }

    def 'access Denied Exception occurence'() {
        given:
        def token = asJwt('SECURED')
        expect:
        def response = given().contentType("application/json")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .when()
                .get("/exceptions/accessdeniedexception")
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .extract()
                .asString()
        def errorResponse = new JsonSlurper().parse(response.getBytes())
        errorResponse.errors[0].code == "DJCOMM-FRBDN"
    }
    
    

    def 'test generic Exception'() {
        given:
        def token = asJwt('SECURED')

        expect:
        given().contentType("application/json")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .when()
                .get("/exceptions/genericexception")
                .then()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)

    }

    def 'test Djita error'() {
        given:
        def token = asJwt('ADMIN', 'CAN_SEE')
        expect:
        def response = given().contentType("application/json")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .when()
                .get("/exceptions/djitaerror")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract()
                .asString()
        def errorResponse = new JsonSlurper().parse(response.getBytes())
        errorResponse.errors[0].code == "400"
        errorResponse.errors[0].type == "ERROR"
    }

	
	def 'test Djita error with field'() {
		given:
		def token = asJwt('ADMIN', 'CAN_SEE')
		expect:
		def response = given().contentType("application/json")
				.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
				.when()
				.get("/exceptions/djitaerrorwithfield")
				.then()
				.statusCode(HttpStatus.SC_BAD_REQUEST)
				.extract()
				.asString()
		def errorResponse = new JsonSlurper().parse(response.getBytes())
		errorResponse.errors[0].code == "400"
		errorResponse.errors[0].type == "ERROR"
	}
	
	def 'test Djita error with code'() {
		given:
		def token = asJwt('ADMIN', 'CAN_SEE')
		expect:
		def response = given().contentType("application/json")
				.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
				.when()
				.get("/exceptions/djitaerrorwithcode")
				.then()
				.statusCode(HttpStatus.SC_BAD_REQUEST)
				.extract()
				.asString()
		def errorResponse = new JsonSlurper().parse(response.getBytes())
		errorResponse.errors[0].code == "400"
		errorResponse.errors[0].type == "ERROR"
	}

	
	def 'test Djita error with param'() {
		given:
		def token = asJwt('ADMIN', 'CAN_SEE')
		expect:
		def response = given().contentType("application/json")
				.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
				.when()
				.get("/exceptions/djitaerrorwithparam")
				.then()
				.statusCode(HttpStatus.SC_BAD_REQUEST)
				.extract()
				.asString()
		def errorResponse = new JsonSlurper().parse(response.getBytes())
		errorResponse.errors[0].code == "400"
		errorResponse.errors[0].type == "ERROR"
	}
	
	def 'test Djita error code with allattributes'() {
		given:
	   def token = asJwt('ADMIN', 'CAN_SEE')
		expect:
		def response = given().contentType("application/json")
				.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
				.when()
				.get("/exceptions/djitaerrorcodewithallattributes")
				.then()
				.statusCode(HttpStatus.SC_BAD_REQUEST)
				.extract()
				.asString()
		def errorResponse = new JsonSlurper().parse(response.getBytes())
		errorResponse.errors[0].code == "400"
		errorResponse.errors[0].type == "ERROR"
	}
	
	def 'test MethodArgumentNotValidException Exception' () {
		given:
		def token = asJwt('ADMIN', 'CAN_SEE')

		expect:
		def response = given().contentType("application/json")
				.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
				.body(objectMapper.writeValueAsString(new Employee(name: "Ganesan")))
				.post("/exceptions/employee")
				.then()
				.statusCode(HttpStatus.SC_BAD_REQUEST)
				.extract()
				.asString()
		def errorResponse = new JsonSlurper().parse(response.getBytes())
		errorResponse.errors[0].code == "NotNull" || "NotEmpty"

	}

	
	def 'test Djita error code'() {
		given:
		def token = asJwt('ADMIN', 'CAN_SEE')
		expect:
		def response = given().contentType("application/json")
				.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
				.when()
				.get("/exceptions/djitaerrorcode")
				.then()
				.statusCode(HttpStatus.SC_BAD_REQUEST)
				.extract()
				.asString()
		def errorResponse = new JsonSlurper().parse(response.getBytes())
		errorResponse.errors[0].code == "400"
		errorResponse.errors[0].type == "ERROR"
	}
	
	
	def 'test Djita error with code and field'() {
		given:
		def token = asJwt('ADMIN', 'CAN_SEE')
		expect:
		def response = given().contentType("application/json")
				.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
				.when()
				.get("/exceptions/djitaerrorwithcodeandfield")
				.then()
				.statusCode(HttpStatus.SC_BAD_REQUEST)
				.extract()
				.asString()
		def errorResponse = new JsonSlurper().parse(response.getBytes())
		errorResponse.errors[0].code == "400"
		errorResponse.errors[0].type == "ERROR"
	}
	
	
	def 'test Djita error code with param'() {
		given:
		def token = asJwt('ADMIN', 'CAN_SEE')
		expect:
		def response = given().contentType("application/json")
				.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
				.when()
				.get("/exceptions/djitaerrorcodewithparam")
				.then()
				.statusCode(HttpStatus.SC_BAD_REQUEST)
				.extract()
				.asString()
		def errorResponse = new JsonSlurper().parse(response.getBytes())
		errorResponse.errors[0].code == "400"
		errorResponse.errors[0].type == "ERROR"
	}
	
	def 'test Djita error code with code,field and Param'() {
		given:
		def token = asJwt('ADMIN', 'CAN_SEE')
		expect:
		def response = given().contentType("application/json")
				.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
				.when()
				.get("/exceptions/djitaerrorwithcodefieldandparam")
				.then()
				.statusCode(HttpStatus.SC_BAD_REQUEST)
				.extract()
				.asString()
		def errorResponse = new JsonSlurper().parse(response.getBytes())
		errorResponse.errors[0].code == "400"
		errorResponse.errors[0].type == "ERROR"
	}
	
	
}


@SpringBootApplication
@EnableDijtaBase
class ExceptionGeneratorApp {

    static void main(String[] args) {
        SpringApplication.run(ExceptionGeneratorApp.class, args)
    }
}


@RestController
@RequestMapping("/exceptions")
public class ExceptionController {

    protected ExceptionController(ISecurityContext securityContext) {

    }

    


    @GetMapping("/accessdeniedexception")
    public String accessDeniedException() {
        throw new AccessDeniedException("access denied exception")
    }

    @GetMapping("/genericexception")
    public String genericException() {
        throw new Exception("generic exception")
    }

    @GetMapping("/djitaerror")
    public String djitaError() {
        throw new DijtaException(org.springframework.http.HttpStatus.BAD_REQUEST,
                MessageCode.error("400"))
		//throw new DijtaException(org.springframework.http.HttpStatus.UNAUTHORIZED, Message)
    }
	
	@GetMapping("/djitaerrorwithfield")
	public String djitaErrorWithField() {
		throw new DijtaException(org.springframework.http.HttpStatus.BAD_REQUEST, MessageCode.error("400", "djitaError"))
	}
	
	@GetMapping("/djitaerrorwithcode")
	public String djitaErrorWithCode() {
		throw new DijtaException(org.springframework.http.HttpStatus.BAD_REQUEST, MessageCode.error("400", "400", "Bad Request Error"))
	}
	
	@GetMapping("/djitaerrorwithcodeandfield")
	public String djitaErrorWithCodeandField() {
		throw new DijtaException(org.springframework.http.HttpStatus.BAD_REQUEST, MessageCode.error("400", "djitaError", "400", "Bad Request Error"))
	}
	
	@GetMapping("/djitaerrorwithparam")
	public String djitaErrorWithParam() {
		throw new DijtaException(org.springframework.http.HttpStatus.BAD_REQUEST, MessageCode.error("400", new MessageParam("400", "Bad Request Error")))
	}
	
	@GetMapping("/djitaerrorwithcodefieldandparam")
	public String djitaErrorWithCodeFieldandParam() {
		throw new DijtaException(org.springframework.http.HttpStatus.BAD_REQUEST, MessageCode.error("400", "djitaError", new MessageParam("400", "Bad Request Error")))
	}
	
	@GetMapping("/djitaerrorcode")
	public String djitaErrorCode() {
		throw new DijtaException(org.springframework.http.HttpStatus.BAD_REQUEST, "400")
	}

	@GetMapping("/djitaerrorcodewithparam")
	public String djitaErrorCodewithParam() {
		throw new DijtaException(org.springframework.http.HttpStatus.BAD_REQUEST, "400",new MessageParam("400", "Bad Request Error"))
	}
	
	@GetMapping("/djitaerrorcodewithallattributes")
	public String djitaErrorCodewithAllAttributes() {
		throw new DijtaException(org.springframework.http.HttpStatus.BAD_REQUEST, "400","djitaError","400","Bad Request Error")
	}


    @PostMapping("/employee")
    public String(@Valid @RequestBody Employee employee) {
        return "Ok"
    }
}
