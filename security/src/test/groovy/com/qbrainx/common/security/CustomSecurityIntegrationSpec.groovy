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
@ActiveProfiles("test")
class CustomSecurityIntegrationSpec extends Specification implements DownStreamServer {


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

    def 'test security with authorized and cookies propagated to downstream'() {
        given:
        def token = asJwt('ADMIN', 'SECURED')

        expect:
        given().contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .get("/testauth")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .getBody()
                .asString() == 'Test'

    }

    def 'test security with un authorized'() {
        expect:
        given().contentType("application/json")
                .when()
                .get("/testauth")
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)


    }

    def 'test security with authorized code level'() {
        given:
        def token = asJwt('CARRIER', 'CAN_SEE')
        expect:
        given().contentType("application/json")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .when()
                .get("/cansee")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .getBody()
                .asString() == 'Yes Can see'

    }


    def 'test security with un authorized code level'() {
        given:
        def token = asJwt('ADMIN', 'CANNOTSEE')

        expect:
        given().contentType("application/json")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .when()
                .get("/cansee")
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN)

    }

    def 'test security with over all app entry restricted'() {
        given:
        def token = asJwt('CARRIER', 'CAN_SEE', 'NOTALLOWED')

        expect:
        given().contentType("application/json")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .when()
                .get("/cansee")
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN)

    }

    def 'test security with invalid token'() {

        given:
        def token = "asdf"

        expect:
        given().contentType("application/json")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .when()
                .get("/cansee")
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)

    }


    def 'test security with allowed url though security detail not provided'() {

        expect:
        given().contentType("application/json")
                .when()
                .get("/opendata")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .getBody()
                .asString() == 'Open Data'

    }

    def 'test security logged in user'() {
        given:
        def token = asJwt('ADMIN', 'CAN_SEE')

        expect:
        given().contentType("application/json")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .when()
                .get("/user")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .jsonPath()
                .get("id") == 1L

    }

    def 'calls to downstream services propagates tokens'() {
        given:
        def token = asJwt('ADMIN', 'CAN_SEE')
        wireMockServer.stubFor(get('/services/v1/users')
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("Result")))

        expect:
        given().contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .get("/propagate")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .body()
                .asString() == "Result"

        wireMockServer.verify(getRequestedFor(urlMatching("/services/v1/users"))
                .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer $token")))

    }

    def 'test business error'() {
        given:
        def token = asJwt('ADMIN', 'CAN_SEE')

        expect:
        given().contentType("application/json")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .when()
                .get("/error")
                .then()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)

    }

    def 'test bad request error'() {
        given:
        def token = asJwt('ADMIN', 'CAN_SEE')

        expect:
        given().contentType("application/json")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .when()
                .get("/badrequesterror")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)

    }

    def 'when not logged in but try to access security'() {
        expect:
        given().contentType("application/json")
                .when()
                .get("/notloggedin")
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)

    }


}
