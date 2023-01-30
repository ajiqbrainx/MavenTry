package com.dijta.common.service.validation

import com.dijta.common.EnableDijtaBase
import com.dijta.common.TestConstants
import com.dijta.common.auditing.AbstractBaseAuditingEntityService
import com.dijta.common.auditing.BaseAuditingEntity
import com.dijta.common.auditing.BaseAuditingEntityController
import com.dijta.common.auditing.BaseAuditingEntityRepository
import com.dijta.common.auditing.BaseAuditingEntityService
import com.dijta.common.exception.ErrorResponse
import com.dijta.common.identity.DijtaConverters
import com.dijta.common.message.MessageConstants
import com.dijta.common.security.ISecurityContext
import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.RestAssured
import org.apache.http.HttpStatus
import org.dozer.DozerBeanMapper
import org.hibernate.annotations.Where
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import spock.lang.Specification

import javax.persistence.*
import javax.transaction.Transactional

import static io.restassured.RestAssured.given

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [ChildLevelValidationApp])
@ActiveProfiles("local")
class ChildLevelValidationSpec extends Specification implements TestConstants {

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

    def "verify employee with child save validation"() {
        given:
        def token = asJwtWithTenant('DIJTA_APP_WRITE', 1L)

        final CountryDto countryDto = new CountryDto()
        final AddressDto addressDto = new AddressDto()
        addressDto.country = [countryDto]
        final EmployeeDTO dto = new EmployeeDTO()
        dto.firstName = "Ram"
        dto.otherName = ""
        dto.companyName = "name"
        dto.street = "Ramu"
        dto.companyName = "name"
        dto.engineNumber="U3K5C1KE060934"
        dto.chassisNumber="ME3U3K5C1KE959566"
        dto.regNumber="AP00AT888"
        dto.pincode="500035"
        dto.address = [addressDto]

        expect:
        def response = given()
                .contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .body(objectMapper.writeValueAsString(dto))
                .post("/employee_with_child_save")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract()
                .asString()

        def messageCode = objectMapper.readValue(response, ErrorResponse.class)
        messageCode.errors.size() == 8

        messageCode.errors[0].code == MessageConstants.NON_NULL
        messageCode.errors[0].field == 'arg0.address[0].country[0].code'
        messageCode.errors[0].params.size() == 1
        with(messageCode.errors[0].params[0]) {
            code == 'code'
            defaultValue == 'CountryCode'
        }

        messageCode.errors[1].code == MessageConstants.INVALIDEMAILFORMAT
        messageCode.errors[1].field == 'arg0.email'
        messageCode.errors[1].params.size() == 1
        with(messageCode.errors[1].params[0]) {
            code == 'email'
            defaultValue == 'Name'
        }
        messageCode.errors[2].code == MessageConstants.INVALIDGSTFORMAT
        messageCode.errors[2].field == 'arg0.gst'
        messageCode.errors[2].params.size() == 1
        with(messageCode.errors[2].params[0]) {
            code == 'gst'
            defaultValue == 'Name'
        }

        messageCode.errors[3].code == MessageConstants.INVALIDMOBILENUMBERFORMAT
        messageCode.errors[3].field == 'arg0.mobile'
        messageCode.errors[3].params.size() == 1
        with(messageCode.errors[3].params[0]) {
            code == 'mobile'
            defaultValue == 'Name'
        }
        messageCode.errors[4].code == MessageConstants.NON_NULL
        messageCode.errors[4].field == 'arg0.name'
        messageCode.errors[4].params.size() == 1
        with(messageCode.errors[4].params[0]) {
            code == 'EMP_NAME'
            defaultValue == 'Name'
        }
        messageCode.errors[5].code == MessageConstants.LENGTH
        messageCode.errors[5].field == 'arg0.otherName'
        messageCode.errors[5].params.size() == 1
        with(messageCode.errors[5].params[0]) {
            code == 'EMP_OTHER_NAME_LENGTH'
            defaultValue == 'Other Name'
        }
        messageCode.errors[6].code == MessageConstants.NOT_EMPTY
        messageCode.errors[6].field == 'arg0.otherName'
        messageCode.errors[6].params.size() == 1
        with(messageCode.errors[6].params[0]) {
            code == 'EMP_OTHER_NAME'
            defaultValue == 'Other Name'
        }
        messageCode.errors[7].code == MessageConstants.INVALIDPANFORMAT
        messageCode.errors[7].field == 'arg0.panCard'
        messageCode.errors[7].params.size() == 1
        with(messageCode.errors[7].params[0]) {
            code == 'panCard'
            defaultValue == 'Name'
        }
    }

