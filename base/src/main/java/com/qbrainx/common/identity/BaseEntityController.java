package com.qbrainx.common.identity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.qbrainx.common.security.ISecurityContext;

public abstract class BaseEntityController<V extends BaseDTO, T extends BaseEntity> {

    private final BaseEntityService<V, T> service;
    private final ISecurityContext securityContext;
    private final Map<HttpMethod, String> securityExpression;

    protected BaseEntityController(final BaseEntityService<V, T> service,
                                   final ISecurityContext securityContext,
                                   final Map<HttpMethod, String> securityExpression) {
        this.service = service;
        this.securityContext = securityContext;
        this.securityExpression = securityExpression;
    }

    @Operation(operationId = "getById")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Server Error", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorise", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{id}")
    public Optional<V> getById(@PathVariable final Long id) {
        return securityContext.authorize(securityExpression.get(HttpMethod.GET), () -> service.getById(id));
    }

    @Operation(operationId = "save")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Server Error", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorise", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public V save(@RequestBody final V t) {
        return securityContext.authorize(securityExpression.get(HttpMethod.POST), () -> service.save(t));
    }

    @Operation(operationId = "saveAll")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Server Error", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorise", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/saveAll")
    public List<V> saveAll(@RequestBody final List<V> t) {
        return securityContext.authorize(securityExpression.get(HttpMethod.POST), () -> service.saveAll(t));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Server Error", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorise", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json"))
    })
    @Operation(operationId = "delete")
    @DeleteMapping
    public Boolean delete(@RequestBody final V t) {
        V v = securityContext.authorize(securityExpression.get(HttpMethod.DELETE), () -> service.delete(t));
        if (v != null && v.getPkId() != null && v.getDeletedAt() != null) {
            return true;
        } else {
            return false;
        }
    }

    @Operation(operationId = "update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Server Error", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorise", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json"))
    })
    @PutMapping
    public V update(@RequestBody final V t) {
        return securityContext.authorize(securityExpression.get(HttpMethod.PUT), () -> service.update(t));
    }

    @Operation(operationId = "deleteById")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Server Error", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorise", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/{id}")
    public Boolean deleteById(@PathVariable final Long id) {
        Optional<V> v = securityContext.authorize(securityExpression.get(HttpMethod.DELETE), () -> service.deleteById(id));
        if (v.isPresent() && v.get().getPkId() != null && v.get().getDeletedAt() != null) {
            return true;
        } else {
            return false;
        }
    }

    @Operation(operationId = "deleteAll")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Server Error", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorise", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json")) })
    @DeleteMapping("all")
    public Boolean deleteAll(@RequestBody List<Long> ids) {
        return securityContext.authorize(securityExpression.get(HttpMethod.DELETE),
                () -> service.deleteAll(ids));
    }

    @Operation(operationId = "getAll")
    @GetMapping(consumes = "application/json", produces = "application/json")
    public Page<T> getAll(final PaginationRequest paginationRequest) {
        return securityContext
                .authorize(securityExpression.get(HttpMethod.GET), () -> service.findAll(paginationRequest));
    }

    @Operation(operationId = "count")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Server Error", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorise", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/count")
    public Long count(@RequestParam(required = false) String search) {
        return securityContext.authorize(securityExpression.get(HttpMethod.GET), () -> service.count(Optional.ofNullable(search)));
    }

}
