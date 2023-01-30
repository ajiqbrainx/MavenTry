package com.dijta.common.filtermultitenant

import be.janbols.spock.extension.dbunit.DbUnit
import com.dijta.common.EnableDijtaBase
import com.dijta.common.TestConstants
import com.dijta.common.identity.BaseEntityController
import com.dijta.common.identity.DijtaConverters
import com.dijta.common.identity.PaginationRequest
import com.dijta.common.multitenant.AbstractMultiTenantEntityService
import com.dijta.common.multitenant.MultiTenantEntityRepository
import com.dijta.common.multitenant.MultiTenantEntityService
import com.dijta.common.security.ISecurityContext
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.json.JsonSlurper
import io.restassured.RestAssured
import org.apache.http.HttpStatus
import org.dozer.DozerBeanMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.data.domain.Page
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.repository.query.QueryByExampleExecutor
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import spock.lang.Ignore
import spock.lang.Specification
import javax.sql.DataSource

import static io.restassured.RestAssured.given

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [MultitenantEmployeeApp])
@ActiveProfiles("local")
class MultiTenantFilterSpec extends Specification implements TestConstants {

    @LocalServerPort
    private int localServerPort

    @Autowired
    DataSource dataSource

    def setup() {
        RestAssured.port = localServerPort
    }

    @DbUnit
    def content = {
        dijta_id(sequence_name: "employeer.pk_id", next_val: 6)
        Employeer(pk_id: 1, name: "Vasanthraj", tenant_id: 1, created_by: 1, created_at: '[NOW]', changed_by: 1, changed_at: '[NOW]')
        Employeer(pk_id: 2, name: "Ganesan", tenant_id: 1, created_by: 1, created_at: '[NOW]', changed_by: 1, changed_at: '[NOW]')
        Employeer(pk_id: 3, name: "Baskar", tenant_id: 1, created_by: 1, created_at: '[NOW]', changed_by: 1, changed_at: '[NOW]')
        Employeer(pk_id: 4, name: "Arun", tenant_id: 2, created_by: 2, created_at: '[NOW]', changed_by: 2, changed_at: '[NOW]')
        Employeer(pk_id: 5, name: "Vijay", tenant_id: 2, created_by: 2, created_at: '[NOW]', changed_by: 2, changed_at: '[NOW]')
    }


    def cleanup() {
        RestAssured.reset()
    }

    @Autowired
    private MultiTenantEmployeeRepository repository

    @Autowired
    private ObjectMapper objectMapper

    def 'filterBased on the Multitenant'() {
        given:
        def token = asJwtWithTenant('DIJTA_APP_READ', 1L)
        expect:
        def response = given().contentType("application/json")
            .when()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .get("/employee")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .asString()

        def responseEmployee = new JsonSlurper().parse(response.getBytes())

        responseEmployee.content.size() == 3
        responseEmployee.content[0].name == "Vasanthraj"
        responseEmployee.content[0].createdAt
        responseEmployee.content[0].tenantId == 1
    }

    def 'filterBased on the Multitenant with Desc'() {
        given:
        def token = asJwtWithTenant('DIJTA_APP_READ', 1L)
        expect:
        def response = given().contentType("application/json")
            .when()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .get("/employee?sortBy=name")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .asString()

        def responseEmployee = new JsonSlurper().parse(response.getBytes())

        responseEmployee.content.size() == 3
        responseEmployee.content[0].name == "Baskar"
        responseEmployee.content[0].createdAt
        responseEmployee.content[0].tenantId == 1
    }

    def 'filterBased on the Multitenant by id'() {
        given:
        def token = asJwtWithTenant('DIJTA_APP_READ', 2L)
        expect:
        def response = given().contentType("application/json")
            .when()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .get("/employee")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .asString()

        def responseEmployee = new JsonSlurper().parse(response.getBytes())
        responseEmployee.content.size() == 2
    }

    def 'fetch Employeer based on the Valid tenantId'() {
        given:
        def token = asJwtWithTenant('DIJTA_APP_READ', 2L)
        expect:
        def response = given().contentType("application/json")
            .when()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .get("/employee/4")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .asString()

        def responseEmployee = new JsonSlurper().parse(response.getBytes())
        responseEmployee.name == "Arun"
    }

    def 'fetch Employeer based on the Valid InvalidtenantId'() {
        given:
        def token = asJwtWithTenant('DIJTA_APP_WRITE', 1L)
        expect:
        def response = given().contentType("application/json")
            .when()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .get("/employee/4")
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .asString()

        def responseEmployee = new JsonSlurper().parse(response.getBytes())
        responseEmployee == null
    }

