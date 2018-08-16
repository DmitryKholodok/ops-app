package by.issoft.opsapp.issue

import by.issoft.opsapp.dto.Issue
import by.issoft.opsapp.dto.Project
import by.issoft.opsapp.service.IssueService
import by.issoft.opsapp.service.ProjectService
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
class IssueSpec extends Specification {

    @Autowired
    MockMvc mockMvc

    @PersistenceContext
    EntityManager entityManager

    @Autowired
    ProjectService projectService

    @Autowired
    IssueService issueService

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

    def "saving the project with non-existing project id"() {
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

    def "saving the issue with invalid fields"() {
        when:
        def response = mockMvc.perform(post('/projects')
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(toJson(issue))).andReturn().response

        then:
        response.status == HttpStatus.BAD_REQUEST.value()

        where:
        [issue] << [
                [new Issue(-13, "Do not work all!", 3)],
                [new Issue(null, null, 3)],
                [new Issue(null, "Do not work all!", null)],
                [new Issue(null, "Do not work all!", 0)]
        ]
    }

    def 'getting the issue'() {
        given:
        def projectId = projectService.saveProject(anyProject())
        flushAndClear()

        and:
        def issue = anyIssue(projectId)
        def issueId = issueService.saveIssue(issue)
        flushAndClear()

        when:
        def response = mockMvc.perform(get('/issues/' + issueId)).andReturn().getResponse()

        then:
        response.status == HttpStatus.OK.value()

        and:
        def issueFromJson = retrieveIssueFromJson(response.getContentAsString())
        issueFromJson.id == issueId
        issueFromJson.projectId == issue.projectId
        issueFromJson.description == issue.description
    }

    def 'getting the issue with invalid id'() {
        given:
        def projectId = projectService.saveProject(anyProject())
        flushAndClear()

        and:
        def issue = anyIssue(projectId)
        issueService.saveIssue(issue)
        flushAndClear()

        and:
        def fakeIssueId = Integer.MIN_VALUE

        when:
        def response = mockMvc.perform(get('/issues/' + fakeIssueId)).andReturn().getResponse()

        then:
        response.status == HttpStatus.NOT_FOUND.value()
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

    def retrieveIssueFromJson(String json) {
        def mapper = new ObjectMapper()
        return mapper.readValue(json, Issue.class)
    }
}