    def "verify employee with child save validation Success"() {
        given:
        def token1 = asJwtWithTenant('DIJTA_APP_WRITE', 1L)

        final CountryDto countryDto1 = new CountryDto()
        countryDto1.code = "IN"
        final AddressDto addressDto1 = new AddressDto()
        addressDto1.city = "Saidapet"
        addressDto1.district = "Chennai"
        addressDto1.country = [countryDto1]
        final EmployeeDTO dto1 = new EmployeeDTO()
        dto1.firstName = "Ram"
        dto1.companyName = "name"
        dto1.name = "Ram"
        dto1.otherName = "Ramu"
        dto1.optionalName = "Opt.Name"
        dto1.gst = "18AABCU9603R1ZM"
        dto1.panCard = "ALWPG5809L"
        dto1.email = "sai@gmail.com"
        dto1.mobile = "9999999999"
        dto1.street = "Ramu"
        dto1.companyName = "name"
        dto1.engineNumber="U3K5C1KE060934"
        dto1.chassisNumber="ME3U3K5C1KE959566"
        dto1.regNumber="AP00AT888"
        dto1.pincode="500035"
        dto1.address = [addressDto1]

        expect:
        def response1 = given()
                .contentType("application/json")
                .when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token1")
                .body(objectMapper.writeValueAsString(dto1))
                .post("/employee_with_child_save")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .asString()

        def employeeDTONew = objectMapper.readValue(response1, EmployeeDTO.class)
        println("employeeDTONew:" + employeeDTONew)
        employeeDTONew.pkId == 1
        employeeDTONew.name == "Ram"
        employeeDTONew.otherName == null // EmployeeEntity is not having this field.
        employeeDTONew.address[0].pkId == 1
        employeeDTONew.address[0].city == "Saidapet"
        employeeDTONew.address[0].district == "Chennai"
        employeeDTONew.address[0].country[0].pkId == 1
        employeeDTONew.address[0].country[0].code == "IN"
    }

}

@Entity
@Where(clause = "deleted_at is null")
public class EmployeeEntity extends BaseAuditingEntity {

    private String name;

    private boolean isActive;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<AddressEntity> address;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean getIsActive() { return isActive; }

    public void setIsActive(final boolean isActiveBol) {
        this.isActive = isActiveBol;
    }

    public List<AddressEntity> getAddress() {
        return address;
    }

    public void setAddress(List<AddressEntity> address) {
        this.address = address;
    }

}

@Entity
@Where(clause = "deleted_at is null")
public class AddressEntity extends BaseAuditingEntity {

    private String city;
    private String district;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_pk_id", referencedColumnName = "pk_id")
    private EmployeeEntity employee;

    @OneToMany(mappedBy = "address", cascade = CascadeType.ALL)
    private List<CountryEntity> country;

    public List<CountryEntity> getCountry() {
        return country;
    }

    public void setCountry(List<CountryEntity> country) {
        this.country = country;
    }


    public EmployeeEntity getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeEntity employee) {
        this.employee = employee;
    }

    public AddressEntity() {

    }

    public AddressEntity(String city, String district) {
        this.city = city;
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

}

@Entity
@Where(clause = "deleted_at is null")
public class CountryEntity extends BaseAuditingEntity {

    private String code;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_pk_id", referencedColumnName = "pk_id")
    private AddressEntity address;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

}

@Repository
interface EmployeeChildRepository extends BaseAuditingEntityRepository<EmployeeEntity> {}

public interface IEmployeeWithChildService extends BaseAuditingEntityService<EmployeeDTO, EmployeeEntity> {}

@Service
@Transactional
class EmployeeWithChildServiceImpl extends AbstractBaseAuditingEntityService<EmployeeDTO, EmployeeEntity> implements IEmployeeWithChildService {

    protected EmployeeWithChildServiceImpl(EmployeeChildRepository repository,
                                           ISecurityContext securityContext,
                                           DozerBeanMapper mapper) {
        super(repository, DijtaConverters.converter(mapper, EmployeeDTO.class, EmployeeEntity.class))
    }

    protected EmployeeEntity getEntityForExample() {
        return new EmployeeEntity()
    }
}

@RestController
class EmployeeWithChildSaveController extends BaseAuditingEntityController<EmployeeDTO, EmployeeEntity> {

    @Autowired
    private IEmployeeWithChildService employeeService

    protected EmployeeWithChildSaveController(IEmployeeWithChildService service, ISecurityContext securityContext) {
        super(service, securityContext, buildHttpSecurityPrivilege())
    }

    @PostMapping("/employee_with_child_save")
    @PreAuthorize("hasAuthority('DIJTA_APP_WRITE')")
    public EmployeeDTO save(@RequestBody final EmployeeDTO dto) {
        return employeeService.save(dto)
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


@SpringBootApplication
@EnableDijtaBase
class ChildLevelValidationApp {

    public static void main(String[] args) {
        SpringApplication.run(ChildLevelValidationApp.class, args)
    }

}