    def 'delete Employeer based on the Invalid tenantId'() {
        given:
        def token = asJwtWithTenant('DIJTA_APP_DELETE', 1L)
        when:
        def response = given().contentType("application/json")
            .when()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .delete("/employee/4")
            .then()
            .statusCode(HttpStatus.SC_FORBIDDEN)
            .extract()
            .asString()
        then:
        def errorResponse = new JsonSlurper().parse(response.getBytes())
        errorResponse.errors[0].code == "DJCOMM-FRBDN-ACTN"
        errorResponse.errors[0].defaultMessage == "Forbidden Action: {}"
        errorResponse.errors[0].type == "ERROR"
        errorResponse.errors[0].params[0].defaultValue == "Requested Tenant user can't perform the action intended"
    }

    def 'delete Employeer based on the Invalid Tenant ID with DTO'() {
        given:
        def token = asJwtWithTenant('DIJTA_APP_DELETE', 1L)
        Employeer employeer = new Employeer()
        employeer.pkId = 4
        employeer.name = "Arun"
        employeer.tenantId = 2
        when:
        def response = given().contentType("application/json")
            .when()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .body(objectMapper.writeValueAsString(employeer))
            .delete("/employee")
            .then()
            .statusCode(HttpStatus.SC_FORBIDDEN)
            .extract()
            .asString()
        then:
        def errorResponse = new JsonSlurper().parse(response.getBytes())
        errorResponse.errors[0].code == "DJCOMM-FRBDN-ACTN"
        errorResponse.errors[0].defaultMessage == "Forbidden Action: {}"
        errorResponse.errors[0].type == "ERROR"
        errorResponse.errors[0].params[0].defaultValue == "Requested Tenant user can't perform the action intended"
    }

    @Ignore//JsonProperty added to restrict tenant id.
    def 'save Employeer based on the Invalid Tenant ID with DTO'() {
        given:
        def token = asJwtWithTenant('DIJTA_APP_WRITE', 1L)
        Employeer employer = new Employeer()
        employer.setName("Ganesan")
        employer.setTenantId(2L)
        when:
        def response = given().contentType("application/json")
            .when()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .body(objectMapper.writeValueAsString(employer))
            .post("/employee")
            .then()
            .statusCode(HttpStatus.SC_FORBIDDEN)
            .extract()
            .asString()
        then:
        def errorResponse = new JsonSlurper().parse(response.getBytes())
        errorResponse.errors[0].code == "Forbidden Action"
        errorResponse.errors[0].type == "ERROR"
        errorResponse.errors[0].params[0].defaultValue == "Requested Tenant user can't perform the action intended"
    }

    def 'update Employeer based on the Invalid Tenant ID with DTO'() {
        given:
        def token = asJwtWithTenant('DIJTA_APP_WRITE', 1L)
        Employeer employeer = new Employeer()
        employeer.pkId = 4
        employeer.name = "Vijay"
        employeer.tenantId = 2
        when:
        def response = given().contentType("application/json")
            .when()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .body(objectMapper.writeValueAsString(employeer))
            .put("/employee")
            .then()
            .statusCode(HttpStatus.SC_FORBIDDEN)
            .extract()
            .asString()
        then:
        def errorResponse = new JsonSlurper().parse(response.getBytes())
        errorResponse.errors[0].code == "DJCOMM-FRBDN-ACTN"
        errorResponse.errors[0].defaultMessage == "Forbidden Action: {}"
        errorResponse.errors[0].type == "ERROR"
        errorResponse.errors[0].params[0].defaultValue == "Requested Tenant user can't perform the action intended"
    }

    def 'fetch Employeer based on the Admin Privilege'() {
        given:
        def token = asJwtWithTenant('DIJTA_ACCA_READ', 1L)
        when:
        def response = given().contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .get("/employee/all/2")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()
        then:
        def responseEmployee = new JsonSlurper().parse(response.bytes)
        responseEmployee.content.size() == 2
        responseEmployee.content[0].name == "Arun"
        responseEmployee.content[0].createdAt
    }

    def 'fetch Employeer based on the Admin Privilege Invalid Tenant'() {
        given:
        def token = asJwtWithTenant('DIJTA_ACCA_READ', 1L)
        when:
        def response = given().contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .get("/employee/all/100")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()
        then:
        def responseEmployee = new JsonSlurper().parse(response.bytes)
        responseEmployee.content.size() == 0
    }

    def 'fetch All Employeer based on the Admin Privilege'() {
        given:
        def token = asJwtWithTenant('DIJTA_ACCA_READ', 1L)
        when:
        def response = given().contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .get("/employee/all")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()
        then:
        def responseEmployee = new JsonSlurper().parse(response.bytes)
        responseEmployee.content.size() == 5
        with(responseEmployee.content[0]) {
            name == "Vasanthraj"
        }
    }

