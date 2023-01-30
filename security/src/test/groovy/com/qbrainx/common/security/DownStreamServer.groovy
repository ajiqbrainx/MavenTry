package com.qbrainx.common.security

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.vavr.API

trait DownStreamServer {

    static WireMockServer wireMockServer

    def startServer() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(1234))
        wireMockServer.start()
    }

    def stopServer() {
        wireMockServer.stop()
    }

    def resetServer() {
        wireMockServer.resetAll()
    }

    String asJwt(String role, String previlege, String overAllPrevilage = "TEST_APP_ALLOWED") {
        return SecurityPrincipal.builder().id(1L).name("Test User").privileges(API.Set(previlege, overAllPrevilage)).build().toJwt()
    }
}
