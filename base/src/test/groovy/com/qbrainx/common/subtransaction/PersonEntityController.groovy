package com.dijta.common.subtransaction

import com.dijta.common.multitenant.BaseMultiTenantEntityController
import com.dijta.common.multitenant.MultiTenantEntityService
import com.dijta.common.security.ISecurityContext
import org.springframework.http.HttpMethod
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/person-entity")
class PersonEntityController extends BaseMultiTenantEntityController<PersonEntityDto, PersonEntity> {

    protected PersonEntityController(PersonEntityService personEntityServiceservice,
                                     ISecurityContext securityContext,
                                     Map<HttpMethod, String> securityExpression) {
        super(personEntityServiceservice, securityContext, buildHttpSecurityPrivilege())
    }

    private static Map<HttpMethod, String> buildHttpSecurityPrivilege() {
        final HashMap<HttpMethod, String> map = new HashMap<>()
        map[HttpMethod.GET] = "hasAnyAuthority('DIJTA_APP_READ', 'DIJTA_APP_WRITE')"
        map[HttpMethod.POST] = "hasAnyAuthority('DIJTA_APP_WRITE')"
        map[HttpMethod.PUT] = "hasAnyAuthority('DIJTA_APP_WRITE')"
        map[HttpMethod.DELETE] = "hasAnyAuthority('DIJTA_APP_READ','DIJTA_APP_DELETE')"
        return map
    }
}