    def 'fetch All Employeer in tenant id 1 and 2'() {
        given:
        def token = asJwtWithTenant('DIJTA_ACCA_READ', 1L)
        when:
        def response = given().contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .get("/employee/allin")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()
        then:
        def responseEmployee = new JsonSlurper().parse(response.bytes)
        responseEmployee.content.size() == 5
    }
}

interface MultiTenantEmployeeRepository extends MultiTenantEntityRepository<Employeer>, QueryByExampleExecutor<Employeer> {
}

interface IMultiTenantEmployeeService extends MultiTenantEntityService<EmployeerDTO, Employeer> {
    Page<EmployeerDTO> getAllEmployeesByTenantId(PaginationRequest paginationRequest, Long tenantId);

    Page<EmployeerDTO> getAllEmployeesByAdmin(PaginationRequest paginationRequest);

    Page<EmployeerDTO> getAllEmployeesInTenantId(PaginationRequest paginationRequest);
}

@Service
class IMultiTenantEmployeeServiceImpl extends AbstractMultiTenantEntityService<EmployeerDTO, Employeer> implements IMultiTenantEmployeeService {

    private final MultiTenantEmployeeRepository multiTenantEmployeeRepository;
    private final ISecurityContext securityContext;

    protected IMultiTenantEmployeeServiceImpl(MultiTenantEmployeeRepository repository,
                                              DozerBeanMapper mapper,
                                              ISecurityContext securityContext) {
        super(repository, DijtaConverters.converter(mapper, EmployeerDTO.class, Employeer.class), securityContext)
        this.multiTenantEmployeeRepository = repository;
        this.securityContext = securityContext;
    }

    @Override
    protected Employeer getEntityForExample() {
        return new Employeer()
    }

    @Override
    public Page<EmployeerDTO> getAllEmployeesByTenantId(PaginationRequest paginationRequest, final Long tenantId) {
        securityContext.tenantId = tenantId;
        return super.getAll(paginationRequest).map(this.converter.&convertEntityToVo);
    }

    @Override
    Page<EmployeerDTO> getAllEmployeesByAdmin(PaginationRequest paginationRequest) {
        securityContext.disableMultiTenantFilter();
        return super.getAll(paginationRequest).map(this.converter.&convertEntityToVo);
    }

    @Override
    Page<EmployeerDTO> getAllEmployeesInTenantId(PaginationRequest paginationRequest) {
        securityContext.setTenantIds(1L, 2L);
        return super.getAll(paginationRequest).map(this.converter.&convertEntityToVo);
    }
}

@RestController
@RequestMapping("/employee")
class MultitenantController extends BaseEntityController<EmployeerDTO, Employeer> {

    private final IMultiTenantEmployeeService iMultiTenantEmployeeService;

    protected MultitenantController(IMultiTenantEmployeeService service,
                                    ISecurityContext securityContext) {
        super(service, securityContext, buildHttpSecurityPrivilege());
        this.iMultiTenantEmployeeService = service;
    }

    @GetMapping("/all/{tenantId}")
    public Page<EmployeerDTO> getAllEmployeesByAdminUsingTenantId(final PaginationRequest paginationRequest, @PathVariable final Long tenantId) {
        return iMultiTenantEmployeeService.getAllEmployeesByTenantId(paginationRequest, tenantId);
    }

    @GetMapping("/all")
    public Page<EmployeerDTO> getAllEmployeesByAdmin(final PaginationRequest paginationRequest) {
        return iMultiTenantEmployeeService.getAllEmployeesByAdmin(paginationRequest);
    }

    @GetMapping("/allin")
    public Page<EmployeerDTO> getAllEmployeesInTenantId(final PaginationRequest paginationRequest) {
        return iMultiTenantEmployeeService.getAllEmployeesInTenantId(paginationRequest);
    }

    private static Map<HttpMethod, String> buildHttpSecurityPrivilege() {
        final HashMap<HttpMethod, String> map = new HashMap<>();
        map[HttpMethod.GET] =  "hasAnyAuthority('DIJTA_APP_READ', 'DIJTA_APP_WRITE')";
        map[HttpMethod.POST] = "hasAnyAuthority('DIJTA_APP_WRITE')";
        map[HttpMethod.PUT] = "hasAnyAuthority('DIJTA_APP_WRITE')";
        map[HttpMethod.DELETE] = "hasAnyAuthority('DIJTA_APP_READ','DIJTA_APP_DELETE')";
        return map;
    }
}

@SpringBootApplication
@EnableJpaRepositories
@EnableDijtaBase
class MultitenantEmployeeApp {
    static void main(String[] args) {
        SpringApplication.run(MultitenantEmployeeApp.class, args)
    }
}
