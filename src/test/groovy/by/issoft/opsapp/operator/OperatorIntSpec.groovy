package by.issoft.opsapp.operator

import by.issoft.opsapp.dto.Operator
import by.issoft.opsapp.repository.OperatorRepository
import by.issoft.opsapp.service.OperatorService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

import javax.persistence.EntityExistsException
import javax.persistence.EntityManager
import javax.persistence.EntityNotFoundException
import javax.persistence.PersistenceContext
import java.util.concurrent.ThreadLocalRandom

@SpringBootTest
@Transactional
class OperatorIntSpec extends Specification {

    @Autowired
    OperatorService operatorService

    @Autowired
    OperatorRepository operatorRepository

    @PersistenceContext
    EntityManager entityManager

    def 'saving the operator'() {
        given:
        def operator = anyOperator()

        when:
        def operatorId = operatorService.saveOperator(operator)
        flushAndClear()

        then:
        operatorRepository.count() == old(operatorRepository.count()) + 1

        and:
        with(operatorRepository.findById(operatorId).get()) {
            id >= 0
            name == operator.name
            isBillable == operator.isBillable
        }
    }

    def 'saving the operator with existing id'() {
        given:
        def operatorId = operatorService.saveOperator(anyOperator())
        flushAndClear()

        and:
        def operator = anyOperator()
        operator.setId(operatorId)

        when:
        operatorService.saveOperator(operator)
        flushAndClear()

        then:
        thrown EntityExistsException

    }

    def 'getting the operator'() {
        given:
        def operator = anyOperator()

        and:
        def operatorId = operatorService.saveOperator(operator)
        flushAndClear()

        when:
        def operatorFromDb = operatorService.retrieveOperatorById(operatorId)

        then:
        with(operatorFromDb) {
            id == operatorId
            name == operator.name
            isBillable == operator.isBillable
        }
    }

    def 'getting the operator with non-existing id'() {
        given:
        operatorService.saveOperator(anyOperator())
        flushAndClear()

        and:
        def fakeOperatorId = Integer.MIN_VALUE

        when:
        operatorService.retrieveOperatorById(fakeOperatorId)

        then:
        thrown EntityNotFoundException
    }

    def anyOperator() {
        Operator operator = new Operator()
        operator.setName(UUID.randomUUID().toString())
        operator.setIsBillable(ThreadLocalRandom.current().nextBoolean())
        return operator
    }

    def flushAndClear() {
        entityManager.flush()
        entityManager.clear()
    }

}
