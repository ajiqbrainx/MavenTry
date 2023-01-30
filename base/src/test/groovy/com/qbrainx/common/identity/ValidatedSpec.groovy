package com.dijta.common.identity

import com.dijta.common.EnableDijtaBase
import com.dijta.common.TestConstants
import io.restassured.RestAssured
import org.dozer.DozerBeanMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import javax.transaction.Transactional
import javax.validation.ConstraintViolationException

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ValidatedAnnotationApp)
@ActiveProfiles("local")
class ValidatedSpec extends Specification implements TestConstants {


    @LocalServerPort
    private int localServerPort

    def setup() {
        RestAssured.port = localServerPort
    }

    def cleanup() {
        RestAssured.reset()
    }

    @Autowired
    private IEmployeeService employeeService

    def "Validated and Valid Annotations"() {
        given: "EmployeeDto object to validate before persist"
        final EmployeeDTO employeeDTO = new EmployeeDTO()
        println("employeeDTO:" + employeeDTO)
        when: "call save method to do validate and persist"
        final EmployeeDTO employeeDTONew = employeeService.save(employeeDTO)
        println("employeeDTONew:" + employeeDTONew)

        then: "validate the persisting data and persisted EmployeeDTO "
        def exception = thrown(ConstraintViolationException)
        final String message = exception.getMessage();
        message == 'save.arg0.name: DJCOMM-NONNULL'
        exception.toString() == "javax.validation.ConstraintViolationException: save.arg0.name: DJCOMM-NONNULL"
        //employeeDTONew.pkId == 1
        //employeeDTONew.sequence == 1
        //employeeDTONew.name == 'Ganesan'
    }

}

@SpringBootApplication
@EnableDijtaBase
public class ValidatedAnnotationApp {

    static void main(String[] args) {
        SpringApplication.run(ValidatedAnnotationApp.class, args)
    }
}

@Repository
interface EmployeeValidationRepository extends BaseEntityRepository<EmployeeEntity> {}

@Service
interface IEmployeeValidationService extends BaseEntityService<EmployeeDTO, EmployeeEntity> {

}

@Service
@Transactional
public class EmployeeValidationServiceImpl extends AbstractBaseEntityService<EmployeeDTO, EmployeeEntity> implements IEmployeeValidationService {

    EmployeeValidationServiceImpl(EmployeeValidationRepository repository, DozerBeanMapper dozerBeanMapper) {
        super(repository, DijtaConverters.converter(dozerBeanMapper, EmployeeDTO.class, EmployeeEntity.class))
    }

}

