package com.qbrainx.common.security

import io.restassured.RestAssured
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static io.restassured.RestAssured.given

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SecurityTestWebApp)
class CustomSecurityIntegrationWithNoConfigSpec extends Specification implements DownStreamServer {


    @LocalServerPort
    private int localServerPort

    def setup() {
        RestAssured.port = localServerPort
    }

    def cleanup() {
        RestAssured.reset()
        resetServer()
    }


    def setupSpec() {
        startServer()
    }

    def cleanupSpec() {
        stopServer()
    }

    def 'test security with allowed url though security detail not provided and all url is restricted'() {
        setup:

        expect:
        given().contentType("application/json")
                .when()
                .get("/opendata")
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)

    }

    def 'test security with over all no app entry restricted'() {
        setup:
        def token = asJwt('CARRIER', 'CAN_SEE', 'NOTALLOWED')


        expect:
        given().contentType("application/json")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .when()
                .get("/cansee")
                .then()
                .statusCode(HttpStatus.SC_OK)

    }


}
