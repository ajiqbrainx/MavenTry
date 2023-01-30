package com.dijta.common.dijtasequence.transaction


import com.dijta.common.identity.BaseEntityController
import com.dijta.common.security.ISecurityContext
import com.google.common.collect.ImmutableList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController extends BaseEntityController<UserDto, UserEntity> {

    @Autowired
    private IUserService userService

    protected UserController(IUserService service, ISecurityContext securityContext) {
        super(service, securityContext, buildHttpSecurityPrivilege())
    }

    @GetMapping(path = "/dijta-sequence-transaction-with-entity")
    ImmutableList<UserEntity> findAll() {
        userService.findAllUsers()
    }

    @PostMapping("/dijta-sequence-transaction-with-dto-child")
    @PreAuthorize("hasAuthority('DIJTA_APP_WRITE')")
    UserDto save(@RequestBody final UserDto dto) {
        return userService.saveDto(dto)
    }

    @GetMapping(path = "/dijta-sequence-transaction-with-dto-child")
    @PreAuthorize("hasAnyAuthority('DIJTA_APP_READ', 'DIJTA_APP_WRITE')")
    List<UserDto> getAll() {
        userService.getAll()
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
