package by.issoft.opsapp.issue

import by.issoft.opsapp.dto.Issue
import by.issoft.opsapp.dto.Project
import by.issoft.opsapp.service.IssueService
import by.issoft.opsapp.service.ProjectService
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class IssueSpec extends Specification {

    @Autowired
    MockMvc mockMvc

    @PersistenceContext
    EntityManager entityManager

    @Autowired
    ProjectService projectService

    def "saving the issue"() {
        given: "This regex should verify header 'Location' in the response"
        def regex = /\/issues\/[1-9]{1}[0-9]*/

        and:
        def projectId = projectService.saveProject(anyProject())
        flushAndClear()

        and:
        def issue = anyIssue(projectId)

        when:
        def response = mockMvc.perform(post('/issues')
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(toJson(issue))).andReturn().response

        then:
        with(response) {
            status == HttpStatus.CREATED.value()
            getHeader("Location") ==~ regex
        }
    }

    def "saving the project with invalid fields"() {
        given:
        def fakeProjectId = Integer.MIN_VALUE

        and:
        def issue = anyIssue(fakeProjectId)

        when:
        def response = mockMvc.perform(post('/projects')
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(toJson(issue))).andReturn().response

        then:
        response.status == HttpStatus.BAD_REQUEST.value()
    }

    def anyIssue(Integer projectId) {
        Issue issue = new Issue()
        issue.setDescription(UUID.randomUUID().toString())
        issue.setProjectId(projectId)
        return issue
    }

    def anyProject() {
        return new Project(
                0,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                ThreadLocalRandom.current().nextInt(100))
    }

    def flushAndClear() {
        entityManager.flush()
        entityManager.clear()
    }

}
