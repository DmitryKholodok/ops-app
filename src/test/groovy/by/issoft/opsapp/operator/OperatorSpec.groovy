package by.issoft.opsapp.operator

import by.issoft.opsapp.dto.Operator
import by.issoft.opsapp.service.OperatorService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import java.util.concurrent.ThreadLocalRandom

import static groovy.json.JsonOutput.toJson
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OperatorSpec extends Specification {

    @Autowired
    MockMvc mockMvc

    @PersistenceContext
    EntityManager entityManager

    @Autowired
    OperatorService operatorService

    def 'saving the operator'() {
        given: "This regex should verify header 'Location' in the response"
        def regex = /\/operators\/[1-9]{1}[0-9]*/

        when:
        def response = mockMvc.perform(post('/operators')
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(toJson(anyOperator()))).andReturn().response

        then:
        with(response) {
            status == HttpStatus.CREATED.value()
            getHeader("Location") ==~ regex
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
        def response = mockMvc.perform(post('/operators')
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(toJson(operator))).andReturn().getResponse()

        then:
        response.status == HttpStatus.CONFLICT.value()
    }

    def 'saving the operator with invalid fields'() {

        when:
        def response = mockMvc.perform(post('/operators')
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(toJson(operator))).andReturn().response

        then:
        response.status == HttpStatus.BAD_REQUEST.value()

        where:
        [operator] << [
                [new Operator(-13,"Vlad", true)],
                [new Operator(null,null, true)],
                [new Operator(null,"   ", true)],
                [new Operator(null,"Vlad", null)],
        ]
    }

    def 'getting the operator'() {
        given:
        def operator = anyOperator()

        def operatorId = operatorService.saveOperator(operator)
        flushAndClear()

        when:
        def response = mockMvc.perform(get('/operators/' + operatorId)).andReturn().getResponse()

        then:
        response.status == HttpStatus.OK.value()

        and:
        def operatorFromJson = retrieveOperatorFromJson(response.getContentAsString())
        operatorFromJson.id == operatorId
        operatorFromJson.name == operator.name
        operatorFromJson.isBillable== operator.isBillable
    }

    def 'getting the operator with non-existing id'() {
        given:
        operatorService.saveOperator(anyOperator())
        flushAndClear()

        and:
        def fakeIssueId = Integer.MIN_VALUE

        when:
        def response = mockMvc.perform(get('/issues/' + fakeIssueId)).andReturn().getResponse()

        then:
        response.status == HttpStatus.NOT_FOUND.value()
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

    def retrieveOperatorFromJson(String json) {
        def mapper = new ObjectMapper()
        return mapper.readValue(json, Operator.class)
    }
}
